package org.texas.systembdao.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.texas.systembdao.dto.DobFetchResponse;
import org.texas.systembdao.service.DobFetchService;

@RestController
@RequestMapping("/api/system-b/citizens")
@Tag(name = "System B — Interoperability",
        description = "Fetch DOB from System A (Ward Office) via REST")
public class DobFetchController {

    private final DobFetchService dobFetchService;

    public DobFetchController(DobFetchService dobFetchService) {
        this.dobFetchService = dobFetchService;
    }

    @PostMapping("/{nid}/fetch-dob")
    @PreAuthorize("hasRole('DAO_OFFICER')")
    @Operation(
            summary = "Fetch DOB from System A (Ward Office)",
            description = """
            Calls System A's /api/system-a/citizens/{nid}/dob endpoint.
            Requires the citizen to have granted ACTIVE consent in System A.

            Possible outcomes:
            - 200 OK: DOB returned and saved locally with source=SYSTEM_A
            - 403 CONSENT_MISSING: No consent in System A
            - 403 CONSENT_EXPIRED: Consent expired
            - 403 CONSENT_REVOKED: Consent revoked
            - 503 SOURCE_UNAVAILABLE: System A is unreachable
            """
    )
    public ResponseEntity<DobFetchResponse> fetchDob(
            @PathVariable String nid,
            Authentication auth,
            HttpServletRequest httpRequest) {

        DobFetchResponse response = dobFetchService.fetchDob(
                nid, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }
}