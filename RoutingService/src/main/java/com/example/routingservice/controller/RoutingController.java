package com.example.routingservice.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api")
public class RoutingController {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String AFFERENT_COUPLING_API = "http://afferent-coupling-api:8081/api/afferent-coupling/upload";
    private static final String EFFERENT_COUPLING_API = "http://efferent-coupling-api:8082/api/efferent-coupling/upload";

    @PostMapping("/{service}/upload")
    @Async
    public CompletableFuture<ResponseEntity<String>> routeMultipartRequest(
            @PathVariable String service,
            @RequestParam("file") MultipartFile file
    ) {
        return CompletableFuture.supplyAsync(() -> {

        String targetUrl;

        switch (service.toLowerCase()) {
            case "afferent-coupling":
                targetUrl = AFFERENT_COUPLING_API;
                break;
            case "efferent-coupling":
                targetUrl = EFFERENT_COUPLING_API;
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid service name: " + service);
        }

        try {
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            return restTemplate.exchange(targetUrl, HttpMethod.POST, requestEntity, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while forwarding multipart request: " + e.getMessage());
        }
    });
    }

    @GetMapping("/{service}/repo")
    @Async
    public CompletableFuture<ResponseEntity<String>> routeGetRequest(
            @PathVariable String service,
            @RequestParam("url") String urlParam
    ) {
        return CompletableFuture.supplyAsync(() -> {

            // Only handle 'code-analysis' for the GET call here
            if (!service.equalsIgnoreCase("defects")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid or unsupported service name for GET /repo: " + service);
            }

            // Forward to the new defect density GET endpoint
            String targetUrl = "http://defect-density-api:8083/api/defects/repo";

            try {
                // Build the URI with query param 'url'
                String uri = UriComponentsBuilder.fromHttpUrl(targetUrl)
                                                 .queryParam("url", urlParam)
                                                 .toUriString();

                // Forward the GET request
                return restTemplate.getForEntity(uri, String.class);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error while forwarding GET request: " + e.getMessage());
            }
        });
    }

}
