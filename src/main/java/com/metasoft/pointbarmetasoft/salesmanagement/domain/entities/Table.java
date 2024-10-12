package com.metasoft.pointbarmetasoft.salesmanagement.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.metasoft.pointbarmetasoft.tablemanagement.domain.entities.TableSpace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@jakarta.persistence.Table(name = "table")
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_space_id", nullable = false)
    private TableSpace tableSpace;

    private Integer number;
}
