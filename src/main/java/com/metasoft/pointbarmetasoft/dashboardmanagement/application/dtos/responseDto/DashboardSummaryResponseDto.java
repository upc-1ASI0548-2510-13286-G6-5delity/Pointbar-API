package com.metasoft.pointbarmetasoft.dashboardmanagement.application.dtos.responseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryResponseDto {
    private String user;
    private Double salesToday;
    private Integer tablesServedToday;
    private Double totalRevenue;
}
