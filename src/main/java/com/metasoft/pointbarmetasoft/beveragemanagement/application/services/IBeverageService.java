package com.metasoft.pointbarmetasoft.beveragemanagement.application.services;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.requestDto.BeverageRequestDto;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.responseDto.BeverageResponseDto;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import java.util.List;

public interface IBeverageService {
    ApiResponse<?> createBeverage(BeverageRequestDto requestDto, Long businessId);
    List<BeverageResponseDto> getAllBeverages(Long businessId);
    ApiResponse<?> updateBeverage(Long id, BeverageRequestDto requestDto, Long businessId);
    ApiResponse<?> deleteBeverage(Long id, Long businessId);
}
