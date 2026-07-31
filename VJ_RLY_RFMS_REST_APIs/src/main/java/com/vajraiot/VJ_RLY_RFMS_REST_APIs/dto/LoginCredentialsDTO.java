package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.enums.LoginRoles;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginCredentialsDTO {
    private String username;
    private String password;
    private LoginRoles role;
    private String email;
    private Long phoneNumber;
}
