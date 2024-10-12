package com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequestDto {
    private Long tableSpaceId;
    private Long tableId;
    private List<OrderItemDto> items;
}
