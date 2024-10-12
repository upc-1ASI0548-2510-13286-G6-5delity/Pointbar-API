package com.metasoft.pointbarmetasoft.tablemanagement.application.dtos.responseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableSpaceResponseDto {
    private Long id;
    private String name;
    private Integer numberOfTables;
    private String imageUrl;
}
