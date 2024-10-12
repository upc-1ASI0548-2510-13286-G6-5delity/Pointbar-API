package com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.responseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeverageResponseDto {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private Double price;
    private String imageUrl;
}
