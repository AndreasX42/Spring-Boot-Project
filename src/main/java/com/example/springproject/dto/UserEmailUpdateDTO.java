package com.example.springproject.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class UserEmailUpdateDTO {

    @Nonnull
    @NotBlank(message = "email cannot be blank")
    @Email(message = "email must be a valid email address")
    private String email;

}
