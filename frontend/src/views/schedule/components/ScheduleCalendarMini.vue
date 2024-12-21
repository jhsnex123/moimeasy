<template>
  <Toast position="bottom-right" />
  <ConfirmDialog group="positioned" :modal="false"></ConfirmDialog>
  <Dialog
    v-model:visible="visible"
    :modal="false"
    header="새 일정 "
    :style="{ width: '25rem' }"
    @hide="rejectDialog"
  >
    <span class="add-dialog-subtitle"></span>
    <div class="add-dialog-inputbox">
      <FloatLabel variant="on">
        <InputText
          id="eventTitle"
          class="add-dialog-input"
          autocomplete="off"
          v-model="newEventTitle"
        />
        <label for="eventTitle" class="add-dialog-inputbox-label">일정명</label>
      </FloatLabel>
      <FloatLabel class="float-label-container" variant="on">
        <Select
          v-model="newEventType"
          inputId="eventType"
          :options="types"
          optionLabel="name"
          class="select-style"
        />
        <label for="eventType" class="add-dialog-inputbox-label"
          >일정 타입</label
        >
      </FloatLabel>
      <FloatLabel variant="on">
        <InputText
          id="eventDescription"
          class="add-dialog-input"
          autocomplete="off"
          v-model="newEventDescription"
        />
        <label for="eventDescription" class="add-dialog-inputbox-label"
          >설명</label
        >
      </FloatLabel>
      <FloatLabel variant="on">
        <InputText
          id="eventLocation"
          class="add-dialog-input"
          autocomplete="off"
          v-model="newEventLocation"
        />
        <label for="eventLocation" class="add-dialog-inputbox-label"
          >장소</label
        >
      </FloatLabel>
    </div>
    <template #footer>
      <Button
        label="취소"
        text
        severity="secondary"
        @click="rejectDialog"
        autofocus
      />
      <Button
        label="추가"
        outlined
        severity="secondary"
        @click="resolveDialog"
        autofocus
      />
    </template>
  </Dialog>

  <div class="demo-app">
    <div class="demo-app-main">
      <FullCalendar
        ref="calendarRef"
        class="demo-app-calendar"
        :options="calendarOptions"
      >
        <template v-slot:eventContent="arg">
          <b>{{ arg.timeText }}</b>
          <i>{{ arg.event.title }}</i>
        </template>
      </FullCalendar>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import '@/views/schedule/components/ScheduleCalendarMini.style.css';
import FullCalendar from '@fullcalendar/vue3';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import googleCalendarPlugin from '@fullcalendar/google-calendar';
import { createEventId } from '@/utils/event-utils';
import axios from 'axios';
import { useLoadingStore } from '@/stores/useLoadingStore';
import Toast from 'primevue/toast';
import { useToast } from 'primevue/usetoast';
import { useConfirm } from 'primevue/useconfirm';
import { useAuthStore } from '@/stores/auth';
const auth = useAuthStore();
const toast = useToast();
const primeConfirm = useConfirm();

const types = ref([
  { name: '타입A', type: 'custom-event1' },
  { name: '타입B ', type: 'holiday-event' },
  { name: '타입C', type: 'important-event' },
  { name: '타입D', type: '' },
  { name: '타입E', type: 'custom-event2' },
]);
// 스토어
const loadingStore = useLoadingStore();
// PrimeVue 컴포넌트
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import { fetchAddNotification } from '@/utils/notification-add-utils';

// 구글 캘린더 API 설정
const googleCalendarApiKey = import.meta.env.VITE_GOOGLE_API_KEY;
const googleCalendarId = 'ko.south_korea#holiday@group.v.calendar.google.com';

// Refs 데이터 정의
const visible = ref(false);
const calendarRef = ref(null);
const newEventTitle = ref('');
const newEventDescription = ref('');
const newEventLocation = ref('');
const newEventAttendants = ref('');
const newStartTime = ref('');
const newEndTime = ref('');
const newEventType = ref('');

// Reactive 데이터 정의
const currentEvents = ref([]);
const resolvePromise = ref(null);
const rejectPromise = ref(null);

// Fullcalendar 옵션 설정
const calendarOptions = reactive({
  plugins: [
    dayGridPlugin,
    timeGridPlugin,
    interactionPlugin,
    googleCalendarPlugin,
  ],
  headerToolbar: {
    left: '', // 추후 myCustomButton버튼 추가
    center: 'prev title next',
    right: '',
  },
  buttonText: {
    today: 'Today',
    month: 'Month',
    week: 'Week',
    day: 'Day',
  },
  initialView: 'dayGridMonth',
  // events: [],
  editable: true,
  selectable: true,
  selectMirror: true,
  dayMaxEvents: true,
  weekends: true,
  locale: 'ko',
  googleCalendarApiKey: googleCalendarApiKey,
  dayCellContent: (info) => info.date.getDate(),
  select: handleDateSelect,
  eventClick: handleEventClick,
  eventsSet: handleEvents,
  eventAdd: handleEventAdd,
  eventChange: handleEventChange,
  eventRemove: handleEventRemove,
  // eventSources: [
  //   {
  //     googleCalendarId: googleCalendarId,
  //     className: 'holiday-event',
  //   },
  // ],
  // customButtons: {
  //   myCustomButton: {
  //     text: 'Add',
  //     click: () => {
  //       const calendarApi = calendarRef.value.getApi();
  //       calendarApi.addEvent({
  //         title: 'New Event',
  //         start: new Date(),
  //         end: new Date(),
  //       });
  //       alert('새 이벤트가 추가되었습니다!');
  //     },
  //   },
  // },
});

//아래부터 메서드 정의
async function handleDateSelect(selectInfo) {
  try {
    // 다이얼로그 OK 시 전달되는 일정 데이터
    const eventData = await openDialog(selectInfo);
    if (eventData) {
      const calendarApi = selectInfo.view.calendar;
      calendarApi.addEvent({
        id: createEventId(),
        title: eventData.title,
        start: selectInfo.startStr,
        end: selectInfo.endStr,
        allDay: selectInfo.allDay,
        className: eventData.eventType, // 이벤트 타입 value = 클레스네임
        extendedProps: {
          description: eventData.description || '',
          location: eventData.location || '미정',
          attendants: eventData.attendants || [],
        },
      });
    }
  } catch (error) {
    console.log('다이얼로그에서 입력 취소:', error);
  } finally {
    selectInfo.view.calendar.unselect();
  }
}
// 일정 추가 다이얼로그 오픈
function openDialog(selectInfo) {
  //일정 이벤트 속성 초기화
  visible.value = true;
  newEventTitle.value = '';
  newEventDescription.value = '';
  newEventLocation.value = '';
  newEventAttendants.value = '';
  newEventType.value = '';
  newStartTime.value = selectInfo.startStr;
  newEndTime.value = selectInfo.endStr;

  return new Promise((resolve, reject) => {
    resolvePromise.value = resolve;
    rejectPromise.value = reject;
  });
}

function resolveDialog() {
  if (newEventTitle.value.trim()) {
    const eventData = {
      title: newEventTitle.value.trim(),
      description: newEventDescription.value.trim(),
      location: newEventLocation.value.trim(),
      attendants: newEventAttendants.value
        .split(',')
        .map((attendant) => attendant.trim()),
      eventType: newEventType.value.type,
    };
    resolvePromise.value(eventData);
  } else {
    resolvePromise.value(null);
  }
  closeDialog();
}

function rejectDialog() {
  rejectPromise.value('취소됨');
  closeDialog();
}

function closeDialog() {
  visible.value = false;
}
// 삭제 클릭 이벤트
function handleEventClick(clickInfo) {
  // console.log('삭제 클릭 이벤트', clickInfo.event);
  confirmPosition('top', clickInfo);
}

function handleEvents(events) {
  console.log(events);
  currentEvents.value = events;
}

/**서버에 일정 추가 */
async function handleEventAdd(eventInfo) {
  const event = eventInfo.event; // 캘린더api에 방금 추가된 일정 데이터를 불러옴
  // console.log('이벤트 추가시 호출됨:', event);
  // console.log('이벤트 추가시 호출됨:', event.classNames[0]);
  try {
    const response = await axios.post('/api/v1/events', {
      eventCode: event.id,
      scheduleTitle: event.title,
      startTime: event.start,
      endTime: event.end,
      isAllDayEvent: event.allDay,
      scheduleType: event.classNames[0], //일정에 적용된 클래스네임이 타입으로 들어감
      description: event.extendedProps.description,
      location: event.extendedProps.location,
      attendants: event.extendedProps.attendants,
    });
    console.log('이벤트 추가 성공:', response.data);
    showSuccess();

    // 알림 추가
    fetchAddNotification(
      'new 일정',
      '우리 모임의 새 일정이 등록되었습니다.',
      auth?.user?.moeimId
    );
  } catch (error) {
    console.error('이벤트 추가 실패:', error);
  }
}

async function handleEventChange(eventInfo) {
  // console.log('변경이벤트호출');
  const event = eventInfo.event;
  try {
    const response = await axios.patch(`/api/v1/events/${event.id}`, {
      scheduleTitle: event.title,
      startTime: event.start,
      endTime: event.end,
      isAllDayEvent: event.allDay,
    });
    console.log('이벤트 변경 성공:', response.data);
  } catch (error) {
    console.error('이벤트 변경 실패:', error);
  }
}

async function handleEventRemove(eventInfo) {
  // console.log('삭제이벤트호출');
  const event = eventInfo.event;
  try {
    const response = await axios.delete(`/api/v1/events/${event.id}`);
    console.log('이벤트 삭제 성공:', response.data);
  } catch (error) {
    console.error('이벤트 삭제 실패:', error);
  }
}

async function fetchEvents() {
  loadingStore.startLoading(); // 로딩 스피너
  try {
    const response = await axios.get('/api/v1/events');
    const eventData = response.data.map((event) => ({
      id: event.eventCode,
      title: event.scheduleTitle,
      start: event.startTime,
      end: event.endTime,
      allDay: event.isAllDayEvent,
      className: event.scheduleType,
      extendedProps: {
        description: event.description || '',
        location: event.location || '미정',
        attendants: event.attendants || [],
      },
    }));

    // 구글 캘린더 일정데이터 & DB 일정데이터  eventSources로 추가
    calendarOptions.eventSources = [
      {
        googleCalendarId: googleCalendarId,
        className: 'holiday-event',
      },
      {
        events: eventData, // DB에서 가져온 이벤트 리스트
      },
    ];
  } catch (error) {
    // 데이터베이스에서 가져오기 실패시 구글캘린더 공휴일만 적용
    calendarOptions.eventSources = [
      {
        googleCalendarId: googleCalendarId,
        className: 'holiday-event',
      },
    ];
    console.error('서버에서 이벤트를 가져오는 데 실패했습니다:', error);
  } finally {
    loadingStore.stopLoading(); // 로딩스피너 제거
  }
}
//일정등록 토스트알림
const showSuccess = () => {
  toast.add({
    severity: 'success',
    summary: '일정 등록',
    detail: '새 일정이 등록되었습니다',
    life: 3000,
  });
};
const confirmPosition = (position, clickInfo) => {
  console.error('dddd');
  primeConfirm.require({
    group: 'positioned',
    message: `등록된 일정 '${clickInfo.event.title}'을(를) 삭제하시겠습니까?`,
    header: '일정 삭제',
    icon: 'pi pi-info-circle',
    position: 'top',
    rejectProps: {
      label: '취소',
      severity: 'secondary',
      text: true,
    },
    acceptProps: {
      label: '삭제',
      text: true,
    },
    accept: () => {
      clickInfo.event.remove(); // 캘린더에서 이벤트 제거
      handleEventRemove(clickInfo);
      toast.add({
        severity: 'info',
        summary: '일정삭제',
        detail: '등록된 일정이 삭제되었습니다',
        life: 3000,
      });
    },
    reject: () => {
      toast.add({
        severity: 'error',
        summary: '일정삭제',
        detail: '취소되었습니다',
        life: 3000,
      });
    },
  });
};

// watch 사용
watch(newEventType, (newVal, oldVal) => {
  console.log('newEventType 변경:', newVal);
});
// 컴포넌트 마운트 시 이벤트 fetch로 가져옴
onMounted(() => {
  fetchEvents();
});
</script>

<style scoped>
/* 기존 스타일 유지 */
</style>
