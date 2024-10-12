package com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.responseDto;

import com.metasoft.pointbarmetasoft.salesmanagement.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long id;
    private Long tableSpaceId;
    private String employeeName;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderItemResponseDto> items;
    private Double total;
}
