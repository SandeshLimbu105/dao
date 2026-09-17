package org.texas.systembdao.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.texas.systembdao.dto.CertificateResponse;
import org.texas.systembdao.dto.IssueCertificateRequest;
import org.texas.systembdao.service.CertificateService;

@RestController
@RequestMapping("/api/system-b/certificates")
@Tag(name = "System B — Certificate", description = "Issue citizenship certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DAO_OFFICER')")
    @Operation(summary = "Issue a citizenship certificate for a citizen")
    public ResponseEntity<CertificateResponse> issueCertificate(
            @Valid @RequestBody IssueCertificateRequest request,
            Authentication auth,
            HttpServletRequest httpRequest) {

        CertificateResponse response = certificateService.issueCertificate(
                request, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}