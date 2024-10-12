package com.metasoft.pointbarmetasoft.tablemanagement.domain.entities;
import com.metasoft.pointbarmetasoft.businessmanagement.domain.entities.Business;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "table_spaces")
public class TableSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del espacio es requerido")
    private String name;

    @NotNull(message = "El número de mesas es requerido")
    private Integer numberOfTables;

    private String imageUrl = "https://i.postimg.cc/rsscdnvx/bar-default.jpg";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;
}
