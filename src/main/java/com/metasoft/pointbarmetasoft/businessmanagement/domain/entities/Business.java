package com.metasoft.pointbarmetasoft.businessmanagement.domain.entities;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.Admin;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "business")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name = "PointBar";

    @NotBlank
    private String description = "El mejor software de Bares";

    @NotBlank
    private String address = "Default";

    private String logoUrl = "https://i.postimg.cc/XqSsDcH0/logo-preview.png";

    @OneToMany(mappedBy = "business")
    private Set<Admin> users = new HashSet<>();
}
