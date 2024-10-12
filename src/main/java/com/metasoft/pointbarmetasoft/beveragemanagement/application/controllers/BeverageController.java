package com.metasoft.pointbarmetasoft.beveragemanagement.application.controllers;

import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.requestDto.BeverageRequestDto;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.responseDto.BeverageResponseDto;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.services.IBeverageService;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beverages")
public class BeverageController {
    private final IBeverageService beverageService;
    private final UserRepository userRepository;

    public BeverageController(IBeverageService beverageService, UserRepository userRepository) {
        this.beverageService = beverageService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> createBeverage(@Valid @ModelAttribute BeverageRequestDto requestDto,
                                                         Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ApiResponse<?> response = beverageService.createBeverage(requestDto, user.getBusiness().getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/findAll")
    public ResponseEntity<ApiResponse<List<BeverageResponseDto>>> getAllBeverages(Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<BeverageResponseDto> beverages = beverageService.getAllBeverages(user.getBusiness().getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Beverages retrieved successfully", beverages));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> updateBeverage(@PathVariable Long id,
                                                         @Valid @ModelAttribute BeverageRequestDto requestDto,
                                                         Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ApiResponse<?> response = beverageService.updateBeverage(id, requestDto, user.getBusiness().getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deleteBeverage(@PathVariable Long id,
                                                         Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ApiResponse<?> response = beverageService.deleteBeverage(id, user.getBusiness().getId());
        return ResponseEntity.ok(response);
    }
}
