package org.texas.systembdao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.texas.systembdao.dto.CertificateResponse;
import org.texas.systembdao.dto.IssueCertificateRequest;
import org.texas.systembdao.entity.CitizenshipCertificate;
import org.texas.systembdao.entity.DaoCitizenPartial;
import org.texas.systembdao.entity.enums.AuditResult;
import org.texas.systembdao.exception.ResourceNotFoundException;
import org.texas.systembdao.repository.CitizenshipCertificateRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CertificateService {

    private final CitizenshipCertificateRepository certificateRepository;
    private final DaoCitizenService daoCitizenService;
    private final AuditService auditService;

    private final AtomicLong sequence = new AtomicLong(1);

    public CertificateService(CitizenshipCertificateRepository certificateRepository,
                              DaoCitizenService daoCitizenService,
                              AuditService auditService) {
        this.certificateRepository = certificateRepository;
        this.daoCitizenService = daoCitizenService;
        this.auditService = auditService;
    }

    @Transactional
    public CertificateResponse issueCertificate(IssueCertificateRequest request,
                                                String officerUsername,
                                                String ipAddress) {

        String nid = request.getNid();

        DaoCitizenPartial citizen = daoCitizenService.findEntityByNid(nid);

        if (citizen.getDob() == null) {
            throw new ResourceNotFoundException(
                    "Cannot issue certificate — DOB not yet available for NID " + nid);
        }

        String certificateId = generateCertificateId();

        CitizenshipCertificate certificate = CitizenshipCertificate.builder()
                .certificateId(certificateId)
                .citizenNid(nid)
                .issuedAt(LocalDateTime.now())
                .issuedBy(officerUsername)
                .dobSource(citizen.getDobSource() != null ? citizen.getDobSource() : "UNKNOWN")
                .build();

        CitizenshipCertificate saved = certificateRepository.save(certificate);

        auditService.log(
                "SYSTEM_B",
                officerUsername,
                nid,
                "ISSUE_CERTIFICATE",
                "N/A",
                List.of("certificateId"),
                0L,
                AuditResult.SUCCESS,
                ipAddress
        );

        return CertificateResponse.builder()
                .certificateId(saved.getCertificateId())
                .citizenNid(saved.getCitizenNid())
                .issuedAt(saved.getIssuedAt())
                .issuedBy(saved.getIssuedBy())
                .dobSource(saved.getDobSource())
                .build();
    }

    private String generateCertificateId() {
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long seq = sequence.getAndIncrement();
        return String.format("CERT-%s-%04d", year, seq);
    }
}