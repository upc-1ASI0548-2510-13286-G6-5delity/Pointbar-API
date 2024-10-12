package com.metasoft.pointbarmetasoft.saleshistorymanagement.application.controllers;
import com.metasoft.pointbarmetasoft.saleshistorymanagement.application.dtos.responseDto.SalesHistoryResponseDto;
import com.metasoft.pointbarmetasoft.saleshistorymanagement.application.services.Implements.SalesHistoryServiceImpl;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sales-history")
public class SaleHistoryController {
    private final SalesHistoryServiceImpl saleHistoryService;
    private final UserRepository userRepository;

    public SaleHistoryController(SalesHistoryServiceImpl saleHistoryService, UserRepository userRepository) {
        this.saleHistoryService = saleHistoryService;
        this.userRepository = userRepository;
    }

    @GetMapping("/business")
    public ResponseEntity<ApiResponse<List<SalesHistoryResponseDto>>> getSalesHistoryForBusiness(Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<SalesHistoryResponseDto> salesHistory = saleHistoryService.getSalesHistoryForBusiness(user.getBusiness().getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Sales history retrieved successfully", salesHistory));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<SalesHistoryResponseDto>>> getSalesHistoryForEmployee(@PathVariable Long employeeId) {
        List<SalesHistoryResponseDto> salesHistory = saleHistoryService.getSalesHistoryForEmployee(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Sales history retrieved successfully", salesHistory));
    }
}
