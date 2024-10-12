package com.metasoft.pointbarmetasoft.beveragemanagement.application.services.Implements;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.requestDto.CategoryRequestDto;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.responseDto.CategoryResponseDto;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.services.ICategoryService;
import com.metasoft.pointbarmetasoft.beveragemanagement.domain.entities.Category;
import com.metasoft.pointbarmetasoft.beveragemanagement.infraestructure.repositories.CategoryRepository;
import com.metasoft.pointbarmetasoft.businessmanagement.domain.entities.Business;
import com.metasoft.pointbarmetasoft.businessmanagement.infraestructure.repositories.BusinessRepository;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final BusinessRepository businessRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               BusinessRepository businessRepository,
                               ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.businessRepository = businessRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public ApiResponse<?> createCategory(CategoryRequestDto requestDto, Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

        Category category = new Category();
        category.setName(requestDto.getName());
        category.setBusiness(business);

        category = categoryRepository.save(category);

        CategoryResponseDto responseDto = modelMapper.map(category, CategoryResponseDto.class);
        return new ApiResponse<>(true, "Category created successfully", responseDto);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories(Long businessId) {
        List<Category> categories = categoryRepository.findByBusinessId(businessId);
        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ApiResponse<?> updateCategory(Long id, CategoryRequestDto requestDto, Long businessId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getBusiness().getId().equals(businessId)) {
            throw new ResourceNotFoundException("Category not found for this business");
        }

        category.setName(requestDto.getName());
        category = categoryRepository.save(category);

        CategoryResponseDto responseDto = modelMapper.map(category, CategoryResponseDto.class);
        return new ApiResponse<>(true, "Category updated successfully", responseDto);
    }

    @Override
    public ApiResponse<?> deleteCategory(Long id, Long businessId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getBusiness().getId().equals(businessId)) {
            throw new ResourceNotFoundException("Category not found for this business");
        }

        categoryRepository.delete(category);
        return new ApiResponse<>(true, "Category deleted successfully", null);
    }
}
