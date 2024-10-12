package com.metasoft.pointbarmetasoft.tablemanagement.application.services;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import com.metasoft.pointbarmetasoft.tablemanagement.application.dtos.requestDto.TableSpaceRequestDto;
import com.metasoft.pointbarmetasoft.tablemanagement.application.dtos.responseDto.TableSpaceResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ITableSpaceService {
    ApiResponse<?> createTableSpace(TableSpaceRequestDto requestDto, Long businessId);
    List<TableSpaceResponseDto> getAllTableSpaces(Long businessId);
    ApiResponse<?> updateTableSpace(Long id, TableSpaceRequestDto requestDto, Long businessId);
    ApiResponse<?> deleteTableSpace(Long id, Long businessId);
}
