package com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long beverageId;
    private Integer quantity;
}
