<template>
  <div class="total-frame">
    <!-- <PayMenuList /> 메뉴 위치 -->
    <!-- 차트 부분 제목과 아이콘을 왼쪽 아래로 배치 -->
    <div class="card" style="height: 100%; width: 100%; margin-left: 50px">
      <!-- <div class=" title">모임명 : {{ moeimName }}</div> -->
      <div class="title">{{ currentMonth }}</div>
      <div class="expenseButton">
        <Button
          label="회비사용"
          icon="pi pi-dollar"
          iconPos="right"
          rounded
          class="pay-button"
          @click="visible1 = true"
        />
      </div>
      <div class="icon-button-group">
        <i
          class="pi pi-caret-left"
          style="font-size: 1rem"
          @click="updateMonth(-1)"
        ></i>
        <i class="pi pi-calendar-times" style="font-size: 1rem"></i>
        <i
          class="pi pi-caret-right"
          style="font-size: 1rem"
          @click="updateMonth(1)"
        ></i>
      </div>
      <div v-if="hasData">
        <Splitter style="width: 90%; height: 80vh; margin: 20px" class="mb-8">
          <SplitterPanel
            :size="50"
            :minSize="30"
            class="flex items-center justify-center full-size-panel"
          >
            <div class="chart-wrapper">
              <Chart
                type="doughnut"
                :data="chartData"
                :options="chartOptions"
                class="chart-container"
                style="width: 100%; height: 100%"
              />
            </div>
          </SplitterPanel>

          <SplitterPanel
            :size="50"
            :minSize="20"
            class="flex items-center justify-center full-size-panel"
          >
            <div class="card datatable-card">
              <DataTable
                :value="categoryData"
                ref="dt"
                class="centered-datatable"
                tableStyle="min-width: 20rem"
              >
                <Column header="비율">
                  <template #body="slotProps">
                    <div style="position: relative; width: 6rem; height: 6rem">
                      <div
                        :style="{
                          backgroundColor: slotProps.data.categoryColor,
                          width: '100%',
                          height: '100%',
                          borderRadius: '50%',
                          display: 'flex',
                          justifyContent: 'center',
                          alignItems: 'center',
                          color: 'white',
                          fontSize: '1rem',
                        }"
                      >
                        <span>{{ slotProps.data.categoryPercentage }}%</span>
                      </div>
                    </div>
                  </template>
                </Column>
                <Column
                  :field="'categoryList'"
                  header="항목"
                  style="font-size: 1.5em"
                >
                  <template #body="slotProps">
                    <span>{{ slotProps.data.categoryList }}</span>
                  </template>
                </Column>
                <Column
                  :field="'categoryMoney'"
                  header="금액"
                  style="font-size: 1.5em"
                >
                  <template #body="slotProps">
                    <span>{{ slotProps.data.categoryMoney }}</span>
                  </template>
                </Column>
              </DataTable>
            </div>
          </SplitterPanel>
        </Splitter>
      </div>
      <div v-else>
        <!-- 데이터가 없을 때 표시 -->
        <p>데이터가 없습니다.</p>
      </div>
    </div>
  </div>
  <div>
    <!-- 회비 사용 모달창 -->
    <Dialog
      v-model:visible="visible1"
      modal
      :style="{ width: '30rem', height: '40rem' }"
      @show="resetValue"
    >
      <template #header>
        <div class="text-header">지출 금액</div>
      </template>
      <div class="text-display">
        <InputText type="text" v-model="value" readonly class="text-input" />
      </div>
      <div class="number-buttons">
        <Button
          v-for="num in numbers"
          :key="num"
          rounded
          class="number-button"
          @click="handleButtonClick(num)"
        >
          <template v-if="num === 'x'">
            <i class="pi pi-times-circle" style="font-size: 1.5rem"></i>
          </template>
          <template v-else>
            {{ num }}
          </template>
        </Button>
      </div>
      <Button label="다음" rounded class="next-button" @click="expenseAmount" />
    </Dialog>

    <!-- 지출 항목 입력 모달창 -->
    <Dialog
      v-model:visible="visible2"
      modal
      :style="{ width: '30rem', height: '30rem' }"
    >
      <template #header>
        <div class="sortButton">
          <SplitButton :model="sortOptions" severity="contrast">
            <span>{{ selectedExpense }}</span>
          </SplitButton>
        </div>
      </template>
      <div class="text-display">
        <InputText type="text" v-model="value" readonly class="text-input" />
      </div>
      <Button label="다음" rounded class="next-button" @click="expenseAmount" />
    </Dialog>

    <!-- 회비 사용 확인 모달 -->
    <Dialog
      v-model:visible="visible3"
      modal
      :style="{ width: '30rem', height: '25rem' }"
    >
      <template #header>
        <div
          style="font-size: 1.2em; margin: auto; margin-top: 30px; color: black"
        >
          <span style="font-weight: bold; color: blue">{{
            selectedExpense
          }}</span>
          항목에 <span style="font-weight: bold; color: blue"> {{ value }}</span
          >원 사용하시겠습니까?
          <p>출금계좌 : {{ moeimBank }}({{ moeimAccount }})</p>
          <p>모임명 : {{ moeimName }}</p>
        </div>
      </template>
      <div class="button-group">
        <Button
          label="확인"
          rounded
          class="next-button"
          @click="confirmExpense"
        />
        <Button label="취소" rounded class="next-button" @click="resetValue" />
      </div>
    </Dialog>

    <!-- 회비 사용 결과 모달 -->
    <Dialog
      v-model:visible="visible4"
      modal
      :style="{ width: '27rem', height: '359rem' }"
      :closable="false"
    >
      <template #header>
        <div
          style="
            text-align: center;
            font-size: 3em;
            margin: auto;
            margin-top: 50px;
            font-weight: bold;
            color: #7f56d9;
          "
        >
          <i
            class="pi pi-minus-circle"
            style="font-size: 8rem; margin-bottom: 20px"
          ></i>
          <div>모임비 지출</div>
        </div>
      </template>
      <div
        style="
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          height: 100%;
        "
      >
        <p style="margin-top: 20px; margin-bottom: 20px">
          <span style="font-weight: bold; color: blue">{{
            selectedExpense
          }}</span>
          항목에 <span style="font-weight: bold; color: blue"> {{ value }}</span
          >원 사용하였습니다.
        </p>
        <Button label="닫기" rounded @click="visible4 = false" />
      </div>
    </Dialog>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import Chart from 'primevue/chart';
import PayMenuList from './TransactionMenuList.vue';
import Button from 'primevue/button';
import Splitter from 'primevue/splitter';
import SplitterPanel from 'primevue/splitterpanel';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Dialog from 'primevue/dialog';
import InputText from 'primevue/inputtext';
import SplitButton from 'primevue/splitbutton';
import axios from 'axios';
import 'primeicons/primeicons.css';
import { useRouter } from 'vue-router';
import { useLoadingStore } from '@/stores/useLoadingStore';

const loadingStore = useLoadingStore();
const router = useRouter();

const visible1 = ref(false); // 지출 금액 입력 모달
const visible2 = ref(false); // 지출 항목 선택 모달
const visible3 = ref(false); // 지출 확인 모달
const visible4 = ref(false); // 결과 모달

// 지출 카테고리 데이터
const value = ref(''); // 입력한 금액
const moeimName = ref(''); // 모임 이름
const moeimBank = ref('카카오뱅크'); // 모임 은행 이름
const moeimAccount = ref(''); // 모임 계좌번호
const selectedExpense = ref('지출항목'); // 기본값 설정
const userName = ref('');
const userAccount = ref('');

const sortOptions = [
  { label: '식비', icon: 'pi pi-apple', command: () => selectExpense('식비') },
  { label: '음료', icon: 'pi pi-bolt', command: () => selectExpense('음료') },
  {
    label: '운영비',
    icon: 'pi pi-shopping-cart',
    command: () => selectExpense('운영비'),
  },
  {
    label: '시설대여',
    icon: 'pi pi-briefcase',
    command: () => selectExpense('시설대여'),
  },
  { label: '기타', icon: 'pi pi-gift', command: () => selectExpense('기타') },
];

// 숫자 버튼 목록
const numbers = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '0', 'x'];
const currentMonth = ref('');
const currentDate = ref(new Date()); // 현재 날짜
const hasData = ref(false); // 데이터 존재 여부

// Local Storage에서 accessToken, user 정보 가져오기
const accessToken = localStorage.getItem('accessToken');
const userRaw = localStorage.getItem('user');
let userId = null;
let moeimId = null;

if (userRaw) {
  const user = JSON.parse(userRaw); // JSON 파싱
  userId = user?.userId || null;
  moeimId = user?.moeimId || null; // moeimId
}

if (!accessToken || !moeimId) {
  console.error('필수 데이터(accessToken 또는 moeimId)가 누락되었습니다.');
  router.push('/login'); // 로그인 화면으로 리디렉션
}

// 버튼 클릭 시 호출
function handleButtonClick(num) {
  if (num === 'x') {
    value.value = value.value.slice(0, -1);
  } else {
    value.value += num;
  }
}

// 백엔드로부터 초기 데이터 가져오기
async function fetchInitialData() {
  loadingStore.startLoading(); // 로딩 시작
  try {
    const accessToken = localStorage.getItem('accessToken');
    const userRaw = localStorage.getItem('user');
    let moeimId = null;

    if (userRaw) {
      const user = JSON.parse(userRaw); // JSON 파싱
      moeimId = user?.moeimId || null; // `moeimId` 키로 가져오기
    }

    if (!accessToken || !moeimId) {
      console.error('필수 데이터(accessToken 또는 moeimId)가 누락되었습니다.');
      router.push('/login'); // 로그인 화면으로 리디렉션
      return;
    }

    const startOfMonth = new Date(
      Date.UTC(currentDate.value.getFullYear(), currentDate.value.getMonth(), 1)
    );
    const endOfMonth = new Date(
      Date.UTC(
        currentDate.value.getFullYear(),
        currentDate.value.getMonth() + 1,
        0
      )
    );

    const response = await axios.get('/api/v1/transaction/category', {
      headers: { Authorization: `Bearer ${accessToken}` },
      params: {
        userId: userId,
        startDate: startOfMonth.toISOString().split('T')[0],
        endDate: endOfMonth.toISOString().split('T')[0],
      },
    });
    if (response.data && Array.isArray(response.data.categories)) {
      moeimName.value = response.data.moeimName;
      moeimAccount.value = response.data.moeimAccount;
      userName.value = response.data.userName;
      userAccount.value = response.data.userAccount;

      categoryData.value = response.data.categories;
      hasData.value = categoryData.value.length > 0; // 데이터 존재 여부 설정
    } else {
      categoryData.value = [];
      hasData.value = false; // 데이터가 없을 경우
      console.error('categoryData가 응답에 없음');
    }

    //차트 데이터 설정
    chartData.value = setChartData(); // categoryData.value 기반으로 chartData 설정
    chartOptions.value = setChartOptions();
    transformChartData(); // chartData 기반 비율 계산

    console.log(response.data);
  } catch (error) {
    hasData.value = false; // 오류 발생 시 데이터가 없다고 설정
    console.error(
      '모임 세부 정보 조회 중 오류 발생:',
      error.response?.data || error.message
    );
  } finally {
    loadingStore.stopLoading(); // 로딩 중지
  }
}

// 다음 버튼 클릭 -> 확인 모달 표시
function expenseAmount() {
  localStorage.setItem('value', JSON.stringify(value.value));
  visible1.value = false;
  visible2.value = true;
}

function selectExpense(option) {
  selectedExpense.value = option;
  visible2.value = false;
  visible3.value = true;
}

// 월 변경 및 데이터 다시 가져오기
const updateMonth = (offset) => {
  currentDate.value.setMonth(currentDate.value.getMonth() + offset);
  currentMonth.value = `${currentDate.value.getMonth() + 1}월 카테고리별 지출내역`;
  // 새로운 기간으로 데이터 재조회
  fetchInitialData();
};

// State 로드
function loadStateFromLocalStorage() {
  const storedVisible = localStorage.getItem('visible1');
  const storedValue = localStorage.getItem('value');

  if (storedVisible !== null) {
    visible1.value = JSON.parse(storedVisible);
  }

  if (storedValue !== null) {
    value.value = JSON.parse(storedValue);
  }
}

watch(visible1, (newVal) => {
  localStorage.setItem('visible1', JSON.stringify(newVal));
});

watch(value, (newVal) => {
  localStorage.setItem('value', JSON.stringify(newVal));
});

// Reset 함수
function resetValue() {
  localStorage.removeItem('value');
  value.value = '';
  selectedExpense.value = '지출항목';

  visible3.value = false;
}

// 지출 확인 버튼 클릭 -> 지출 처리
async function confirmExpense() {
  console.log('확인 버튼 클릭');
  try {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) {
      console.error('Access token이 없습니다.');
      return;
    }

    const userRaw = localStorage.getItem('user');
    let userId = null;

    if (userRaw) {
      const user = JSON.parse(userRaw);
      userId = user?.userId || null;
    }

    if (!userId) {
      console.error('userId가 없습니다.');
      return;
    }

    const response = await axios.put(
      '/api/v1/transaction/withdraw',
      {
        userId: userId,
        accountNumber: moeimAccount.value,
        withdrawName: userName.value,
        amount: Number(value.value),
        categoryName: selectedExpense.value,
      },
      {
        headers: {
          Authorization: accessToken.trim(),
        },
      }
    );
    console.log('요청 데이터:', {
      userId: userId,
      amount: Number(value.value),
    });

    console.log('지출 성공:', response.data);
    visible3.value = false;
    visible4.value = true;

    // 지출 후 데이터 재조회
    fetchInitialData();
  } catch (error) {
    console.error(
      '모임비 지출 중 오류 발생:',
      error.response?.data || error.message
    );
  }
}

onMounted(() => {
  loadStateFromLocalStorage();
  const date = new Date();
  currentMonth.value = `${date.getMonth() + 1}월 카테고리별 지출내역`;
  fetchInitialData(); // 데이터 조회 후 chartData, categoryData 설정
});

const chartOptions = ref({});
const categoryData = ref([]); // 백엔드에서 받아온 카테고리별 데이터
const chartData = ref({});

// chartData 설정: categoryData를 바탕으로 동적 차트 데이터 생성
const setChartData = () => {
  const documentStyle = getComputedStyle(document.body);

  // categoryData.value에 따라 labels, data 동적 생성
  const labels = categoryData.value.map((item) => item.categoryName);
  const amounts = categoryData.value.map((item) => item.categoryMoney);

  // 카테고리 개수에 따라 색상 배열 생성
  //   const backgroundColors = [
  //     documentStyle.getPropertyValue('--p-orange-300'),
  //     documentStyle.getPropertyValue('--p-purple-300'),
  //     documentStyle.getPropertyValue('--p-green-300'),
  //     documentStyle.getPropertyValue('--p-blue-300'),
  //     documentStyle.getPropertyValue('--p-red-300'),
  //   ];

  //   const hoverBackgroundColors = [
  //     documentStyle.getPropertyValue('--p-orange-200'),
  //     documentStyle.getPropertyValue('--p-purple-200'),
  //     documentStyle.getPropertyValue('--p-green-200'),
  //     documentStyle.getPropertyValue('--p-blue-200'),
  //     documentStyle.getPropertyValue('--p-red-200'),
  //   ];
  const backgroundColors = [
    '#C19ED2',
    '#FFA2C3',
    '#FF7CA1',
    '#96C2EE',
    '#68A3EE',
  ];
  const hoverBackgroundColors = [
    '#B58BBF',
    '#FF99BA',
    '#FF7296',
    '#8CB9E8',
    '#62A0E6',
  ];

  // 카테고리 수가 4개보다 많을 수도 있으니 색상을 반복 사용
  const finalBgColors = labels.map(
    (_, i) => backgroundColors[i % backgroundColors.length]
  );
  const finalHoverColors = labels.map(
    (_, i) => hoverBackgroundColors[i % hoverBackgroundColors.length]
  );

  return {
    labels: labels,
    datasets: [
      {
        data: amounts,
        backgroundColor: finalBgColors,
        hoverBackgroundColor: finalHoverColors,
      },
    ],
  };
};

// 차트 옵션 설정
const setChartOptions = () => {
  const documentStyle = getComputedStyle(document.documentElement);
  const textColor = documentStyle.getPropertyValue('--p-text-color');

  return {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        labels: {
          color: textColor,
        },
      },
    },
    cutout: '50%',
  };
};

// chartData를 바탕으로 categoryData에 비율 계산 속성 추가
const transformChartData = () => {
  if (
    chartData.value.datasets &&
    chartData.value.datasets[0] &&
    chartData.value.datasets[0].data
  ) {
    const totalMoney = chartData.value.datasets[0].data.reduce(
      (acc, curr) => acc + curr,
      0
    ); // double 합산
    categoryData.value = chartData.value.labels.map((label, index) => {
      const categoryMoney = chartData.value.datasets[0].data[index];
      const percentage =
        totalMoney > 0 ? ((categoryMoney / totalMoney) * 100).toFixed(2) : 0;
      return {
        categoryColor: chartData.value.datasets[0].backgroundColor[index],
        categoryList: label,
        categoryMoney: categoryMoney, // double 값 그대로
        categoryPercentage: percentage,
      };
    });
  }
};
</script>

<style scoped>
/* 레이아웃 및 정렬 */
.total-frame {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: flex-start;
  padding: 1rem;
  margin: 10px;
  height: 100%;
}

.title {
  font-size: 1.5em;
  font-weight: bold;
  width: 100%;
  display: flex;
  justify-content: center;
  position: relative;
  left: -100px;
  margin-bottom: 20px;
  /* 텍스트를 살짝 왼쪽으로 이동 */
}

.expenseButton {
  display: flex;
  position: relative;
  left: -100px;
}

.icon-button-group {
  display: flex;
  align-items: center;
  gap: 20px;
  /* 아이콘과 버튼 사이 여백 */
}

.chart-container,
.full-size-panel,
.chart-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.number-buttons {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 20px;
}

.number-button {
  width: 70px;
  height: 70px;
  background-color: white;
  color: black;
  font-size: 1.2em;
  border: none;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.number-button:hover {
  background-color: #333333;
}

.number-button:active {
  background-color: #555555;
}

.next-button {
  margin-top: 20px;
  width: 100%;
}

.text-header {
  font-size: 2em;
  margin: 20px 0;
  font-weight: bold;
  color: lightgrey;
  margin: auto;
}

.text-display {
  display: flex;
  justify-content: center;
  margin: 50px;
}

.text-input {
  font-size: 2rem;
  font-weight: bold;
  text-align: center;
  border: none;
  outline: none;
  background: transparent;
  width: 100%;
}

/* 버튼 스타일 */
:deep(.pay-button.p-button) {
  background-color: #7f56d9 !important;
  color: #ffffff !important;
  padding: 0.5rem 1rem !important;
  border-radius: 20px !important;
  font-size: 0.9rem !important;
  margin-right: 50px;
  margin-left: auto;
}

:deep(.p-button-contrast) {
  background: #7f56d9 !important;
  border: 1px solid #7f56d9 !important;
  color: var(--p-button-contrast-color);
}

:deep(.p-button-contrast:hover) {
  background: #5a37a7 !important;
}

:deep(.pay-button.p-button:hover) {
  background-color: #6c44c3 !important;
}

:deep(.pay-button.p-button:active) {
  background-color: #5a37a7 !important;
}

:deep(.pay-button .p-button-label) {
  font-weight: bold !important;
}

:deep(.p-dialog-header) {
  left: 100px;
}

.title-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  width: 100%;
}

.card {
  width: 100%;
}

.text-green-500 {
  color: #10b981;
}

.text-red-500 {
  color: #ef4444;
}

.right-align {
  display: flex;
  justify-content: flex-end;
}

.sortButton {
  justify-self: center;
}
</style>
