package com.metasoft.pointbarmetasoft.dashboardmanagement.application.services.Implements;
import com.metasoft.pointbarmetasoft.businessmanagement.infraestructure.repositories.BusinessRepository;
import com.metasoft.pointbarmetasoft.dashboardmanagement.application.dtos.responseDto.DashboardSummaryResponseDto;
import com.metasoft.pointbarmetasoft.dashboardmanagement.application.dtos.responseDto.EmployeeDashboardResponseDto;
import com.metasoft.pointbarmetasoft.dashboardmanagement.application.services.IDashboardService;
import com.metasoft.pointbarmetasoft.dashboardmanagement.infraestructure.repositories.SalesRepository;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements IDashboardService {
    private final SalesRepository salesRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public DashboardServiceImpl(SalesRepository salesRepository, UserRepository userRepository, BusinessRepository businessRepository) {
        this.salesRepository = salesRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    /*
    @Override
    public List<EmployeeDashboardResponseDto> getEmployeeDashboard(Long businessId) {
        List<UserEntity> employees = userRepository.findByBusinessId(businessId);
        List<EmployeeDashboardResponseDto> employeeData = new ArrayList<>();

        // Obtener datos para cada empleado
        for (UserEntity employee : employees) {
            Integer tablesServed = salesRepository.getTablesServedByUser(employee.getId());
            Double revenueGenerated = salesRepository.getRevenueByUser(employee.getId());

            // Por ahora, el rating está pendiente de implementación
            employeeData.add(new EmployeeDashboardResponseDto(employee.getFirstname() + " " + employee.getLastname(),tablesServed, revenueGenerated, 0.0));
        }
        return employeeData;
    }

     */

    @Override
    public DashboardSummaryResponseDto getDashboardSummary(Long businessId, String username) {
        Double salesToday = salesRepository.getSalesForToday(businessId);
        Integer tablesServedToday = salesRepository.getTablesServedForToday(businessId);
        Double totalRevenue = salesRepository.getTotalRevenue(businessId);
        return new DashboardSummaryResponseDto(username, salesToday, tablesServedToday, totalRevenue);
    }
}
