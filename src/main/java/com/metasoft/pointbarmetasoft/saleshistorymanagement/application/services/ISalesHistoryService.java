package com.metasoft.pointbarmetasoft.saleshistorymanagement.application.services;
import com.metasoft.pointbarmetasoft.saleshistorymanagement.application.dtos.responseDto.SalesHistoryResponseDto;
import java.util.List;

public interface ISalesHistoryService {
    List<SalesHistoryResponseDto> getSalesHistoryForBusiness(Long businessId);
    List<SalesHistoryResponseDto> getSalesHistoryForEmployee(Long employeeId);
}
