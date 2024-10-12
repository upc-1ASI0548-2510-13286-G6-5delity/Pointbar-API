package com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDto {
    private Long id;
    private Long orderId;
    private Long beverageId;
    private String beverageName;
    private Double beveragePrice;
    private Integer quantity;
    private Boolean delivered;
    private Double subtotal;
}
