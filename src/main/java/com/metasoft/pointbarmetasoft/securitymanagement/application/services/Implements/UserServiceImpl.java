package com.metasoft.pointbarmetasoft.securitymanagement.application.services.Implements;
import com.metasoft.pointbarmetasoft.businessmanagement.domain.entities.Business;
import com.metasoft.pointbarmetasoft.businessmanagement.infraestructure.repositories.BusinessRepository;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.requestDto.*;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.responseDto.EmployeeSignInResponseDto;
import com.metasoft.pointbarmetasoft.securitymanagement.application.services.IUserService;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.*;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.enums.ERole;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.jwt.utils.JwtUtils;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.ClientRepository;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.RolRepository;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import com.metasoft.pointbarmetasoft.shared.config.ModelMapperConfig;
import com.metasoft.pointbarmetasoft.shared.exception.ApplicationException;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final BusinessRepository businessRepository;

    private final ModelMapperConfig modelMapperConfig;
    private final RolRepository rolRepository;

    private final PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserServiceImpl(UserRepository userRepository, ClientRepository clientRepository, ModelMapperConfig modelMapperConfig,
                           RolRepository rolRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                           JwtUtils jwtUtils, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.modelMapperConfig = modelMapperConfig;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.businessRepository = businessRepository;
    }

    @Override
    public ApiResponse<?> signUpEmployee(EmployeeSignUpRequestDto employeeSignUpRequestDto, Long adminId){
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (userRepository.existsByEmail(employeeSignUpRequestDto.getEmail())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        var employee = new Employee();
        employee.setFirstname(employeeSignUpRequestDto.getFirstname());
        employee.setLastname(employeeSignUpRequestDto.getLastname());
        employee.setEmail(employeeSignUpRequestDto.getEmail());
        employee.setPassword(passwordEncoder.encode(employeeSignUpRequestDto.getPassword()));
        employee.setPhone(employeeSignUpRequestDto.getPhone());
        employee.setBusiness(admin.getBusiness());

        Role employeeRole = rolRepository.findByRolName(ERole.EMPLOYEE)
                .orElseThrow(() -> new ResourceNotFoundException("Role ADMIN not found"));
        employee.getRoles().add(employeeRole);

        userRepository.save(employee);
        return new ApiResponse<>(true, "Employee registered successfully", null);
    }

    @Override
    public List<EmployeeSignInResponseDto> getEmployeesByBusinessId(Long businessId) {
        List<UserEntity> employees = userRepository.findByBusinessId(businessId);
        List<EmployeeSignInResponseDto> employeeDtos = new ArrayList<>();

        for (UserEntity employee : employees) {
            if (employee instanceof Employee) {
                boolean isActive = ((Employee) employee).isActive();

                EmployeeSignInResponseDto dto = new EmployeeSignInResponseDto(
                        employee.getId(),
                        employee.getFirstname(),
                        employee.getLastname(),
                        employee.getEmail(),
                        ((Employee) employee).getPhone(),
                        "EMPLOYEE",
                        isActive
                );
                employeeDtos.add(dto);
            }
        }
        return employeeDtos;
    }

    @Override
    public ApiResponse<?> signUpAdminUser(AdminSignUpRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            return new ApiResponse<>(false, "Email already exists", null);
        }

        Business business = new Business();
        business.setName(requestDto.getBusinessName());
        businessRepository.save(business);

        Admin adminUser = new Admin();
        adminUser.setFirstname(requestDto.getFirstname());
        adminUser.setLastname(requestDto.getLastname());
        adminUser.setEmail(requestDto.getEmail());
        adminUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        adminUser.setPhone(requestDto.getPhone());
        adminUser.setBusiness(business);  // Asignar el negocio creado

        Role adminRole = rolRepository.findByRolName(ERole.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role ADMIN not found"));
        adminUser.getRoles().add(adminRole);

        userRepository.save(adminUser);
        return new ApiResponse<>(true, "Admin user registered successfully", null);
    }


    @Override
    public ApiResponse<?> updateUser(Long userId, UserUpdateDto userUpdateDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!(user instanceof Employee)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "User is not an employee");
        }

        Hibernate.initialize(user.getRoles());

        if (userUpdateDto.getFirstname() != null) {
            user.setFirstname(userUpdateDto.getFirstname());
        }
        if (userUpdateDto.getLastname() != null) {
            user.setLastname(userUpdateDto.getLastname());
        }
        if (userUpdateDto.getEmail() != null && !user.getEmail().equals(userUpdateDto.getEmail())) {
            if (userRepository.existsByEmail(userUpdateDto.getEmail())) {
                return new ApiResponse<>(false, "Email is already in use", null);
            }
            user.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getPhone() != null) {
            ((Employee) user).setPhone(userUpdateDto.getPhone());
        }

        userRepository.save(user);
        return new ApiResponse<>(true, "User updated successfully", null);
    }

    @Transactional
    @Override
    public ApiResponse<?> deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(false);
        userRepository.save(user);
        return new ApiResponse<>(true, "User deleted successfully", null);
    }

    @Override
    public ApiResponse<?> signUpClient(ClientSignUpRequestDto clientSignUpRequestDto){
        if(userRepository.existsByEmail(clientSignUpRequestDto.getEmail())){
            throw new ApplicationException(HttpStatus.BAD_REQUEST,"Email already exists");
        }

        if(clientRepository.existsByDni(clientSignUpRequestDto.getDni())){
            throw new ApplicationException(HttpStatus.BAD_REQUEST,"Dni already exists");
        }
        clientSignUpRequestDto.setPassword(passwordEncoder.encode(clientSignUpRequestDto.getPassword()));
        var clientUser = modelMapperConfig.modelMapper().map(clientSignUpRequestDto, Client.class);

        Set<Role> roles=new HashSet<>();
        var rol = "CLIENT";
        var role = rolRepository.findByRolName(ERole.valueOf(ERole.class, rol))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        roles.add(role);

        clientUser.setRoles(roles);
        userRepository.save(clientUser);
        return new ApiResponse<>(true, "Client user registered successfully", null);
    }

    @Transactional
    @Override
    public ApiResponse<?> signInUser(UserSignInRequestDto userSingInRequestDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userSingInRequestDto.getEmail(), userSingInRequestDto.getPassword())
        );
        String papa="true";
        return new ApiResponse<>(true, "Admin user logged in successfully", papa);
    }
}
