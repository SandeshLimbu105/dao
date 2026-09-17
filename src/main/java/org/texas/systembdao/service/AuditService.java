package org.texas.systembdao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.texas.systembdao.entity.AuditLog;
import org.texas.systembdao.entity.enums.AuditResult;
import org.texas.systembdao.repository.AuditLogRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String requestingSystem,
                    String officerId,
                    String citizenNid,
                    String purpose,
                    String consentStatus,
                    List<String> fieldsReturned,
                    long responseTimeMs,
                    AuditResult result,
                    String ipAddress) {

        AuditLog log = AuditLog.builder()
                .timestamp(LocalDateTime.now())
                .requestingSystem(requestingSystem)
                .officerId(officerId)
                .citizenNid(citizenNid)
                .purpose(purpose)
                .consentStatus(consentStatus)
                .fieldsReturned(fieldsReturned == null || fieldsReturned.isEmpty()
                        ? null
                        : "[\"" + String.join("\",\"", fieldsReturned) + "\"]")
                .responseTimeMs((int) responseTimeMs)
                .result(result)
                .ipAddress(ipAddress)
                .build();

        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> findByCitizenNid(String citizenNid) {
        return auditLogRepository.findByCitizenNidOrderByTimestampDesc(citizenNid);
    }
}