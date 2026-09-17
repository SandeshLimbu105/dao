package org.texas.systembdao.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.texas.systembdao.dto.DaoCitizenResponse;
import org.texas.systembdao.dto.ManualDobRequest;
import org.texas.systembdao.service.DaoCitizenService;

@RestController
@RequestMapping("/api/system-b/citizens")
@Tag(name = "System B — DAO Citizen", description = "Search citizens and manage DOB")
public class DaoCitizenController {

    private final DaoCitizenService daoCitizenService;

    public DaoCitizenController(DaoCitizenService daoCitizenService) {
        this.daoCitizenService = daoCitizenService;
    }

    @GetMapping("/{nid}")
    @PreAuthorize("hasRole('DAO_OFFICER')")
    @Operation(summary = "Search citizen by NID in DAO's own records (partial data)")
    public ResponseEntity<DaoCitizenResponse> findByNid(@PathVariable String nid) {
        return ResponseEntity.ok(daoCitizenService.findByNid(nid));
    }

    @PutMapping("/{nid}/dob")
    @PreAuthorize("hasRole('DAO_OFFICER')")
    @Operation(summary = "Manually enter DOB (fallback when System A is unavailable)")
    public ResponseEntity<DaoCitizenResponse> saveManualDob(
            @PathVariable String nid,
            @Valid @RequestBody ManualDobRequest request,
            Authentication auth,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(daoCitizenService.saveManualDob(
                nid, request, auth.getName(), httpRequest.getRemoteAddr()));
    }
}