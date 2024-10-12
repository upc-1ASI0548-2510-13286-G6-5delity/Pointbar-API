package com.metasoft.pointbarmetasoft.beveragemanagement.application.controllers;

import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.requestDto.CategoryRequestDto;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.responseDto.CategoryResponseDto;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.services.ICategoryService;
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
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final ICategoryService categoryService;
    private final UserRepository userRepository;

    public CategoryController(ICategoryService categoryService, UserRepository userRepository) {
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> createCategory(@Valid @RequestBody CategoryRequestDto requestDto,
                                                         Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ApiResponse<?> response = categoryService.createCategory(requestDto, user.getBusiness().getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/findAll")
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getAllCategories(Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<CategoryResponseDto> categories = categoryService.getAllCategories(user.getBusiness().getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Categories retrieved successfully", categories));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> updateCategory(@PathVariable Long id,
                                                         @Valid @RequestBody CategoryRequestDto requestDto,
                                                         Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ApiResponse<?> response = categoryService.updateCategory(id, requestDto, user.getBusiness().getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable Long id,
                                                         Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ApiResponse<?> response = categoryService.deleteCategory(id, user.getBusiness().getId());
        return ResponseEntity.ok(response);
    }
}
