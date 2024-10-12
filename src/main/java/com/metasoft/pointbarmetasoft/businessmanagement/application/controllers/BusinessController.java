package com.metasoft.pointbarmetasoft.businessmanagement.application.controllers;
import com.metasoft.pointbarmetasoft.businessmanagement.application.dtos.requestDto.BusinessConfigRequestDto;
import com.metasoft.pointbarmetasoft.businessmanagement.application.services.Implements.BusinessServiceImpl;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/business")
public class BusinessController {
    private final BusinessServiceImpl businessService;
    private final UserRepository userRepository;

    public BusinessController(BusinessServiceImpl businessService, UserRepository userRepository) {
        this.businessService = businessService;
        this.userRepository = userRepository;
    }

    @GetMapping("/find")
    public ResponseEntity<ApiResponse<?>> getBusinessConfig(Authentication authentication) {
        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var response = businessService.getBusiness(user.getId());
        return new ResponseEntity<>(new ApiResponse<>(true, "Business config retrieved", response), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<?>> updateBusinessConfig(
            @ModelAttribute BusinessConfigRequestDto businessConfigRequestDto,
            Authentication authentication) {

        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var response = businessService.updateBusiness(businessConfigRequestDto, user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
