package com.kosa.moimeasy.user.controller;

import com.kosa.moimeasy.user.dto.UserDTO;
import com.kosa.moimeasy.user.entity.Role;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.service.RoleService;
import com.kosa.moimeasy.user.service.SignUpService;
import com.kosa.moimeasy.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.kosa.moimeasy.common.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = {"http://192.168.5.49:3000"})
//@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SignUpService signUpService;

//    @GetMapping
//    public ResponseEntity<List<UserDTO>> getUsersByMoeimId(@RequestParam(required = false) Long moeimId) {
//        if (moeimId == null) {
//            throw new IllegalArgumentException("moeimId는 필수 파라미터입니다.");
//        }
//
//        List<User> users = userService.findByMoeimId(moeimId);
//        if (users.isEmpty()) {
//            return ResponseEntity.noContent().build(); // 데이터가 없는 경우 204 반환
//        }
//
//        List<UserDTO> userDTOs = users.stream().map(UserDTO::new).toList();
//        return ResponseEntity.ok(userDTOs);
//    }
    //로그인, 회원가입돼서 필요없음
    @PostMapping("/create")
    public User createUser(@RequestBody UserDTO request) {
        Role role = roleService.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + request.getRoleId()));
        return userService.createUser(request, role);
    }
    //요청한 정보만 수정가능
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody UserDTO request) {
        log.info("요청 데이터: {}", request);

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            User updatedUser = userService.updateUser(userId, request);

            if (updatedUser == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("현재 비밀번호가 일치하지 않습니다.");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("userId", updatedUser.getUserId());
            response.put("nickname", updatedUser.getNickname());
            response.put("phone", updatedUser.getPhone());
            response.put("profileImage", updatedUser.getProfileImage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("사용자 정보 수정 중 오류 발생:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }



    //프로필이미지 수정
    @PutMapping("/update/{userId}")
    public User updateProfileImageUser(@PathVariable Long userId, @RequestBody UserDTO request) {
        System.out.println("전달된 데이터 "+userId+" 프로필: "+request.getProfileImage());
        return userService.updateUserProfileImage(userId, request);
    }

    // 닉네임 중복 검사
    @GetMapping("/check-editnickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname) {
        boolean exists = signUpService.isNicknameExists(nickname);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }


    //회원 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        UserDTO userDTO = new UserDTO(user);
        return ResponseEntity.ok(userDTO);
    }


    @GetMapping("/members")
    public ResponseEntity<List<UserDTO>> getMembersByMoeim(
            @RequestParam Long moeimId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername()); // 현재 로그인된 사용자 ID

        List<UserDTO> members = userService.getUsersByMoeimId(moeimId);

        // DTO에 필요한 정보만 설정
        members.forEach(member -> {
            member.setRoleName(member.getRoleName());
            member.setCreateAt(member.getCreateAt());
        });

        return ResponseEntity.ok(members);
    }



}
