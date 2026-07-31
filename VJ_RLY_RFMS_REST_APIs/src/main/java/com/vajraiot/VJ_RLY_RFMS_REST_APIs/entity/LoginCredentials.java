package com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.enums.LoginRoles;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="Login_Credentials")
@Entity
public class LoginCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="login_user_id")
    private Long id;

    private String username;

    private String password;

    private String email;

    private Long phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private LoginRoles role;

    private LocalDateTime createdAt;
}
