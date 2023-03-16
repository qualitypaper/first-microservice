package com.example.accounts.controller.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ForgetRequest {
    private String email;
}
