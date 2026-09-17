package org.texas.systembdao.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.texas.systembdao.dto.DaoCitizenResponse;
import org.texas.systembdao.dto.ManualDobRequest;
import org.texas.systembdao.entity.DaoCitizenPartial;
import org.texas.systembdao.entity.enums.AuditResult;
import org.texas.systembdao.exception.ResourceNotFoundException;
import org.texas.systembdao.repository.DaoCitizenRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class DaoCitizenService {

    private final DaoCitizenRepository daoCitizenRepository;
    private final AuditService auditService;

    public DaoCitizenService(DaoCitizenRepository daoCitizenRepository,
                             AuditService auditService) {
        this.daoCitizenRepository = daoCitizenRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public DaoCitizenResponse findByNid(String nid) {
        DaoCitizenPartial citizen = daoCitizenRepository.findByNid(nid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Citizen not found in DAO records: " + nid));
        return toResponse(citizen);
    }

    @Transactional(readOnly = true)
    public DaoCitizenPartial findEntityByNid(String nid) {
        return daoCitizenRepository.findByNid(nid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Citizen not found in DAO records: " + nid));
    }

    /**
     * Save a manually-entered DOB — used only when System A is unavailable or consent denied.
     */
    @Transactional
    public DaoCitizenResponse saveManualDob(String nid, ManualDobRequest request,
                                            String officerUsername, String ipAddress) {

        DaoCitizenPartial citizen = daoCitizenRepository.findByNid(nid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Citizen not found in DAO records: " + nid));

        citizen.setDob(request.getDob());
        citizen.setDobSource("MANUAL");
        DaoCitizenPartial saved = daoCitizenRepository.save(citizen);

        auditService.log(
                "SYSTEM_B",
                officerUsername,
                nid,
                "MANUAL_DOB_ENTRY",
                "N/A",
                List.of("dob"),
                0L,
                AuditResult.SUCCESS,
                ipAddress
        );

        return toResponse(saved);
    }

    /**
     * Save a DOB fetched from System A.
     */
    @Transactional
    public DaoCitizenPartial saveSystemADob(String nid, LocalDate dob) {
        DaoCitizenPartial citizen = daoCitizenRepository.findByNid(nid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Citizen not found in DAO records: " + nid));

        citizen.setDob(dob);
        citizen.setDobSource("SYSTEM_A");
        return daoCitizenRepository.save(citizen);
    }

    private DaoCitizenResponse toResponse(DaoCitizenPartial c) {
        return DaoCitizenResponse.builder()
                .nid(c.getNid())
                .fullName(c.getFullName())
                .address(c.getAddress())
                .parentsNames(c.getParentsNames())
                .dob(c.getDob())
                .dobSource(c.getDobSource())
                .build();
    }
}