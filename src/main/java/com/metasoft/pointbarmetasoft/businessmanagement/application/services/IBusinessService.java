package com.metasoft.pointbarmetasoft.businessmanagement.application.services;
import com.metasoft.pointbarmetasoft.businessmanagement.application.dtos.requestDto.BusinessConfigRequestDto;
import com.metasoft.pointbarmetasoft.businessmanagement.application.dtos.responseDto.BusinessConfigResponseDto;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;

public interface IBusinessService {
    ApiResponse<?> updateBusiness(BusinessConfigRequestDto businessConfigRequestDto, Long userId);
    BusinessConfigResponseDto getBusiness(Long userId);
}
