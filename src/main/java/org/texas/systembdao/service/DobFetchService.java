package org.texas.systembdao.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.texas.systembdao.client.DobResponse;
import org.texas.systembdao.client.WardApiClient;
import org.texas.systembdao.dto.DobFetchResponse;
import org.texas.systembdao.entity.DaoCitizenPartial;
import org.texas.systembdao.entity.enums.AuditResult;
import org.texas.systembdao.exception.ConsentDeniedException;
import org.texas.systembdao.exception.SourceUnavailableException;

import java.util.List;

@Service
public class DobFetchService {

    private static final Logger log = LoggerFactory.getLogger(DobFetchService.class);

    private final WardApiClient wardApiClient;
    private final DaoCitizenService daoCitizenService;
    private final AuditService auditService;

    public DobFetchService(WardApiClient wardApiClient,
                           DaoCitizenService daoCitizenService,
                           AuditService auditService) {
        this.wardApiClient = wardApiClient;
        this.daoCitizenService = daoCitizenService;
        this.auditService = auditService;
    }

    /**
     * Fetch DOB from System A (Ward Office) and persist it into the DAO's local record.
     */
    @Transactional
    public DobFetchResponse fetchDob(String nid,
                                     String officerUsername,
                                     String ipAddress) {

        long start = System.currentTimeMillis();

        try {
            // Call System A
            DobResponse response = wardApiClient.fetchDob(nid);

            if (response == null || response.getDob() == null) {
                throw new SourceUnavailableException(
                        "System A returned no DOB for NID " + nid);
            }

            // Save locally
            DaoCitizenPartial updated = daoCitizenService.saveSystemADob(nid, response.getDob());

            long took = System.currentTimeMillis() - start;

            // Audit success — field-level
            auditService.log(
                    "SYSTEM_B",
                    officerUsername,
                    nid,
                    "FETCH_DOB",
                    "ACTIVE",
                    List.of("dob"),
                    took,
                    AuditResult.SUCCESS,
                    ipAddress
            );

            return DobFetchResponse.builder()
                    .nid(updated.getNid())
                    .dob(updated.getDob())
                    .source("SYSTEM_A")
                    .build();

        } catch (ConsentDeniedException e) {
            long took = System.currentTimeMillis() - start;
            log.warn("Consent denied by System A for NID {}: {}", nid, e.getMessage());

            auditService.log(
                    "SYSTEM_B",
                    officerUsername,
                    nid,
                    "FETCH_DOB",
                    e.getReason(),   // CONSENT_MISSING / EXPIRED / REVOKED
                    List.of(),
                    took,
                    AuditResult.DENIED,
                    ipAddress
            );

            throw e;

        } catch (SourceUnavailableException e) {
            long took = System.currentTimeMillis() - start;
            log.error("System A unavailable for NID {}: {}", nid, e.getMessage());

            auditService.log(
                    "SYSTEM_B",
                    officerUsername,
                    nid,
                    "FETCH_DOB",
                    "N/A",
                    List.of(),
                    took,
                    AuditResult.ERROR,
                    ipAddress
            );

            throw e;
        }
    }
}