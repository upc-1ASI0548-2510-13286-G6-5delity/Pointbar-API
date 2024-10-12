package com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.requestDto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequestDto {

    @NotBlank(message = "First name is required")
    private String firstname;

    @NotBlank(message = "Last name is required")
    private String lastname;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotEmpty(message = "Phone number is required")
    @Size(min = 9, max = 9, message = "Phone number must be 9 digits")
    @Pattern(regexp = "^[0-9]{9}$", message = "Phone number must be 9 digits")
    private String phone;
}
