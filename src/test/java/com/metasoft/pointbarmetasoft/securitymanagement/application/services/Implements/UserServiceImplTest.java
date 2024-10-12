package com.metasoft.pointbarmetasoft.securitymanagement.application.services.Implements;

import com.metasoft.pointbarmetasoft.businessmanagement.domain.entities.Business;
import com.metasoft.pointbarmetasoft.businessmanagement.infraestructure.repositories.BusinessRepository;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.requestDto.AdminSignUpRequestDto;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.requestDto.EmployeeSignUpRequestDto;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.requestDto.UserUpdateDto;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.responseDto.EmployeeSignInResponseDto;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.Admin;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.Employee;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.Role;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.enums.ERole;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.RolRepository;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BusinessRepository businessRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUpEmployee() {
        // Arrange
        EmployeeSignUpRequestDto employeeSignUpRequestDto = new EmployeeSignUpRequestDto();
        employeeSignUpRequestDto.setFirstname("Luis");
        employeeSignUpRequestDto.setLastname("Rosales");
        employeeSignUpRequestDto.setEmail("luis@gmail.com");
        employeeSignUpRequestDto.setPassword("31W'IT6U.Jyf");

        UserEntity admin = new UserEntity();
        admin.setId(1L);
        Business business = new Business();
        admin.setBusiness(business);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.existsByEmail("claudio@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("31W'IT6U.Jyf")).thenReturn("encodedPassword");
        when(rolRepository.findByRolName(ERole.EMPLOYEE)).thenReturn(Optional.of(new Role(ERole.EMPLOYEE)));

        // Act
        ApiResponse<?> response = userService.signUpEmployee(employeeSignUpRequestDto, 1L);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("Employee registered successfully", response.getMessage());
        verify(userRepository, times(1)).save(any(Employee.class));
    }


    @Test
    void getEmployeesByBusinessId() {
        // Arrange
        Long businessId = 1L;
        Employee employee = new Employee();
        employee.setFirstname("Jose");
        employee.setLastname("Huamani");
        employee.setEmail("josediego@gmail.com");
        employee.setPhone("923256789");
        employee.setActive(true);

        List<UserEntity> users = new ArrayList<>();
        users.add(employee);
        when(userRepository.findByBusinessId(businessId)).thenReturn(users);

        // Act
        List<EmployeeSignInResponseDto> result = userService.getEmployeesByBusinessId(businessId);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Jose", result.get(0).getFirstname());
        assertEquals("Huamani", result.get(0).getLastname());
        assertEquals("josediego@gmail.com", result.get(0).getEmail());
        assertEquals("923256789", result.get(0).getPhone());
        assertEquals("EMPLOYEE", result.get(0).getDtype());
    }

    @Test
    void signUpAdminUser() {
        // Arrange
        AdminSignUpRequestDto adminSignUpRequestDto = new AdminSignUpRequestDto();
        adminSignUpRequestDto.setEmail("gonzalo@gmail.com");
        adminSignUpRequestDto.setFirstname("Admin");
        adminSignUpRequestDto.setLastname("User");
        adminSignUpRequestDto.setBusinessName("MyBarPointBar");
        adminSignUpRequestDto.setPassword("k6A6%#R@9aw6");

        when(userRepository.existsByEmail("gonzalo@gmail.com")).thenReturn(false);
        when(rolRepository.findByRolName(ERole.ADMIN)).thenReturn(Optional.of(new Role(ERole.ADMIN)));
        when(passwordEncoder.encode("k6A6%#R@9aw6")).thenReturn("encodedPassword");

        // Act
        ApiResponse<?> response = userService.signUpAdminUser(adminSignUpRequestDto);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("Admin user registered successfully", response.getMessage());
        verify(businessRepository, times(1)).save(any(Business.class));
        verify(userRepository, times(1)).save(any(Admin.class));
    }

    @Test
    void updateUser() {
        // Arrange
        Long userId = 1L;
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstname("Sebastian");
        userUpdateDto.setLastname("Cordova");

        Employee employee = new Employee();
        employee.setId(userId);
        employee.setFirstname("Jose");
        employee.setLastname("Huamani");

        when(userRepository.findById(userId)).thenReturn(Optional.of(employee));

        // Act
        ApiResponse<?> response = userService.updateUser(userId, userUpdateDto);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("User updated successfully", response.getMessage());
        verify(userRepository, times(1)).save(employee);
    }

    @Test
    void deleteUser() {
        // Arrange
        Long userId = 1L;
        Employee employee = new Employee();
        employee.setId(userId);
        employee.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(employee));

        // Act
        ApiResponse<?> response = userService.deleteUser(userId);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("User deleted successfully", response.getMessage());
        verify(userRepository, times(1)).save(employee);
        assertFalse(employee.isActive());
    }
}