package com.metasoft.pointbarmetasoft.beveragemanagement.application.services.Implements;

import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.requestDto.BeverageRequestDto;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.responseDto.BeverageResponseDto;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.services.IBeverageService;
import com.metasoft.pointbarmetasoft.beveragemanagement.domain.entities.Beverage;
import com.metasoft.pointbarmetasoft.beveragemanagement.domain.entities.Category;
import com.metasoft.pointbarmetasoft.beveragemanagement.infraestructure.repositories.BeverageRepository;
import com.metasoft.pointbarmetasoft.beveragemanagement.infraestructure.repositories.CategoryRepository;
import com.metasoft.pointbarmetasoft.businessmanagement.domain.entities.Business;
import com.metasoft.pointbarmetasoft.businessmanagement.infraestructure.repositories.BusinessRepository;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import com.metasoft.pointbarmetasoft.shared.storage.FirebaseFileService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeverageServiceImpl implements IBeverageService {
    private final BeverageRepository beverageRepository;
    private final BusinessRepository businessRepository;
    private final CategoryRepository categoryRepository;
    private final FirebaseFileService firebaseFileService;
    private final ModelMapper modelMapper;

    public BeverageServiceImpl(BeverageRepository beverageRepository,
                               BusinessRepository businessRepository,
                               CategoryRepository categoryRepository,
                               FirebaseFileService firebaseFileService,
                               ModelMapper modelMapper) {
        this.beverageRepository = beverageRepository;
        this.businessRepository = businessRepository;
        this.categoryRepository = categoryRepository;
        this.firebaseFileService = firebaseFileService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ApiResponse<?> createBeverage(BeverageRequestDto requestDto, Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Beverage beverage = modelMapper.map(requestDto, Beverage.class);
        beverage.setBusiness(business);
        beverage.setCategory(category);

        if (requestDto.getImage() != null) {
            String imageUrl = null;
            try {
                imageUrl = firebaseFileService.saveImage(requestDto.getImage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            beverage.setImageUrl(imageUrl);
        }

        Beverage savedBeverage = beverageRepository.save(beverage);
        return new ApiResponse<>(true, "Beverage created successfully", modelMapper.map(savedBeverage, BeverageResponseDto.class));
    }

    @Override
    public List<BeverageResponseDto> getAllBeverages(Long businessId) {
        List<Beverage> beverages = beverageRepository.findByBusinessId(businessId);
        return beverages.stream()
                .map(beverage -> {
                    BeverageResponseDto dto = modelMapper.map(beverage, BeverageResponseDto.class);
                    dto.setCategoryName(beverage.getCategory().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ApiResponse<?> updateBeverage(Long id, BeverageRequestDto requestDto, Long businessId) {
        Beverage beverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found"));

        if (!beverage.getBusiness().getId().equals(businessId)) {
            return new ApiResponse<>(false, "You don't have permission to update this beverage", null);
        }

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        beverage.setName(requestDto.getName());
        beverage.setDescription(requestDto.getDescription());
        beverage.setCategory(category);
        beverage.setPrice(requestDto.getPrice());

        if (requestDto.getImage() != null && !requestDto.getImage().isEmpty()) {
            String imageUrl;
            try {
                imageUrl = firebaseFileService.saveImage(requestDto.getImage());
                beverage.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Error uploading the image", e);
            }
            beverage.setImageUrl(imageUrl);
        }

        Beverage updatedBeverage = beverageRepository.save(beverage);
        BeverageResponseDto responseDto = modelMapper.map(updatedBeverage, BeverageResponseDto.class);
        responseDto.setCategoryName(updatedBeverage.getCategory().getName());

        return new ApiResponse<>(true, "Beverage updated successfully", responseDto);
    }

    @Override
    public ApiResponse<?> deleteBeverage(Long id, Long businessId) {
        Beverage beverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found"));

        if (!beverage.getBusiness().getId().equals(businessId)) {
            return new ApiResponse<>(false, "You don't have permission to delete this beverage", null);
        }

        beverageRepository.delete(beverage);
        return new ApiResponse<>(true, "Beverage deleted successfully", null);
    }
}
