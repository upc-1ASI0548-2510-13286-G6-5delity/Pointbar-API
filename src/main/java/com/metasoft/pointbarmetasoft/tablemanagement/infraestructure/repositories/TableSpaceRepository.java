package com.metasoft.pointbarmetasoft.tablemanagement.infraestructure.repositories;
import com.metasoft.pointbarmetasoft.tablemanagement.domain.entities.TableSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TableSpaceRepository extends JpaRepository<TableSpace, Long> {
    List<TableSpace> findByBusinessId(Long businessId);
}
