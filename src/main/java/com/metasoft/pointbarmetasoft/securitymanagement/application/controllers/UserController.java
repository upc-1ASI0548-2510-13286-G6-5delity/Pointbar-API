package com.metasoft.pointbarmetasoft.securitymanagement.application.controllers;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.requestDto.*;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.responseDto.EmployeeSignInResponseDto;
import com.metasoft.pointbarmetasoft.securitymanagement.application.services.Implements.UserServiceImpl;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.Admin;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import com.metasoft.pointbarmetasoft.shared.exception.ApplicationException;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    public UserController(UserServiceImpl userService, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/admin/sign-up")
    public ResponseEntity<ApiResponse<?>> signUpAdmin(@Valid @RequestBody AdminSignUpRequestDto requestDto) {
        ApiResponse<?> response = userService.signUpAdminUser(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/employee/sign-up")
    public ResponseEntity<ApiResponse<?>> signUpEmployee(@Valid @RequestBody EmployeeSignUpRequestDto employeeSignUpRequestDto, Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetails) {
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            UserEntity admin = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

            var response = userService.signUpEmployee(employeeSignUpRequestDto, admin.getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
    }

    @GetMapping("/employee/findAll")
    public ResponseEntity<ApiResponse<List<EmployeeSignInResponseDto>>> getEmployees(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetails) {
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            UserEntity admin = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

            List<EmployeeSignInResponseDto> employees = userService.getEmployeesByBusinessId(admin.getBusiness().getId());
            return new ResponseEntity<>(new ApiResponse<>(true, "Employees retrieved successfully", employees), HttpStatus.OK);
        } else {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
    }

    @PutMapping("/employee/update/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        var response = userService.updateUser(userId, userUpdateDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/employee/delete/{userId}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long userId) {
        var response = userService.deleteUser(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/client/sign-up")
    public ResponseEntity<ApiResponse<?>> signUpClientUser(@Valid @RequestBody ClientSignUpRequestDto clientSignUpRequestDto) {
        var response = userService.signUpClient(clientSignUpRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<?>> signInAdminUser(@Valid @RequestBody UserSignInRequestDto userSingInRequestDto) {
        ApiResponse<?> response = userService.signInUser(userSingInRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
