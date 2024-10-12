package com.metasoft.pointbarmetasoft.securitymanagement.domain.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.enums.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private ERole rolName;

    public Role(ERole eRole) {
        this.rolName = eRole;
    }
}
