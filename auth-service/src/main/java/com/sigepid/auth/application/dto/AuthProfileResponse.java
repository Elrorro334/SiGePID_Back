package com.sigepid.auth.application.dto;

import com.sigepid.auth.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthProfileResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
}
