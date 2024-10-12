package com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.responseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSignInResponseDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String dtype;
    private boolean isActive;

    public EmployeeSignInResponseDto(Long id, String firstname, String lastname, String email, String phone, String dtype, boolean isActive) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.dtype = dtype;
        this.isActive = isActive;
    }
}
