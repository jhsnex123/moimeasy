package com.kosa.moimeasy.user.service;

import com.kosa.moimeasy.user.dto.UserDTO;
import com.kosa.moimeasy.user.entity.Role;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findByMoeimId(Long moeimId) {
        return userRepository.findByMoeimId(moeimId);
    }

    public User createUser(UserDTO request, Role role) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword());
        user.setProfileImage(request.getProfileImage());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setNickname(request.getNickname());
        user.setMoeimId(request.getMoeimId());
        user.setRole(role);
        return userRepository.save(user);
    }

    public User updateUser(Long userId, UserDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
            log.info("현재 비밀번호가 제공되지 않았습니다.");
            throw new IllegalArgumentException("현재 비밀번호를 입력해주세요.");
        }

        boolean isPasswordValid = passwordEncoder.matches(request.getCurrentPassword(), user.getPassword());
        if (!isPasswordValid) {
            log.info("현재 비밀번호가 일치하지 않습니다.");
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }


        // Role은 변경하지 않음
        if (request.getUserName() != null) {
            user.setUserName(request.getUserName());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null && !request.getPhone().matches("^\\d{10,11}$")) {
            throw new IllegalArgumentException("전화번호 형식이 잘못되었습니다.");
        }
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            user.setNickname(request.getNickname());
        } else if (request.getNickname() != null) {
            throw new IllegalArgumentException("닉네임은 비어 있을 수 없습니다.");
        }
        if (request.getMoeimId() != null) {
            user.setMoeimId(request.getMoeimId());
        }

        return userRepository.save(user);
    }




    public User updateUserProfileImage(Long userId, UserDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Role은 변경하지 않음
        if (request.getUserName() != null) {
            user.setUserName(request.getUserName());
        }
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getMoeimId() != null) {
            user.setMoeimId(request.getMoeimId());
        }

        return userRepository.save(user);
    }



    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }


    public List<UserDTO> getUsersByMoeimId(Long moeimId) {
        List<User> users = userRepository.findByMoeimId(moeimId);
        return users.stream()
                .map(user -> UserDTO.builder()
                        .userId(user.getUserId())
                        .userName(user.getUserName()) // userName 추가
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .createAt(user.getCreateAt()) // createAt 추가
                        .roleName(user.getRole() != null ? user.getRole().getRoleName() : null) // roleName 추가
                        .moeimId(user.getMoeimId())
                        .build())
                .collect(Collectors.toList());
    }


}
