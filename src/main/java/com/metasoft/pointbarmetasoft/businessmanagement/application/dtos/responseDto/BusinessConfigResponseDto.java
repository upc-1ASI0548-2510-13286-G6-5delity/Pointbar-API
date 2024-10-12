package com.metasoft.pointbarmetasoft.businessmanagement.application.dtos.responseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessConfigResponseDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String logoUrl;
}
