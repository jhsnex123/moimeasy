package com.kosa.moimeasy.user.dto;

import com.kosa.moimeasy.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long userId;
    private String userName;
    private String password;
    private String address;
    private String email;
    private String phone;
    private String nickname;
    private LocalDateTime createAt;
    private Long moeimId;
    private Long roleId; // Role ID 추가
    private String roleName; // Role Name 추가

    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.nickname = user.getNickname();
        this.address = user.getAddress();
        this.createAt = user.getCreateAt();
        this.moeimId = user.getMoeimId();
        this.roleId = user.getRole().getRoleId();
        this.roleName = user.getRole().getRoleName();
        //this.roleId = user.getRole() != null ? user.getRole().getRoleName() : null;
    }
}
