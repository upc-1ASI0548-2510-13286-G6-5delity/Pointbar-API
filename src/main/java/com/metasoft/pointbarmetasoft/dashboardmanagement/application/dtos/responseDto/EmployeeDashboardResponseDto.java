package com.metasoft.pointbarmetasoft.dashboardmanagement.application.dtos.responseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDashboardResponseDto {
    private String employeeName;
    private int tablesServed;
    private Double revenueGenerated;
    private double rating;
}
