package com.metasoft.pointbarmetasoft.dashboardmanagement.application.services;
import com.metasoft.pointbarmetasoft.dashboardmanagement.application.dtos.responseDto.DashboardSummaryResponseDto;
import com.metasoft.pointbarmetasoft.dashboardmanagement.application.dtos.responseDto.EmployeeDashboardResponseDto;
import java.util.List;

public interface IDashboardService {
    //List<EmployeeDashboardResponseDto> getEmployeeDashboard(Long businessId);
    DashboardSummaryResponseDto getDashboardSummary(Long businessId, String username);
}
