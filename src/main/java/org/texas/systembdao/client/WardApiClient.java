package org.texas.systembdao.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.texas.systembdao.exception.ConsentDeniedException;
import org.texas.systembdao.exception.SourceUnavailableException;
import tools.jackson.databind.ObjectMapper;

@Component
public class WardApiClient {

    private static final Logger log = LoggerFactory.getLogger(WardApiClient.class);

    private final RestTemplate restTemplate;
    private final ServiceJwtProvider serviceJwtProvider;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String apiKey;

    public WardApiClient(@Value("${system.a.base-url}") String baseUrl,
                         @Value("${system.a.api-key}") String apiKey,
                         ServiceJwtProvider serviceJwtProvider,
                         ObjectMapper objectMapper) {

        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.serviceJwtProvider = serviceJwtProvider;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    public DobResponse fetchDob(String nid) {
        log.info("Fetching DOB from System A for NID: {}", nid);
        long start = System.currentTimeMillis();

        String url = baseUrl + "/api/system-a/citizens/" + nid + "/dob";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Service-API-Key", apiKey);
        headers.setBearerAuth(serviceJwtProvider.generateServiceToken());
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<DobResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, DobResponse.class);

            long took = System.currentTimeMillis() - start;
            log.info("System A returned DOB in {} ms", took);
            return response.getBody();

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // 4xx — consent issue
            WardApiError apiError = parseError(e.getResponseBodyAsString());
            String errorCode = apiError != null ? apiError.getError() : "UNKNOWN";
            String message = apiError != null ? apiError.getMessage() : "Request denied";
            log.warn("System A denied request: {} — {}", errorCode, message);
            throw new ConsentDeniedException(errorCode, message);

        } catch (org.springframework.web.client.HttpServerErrorException e) {
            // 5xx — System A internal error
            WardApiError apiError = parseError(e.getResponseBodyAsString());
            String message = apiError != null ? apiError.getMessage() : "System A error";
            log.error("System A returned server error: {}", message);
            throw new SourceUnavailableException("System A error: " + message);

        } catch (ResourceAccessException e) {
            // Connection failure, timeout
            log.error("System A unreachable: {}", e.getMessage());
            throw new SourceUnavailableException(
                    "System A (Ward Office) is unavailable: " + e.getMessage());
        }
    }

    public ConsentStatusResponse checkConsentStatus(String nid) {
        log.info("Checking consent status with System A for NID: {}", nid);

        String url = baseUrl + "/api/system-a/citizens/" + nid + "/consent-status";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Service-API-Key", apiKey);
        headers.setBearerAuth(serviceJwtProvider.generateServiceToken());
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ConsentStatusResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, ConsentStatusResponse.class);
            return response.getBody();

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            WardApiError apiError = parseError(e.getResponseBodyAsString());
            String errorCode = apiError != null ? apiError.getError() : "UNKNOWN";
            String message = apiError != null ? apiError.getMessage() : "Consent check denied";
            throw new ConsentDeniedException(errorCode, message);

        } catch (org.springframework.web.client.HttpServerErrorException e) {
            throw new SourceUnavailableException("System A error");

        } catch (ResourceAccessException e) {
            log.error("System A unreachable: {}", e.getMessage());
            throw new SourceUnavailableException(
                    "System A (Ward Office) is unavailable: " + e.getMessage());
        }
    }

    private WardApiError parseError(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(body, WardApiError.class);
        } catch (Exception e) {
            log.warn("Failed to parse System A error body: {}", body);
            return null;
        }
    }
}