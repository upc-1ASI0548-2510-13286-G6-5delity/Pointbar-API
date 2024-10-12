package com.metasoft.pointbarmetasoft.securitymanagement.domain.entities;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("client")
public class Client extends UserEntity {
    @Column(name = "dni")
    private String dni;

    @Column(name = "phone")
    private String phone;
}
