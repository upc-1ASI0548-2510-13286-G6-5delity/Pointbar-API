package com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.responseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientSignInResponseDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String dni;
    private String phone;
    private String dtype;
}
