package com.metasoft.pointbarmetasoft.businessmanagement.application.services.Implements;
import com.metasoft.pointbarmetasoft.businessmanagement.application.dtos.requestDto.BusinessConfigRequestDto;
import com.metasoft.pointbarmetasoft.businessmanagement.application.dtos.responseDto.BusinessConfigResponseDto;
import com.metasoft.pointbarmetasoft.businessmanagement.application.services.IBusinessService;
import com.metasoft.pointbarmetasoft.businessmanagement.domain.entities.Business;
import com.metasoft.pointbarmetasoft.businessmanagement.infraestructure.repositories.BusinessRepository;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import com.metasoft.pointbarmetasoft.shared.storage.FirebaseFileService;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class BusinessServiceImpl implements IBusinessService {
    private final BusinessRepository businessRepository;
    private final FirebaseFileService firebaseFileService;
    private final UserRepository userRepository;

    public BusinessServiceImpl(BusinessRepository businessRepository, FirebaseFileService firebaseFileService, UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.firebaseFileService = firebaseFileService;
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponse<?> updateBusiness (BusinessConfigRequestDto businessConfigRequestDto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Business business = businessRepository.findByUsersContaining(user)
                .orElseThrow(() -> new ResourceNotFoundException("No business found for this user"));

        business.setName(businessConfigRequestDto.getName());
        business.setDescription(businessConfigRequestDto.getDescription());
        business.setAddress(businessConfigRequestDto.getAddress());

        if (businessConfigRequestDto.getLogo() != null && !businessConfigRequestDto.getLogo().isEmpty()) {
            String logoUrl;
            try {
                logoUrl = firebaseFileService.saveImage(businessConfigRequestDto.getLogo());
            } catch (IOException e) {
                throw new RuntimeException("Error uploading the logo", e);
            }
            business.setLogoUrl(logoUrl);
        }

        businessRepository.save(business);
        return new ApiResponse<>(true, "Business updated successfully", null);
    }

    @Override
    public BusinessConfigResponseDto getBusiness(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Business business = businessRepository.findByUsersContaining(user)
                .orElseThrow(() -> new ResourceNotFoundException("No business found for this user"));

        return new BusinessConfigResponseDto(
                business.getId(),
                business.getName(),
                business.getDescription(),
                business.getAddress(),
                business.getLogoUrl()
        );
    }
}
