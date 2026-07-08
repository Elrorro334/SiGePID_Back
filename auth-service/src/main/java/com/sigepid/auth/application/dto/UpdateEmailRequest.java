package com.sigepid.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmailRequest {
    @NotBlank(message = "El nuevo correo es obligatorio")
    @Email(message = "El formato del correo es inválido")
    private String newEmail;
}
