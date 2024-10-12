package com.metasoft.pointbarmetasoft.securitymanagement.application.services;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.requestDto.*;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.responseDto.EmployeeSignInResponseDto;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;

import java.util.List;

public interface IUserService {
    ApiResponse<?> signUpEmployee(EmployeeSignUpRequestDto employeeSignUpRequestDto, Long adminId);
    List<EmployeeSignInResponseDto> getEmployeesByBusinessId(Long businessId);

    ApiResponse<?> updateUser(Long userId, UserUpdateDto userUpdateDto);
    ApiResponse<?> deleteUser(Long userId);

    ApiResponse<?> signUpAdminUser(AdminSignUpRequestDto adminSignUpRequestDto);
    ApiResponse<?> signUpClient(ClientSignUpRequestDto clientSignUpRequestDto);
    ApiResponse<?> signInUser(UserSignInRequestDto userSignInRequestDto);
}
