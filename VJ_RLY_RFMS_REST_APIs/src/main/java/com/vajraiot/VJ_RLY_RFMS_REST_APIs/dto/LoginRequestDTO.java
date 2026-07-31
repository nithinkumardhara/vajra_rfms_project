package com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.enums.LoginRoles;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    private String username;
    private String password;
    private LoginRoles role;
}
