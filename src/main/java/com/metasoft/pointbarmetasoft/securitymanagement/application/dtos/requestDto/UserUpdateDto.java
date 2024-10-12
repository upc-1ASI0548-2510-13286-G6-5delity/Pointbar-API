package com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.requestDto;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
}
