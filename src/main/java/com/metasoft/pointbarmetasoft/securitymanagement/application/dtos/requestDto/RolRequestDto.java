package com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.requestDto;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.enums.ERole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolRequestDto {
    @NotNull(message = "Role name is required")
    private ERole role;
}
