package org.texas.systembdao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dao_citizen_partial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DaoCitizenPartial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nid", length = 20, nullable = false, unique = true)
    private String nid;

    @Column(name = "full_name", length = 150, nullable = false)
    private String fullName;

    @Column(name = "address", length = 250, nullable = false)
    private String address;

    @Column(name = "parents_names", length = 250, nullable = false)
    private String parentsNames;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "dob_source", length = 20)
    private String dobSource;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}