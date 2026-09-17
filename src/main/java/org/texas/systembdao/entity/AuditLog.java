package org.texas.systembdao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.texas.systembdao.entity.enums.AuditResult;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "requesting_system", length = 50, nullable = false)
    private String requestingSystem;

    @Column(name = "officer_id", length = 100)
    private String officerId;

    @Column(name = "citizen_nid", length = 20)
    private String citizenNid;

    @Column(name = "purpose", length = 100)
    private String purpose;

    @Column(name = "consent_status", length = 20)
    private String consentStatus;

    @Column(name = "fields_returned", length = 500)
    private String fieldsReturned;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", length = 20, nullable = false)
    private AuditResult result;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;
}