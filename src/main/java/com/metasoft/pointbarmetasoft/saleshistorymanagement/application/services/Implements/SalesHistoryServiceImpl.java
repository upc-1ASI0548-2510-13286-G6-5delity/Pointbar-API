package com.metasoft.pointbarmetasoft.saleshistorymanagement.application.services.Implements;
import com.metasoft.pointbarmetasoft.saleshistorymanagement.application.dtos.responseDto.SalesHistoryResponseDto;
import com.metasoft.pointbarmetasoft.saleshistorymanagement.application.services.ISalesHistoryService;
import com.metasoft.pointbarmetasoft.saleshistorymanagement.domain.entities.SalesHistory;
import com.metasoft.pointbarmetasoft.saleshistorymanagement.infraestructure.repositories.SalesHistoryRepository;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalesHistoryServiceImpl implements ISalesHistoryService {
    private final SalesHistoryRepository saleHistoryRepository;
    private final UserRepository userRepository;

    public SalesHistoryServiceImpl(SalesHistoryRepository saleHistoryRepository, UserRepository userRepository) {
        this.saleHistoryRepository = saleHistoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<SalesHistoryResponseDto> getSalesHistoryForBusiness(Long businessId) {
        List<SalesHistory> sales = saleHistoryRepository.findAllByBusiness(businessId);
        return sales.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<SalesHistoryResponseDto> getSalesHistoryForEmployee(Long employeeId) {
        List<SalesHistory> sales = saleHistoryRepository.findAllByEmployee(employeeId);
        return sales.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private SalesHistoryResponseDto mapToDto(SalesHistory sale) {
        return new SalesHistoryResponseDto(
                sale.getEmployee().getFirstname() + " " + sale.getEmployee().getLastname(),
                sale.getTableName(),
                sale.getAmount(),
                sale.getSaleDate()
        );
    }
}
