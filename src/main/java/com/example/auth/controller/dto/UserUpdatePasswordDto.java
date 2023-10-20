package com.example.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatePasswordDto {

    private String currentPassword;
    private String newPassword;
    private String token;
}
