package com.metasoft.pointbarmetasoft.tablemanagement.application.controllers;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import com.metasoft.pointbarmetasoft.tablemanagement.application.dtos.requestDto.TableSpaceRequestDto;
import com.metasoft.pointbarmetasoft.tablemanagement.application.dtos.responseDto.TableSpaceResponseDto;
import com.metasoft.pointbarmetasoft.tablemanagement.application.services.ITableSpaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/table-spaces")
public class TableSpaceController {
    private final ITableSpaceService tableSpaceService;
    private final UserRepository userRepository;

    public TableSpaceController(ITableSpaceService tableSpaceService, UserRepository userRepository) {
        this.tableSpaceService = tableSpaceService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> createTableSpace(
            @Valid @ModelAttribute TableSpaceRequestDto requestDto,
            Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ApiResponse<?> response = tableSpaceService.createTableSpace(requestDto, user.getBusiness().getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/findAll")
    public ResponseEntity<ApiResponse<List<TableSpaceResponseDto>>> getAllTableSpaces(Authentication authentication) {
        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<TableSpaceResponseDto> tableSpaces = tableSpaceService.getAllTableSpaces(user.getBusiness().getId());

        return ResponseEntity.ok(new ApiResponse<>(true, "Table spaces retrieved successfully", tableSpaces));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> updateTableSpace(
            @PathVariable Long id,
            @Valid @ModelAttribute TableSpaceRequestDto requestDto,
            Authentication authentication) {
        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ApiResponse<?> response = tableSpaceService.updateTableSpace(id, requestDto, user.getBusiness().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deleteTableSpace(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ApiResponse<?> response = tableSpaceService.deleteTableSpace(id, user.getBusiness().getId());

        return ResponseEntity.ok(response);
    }
}
