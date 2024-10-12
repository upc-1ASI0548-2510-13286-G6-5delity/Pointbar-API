package com.metasoft.pointbarmetasoft.dashboardmanagement.application.controllers;
import com.metasoft.pointbarmetasoft.dashboardmanagement.application.dtos.responseDto.DashboardSummaryResponseDto;
import com.metasoft.pointbarmetasoft.dashboardmanagement.application.dtos.responseDto.EmployeeDashboardResponseDto;
import com.metasoft.pointbarmetasoft.dashboardmanagement.application.services.Implements.DashboardServiceImpl;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardServiceImpl dashboardService;
    private final UserRepository userRepository;

    public DashboardController(DashboardServiceImpl dashboardService, UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
    }

    // Endpoint para obtener el resumen del Dashboard
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<?>> getDashboardSummary(Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        DashboardSummaryResponseDto summary = dashboardService.getDashboardSummary(user.getBusiness().getId(), user.getFirstname() + " " + user.getLastname());
        return new ResponseEntity<>(new ApiResponse<>(true, "Dashboard summary retrieved", summary), HttpStatus.OK);
    }

    /*
    // Endpoint para obtener la información de los empleados del negocio
    @GetMapping("/employees")
    public ResponseEntity<ApiResponse<?>> getEmployeeDashboard(Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<EmployeeDashboardResponseDto> employees = dashboardService.getEmployeeDashboard(user.getBusiness().getId());
        return new ResponseEntity<>(new ApiResponse<>(true, "Employee data retrieved", employees), HttpStatus.OK);
    } */
}
