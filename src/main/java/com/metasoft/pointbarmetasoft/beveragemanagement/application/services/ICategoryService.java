package com.metasoft.pointbarmetasoft.beveragemanagement.application.services;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.requestDto.CategoryRequestDto;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.responseDto.CategoryResponseDto;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import java.util.List;

public interface ICategoryService {
    ApiResponse<?> createCategory(CategoryRequestDto requestDto, Long businessId);
    List<CategoryResponseDto> getAllCategories(Long businessId);
    ApiResponse<?> updateCategory(Long id, CategoryRequestDto requestDto, Long businessId);
    ApiResponse<?> deleteCategory(Long id, Long businessId);
}
