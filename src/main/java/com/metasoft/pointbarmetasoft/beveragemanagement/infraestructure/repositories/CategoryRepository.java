package com.metasoft.pointbarmetasoft.beveragemanagement.infraestructure.repositories;

import com.metasoft.pointbarmetasoft.beveragemanagement.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByBusinessId(Long businessId);
}
