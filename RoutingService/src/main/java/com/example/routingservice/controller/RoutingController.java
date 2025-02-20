package com.example.routingservice.controller;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api")
public class RoutingController {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String AFFERENT_COUPLING_API = "http://afferent-coupling-api:8081/api/afferent-coupling/upload";
    private static final String EFFERENT_COUPLING_API = "http://efferent-coupling-api:8082/api/efferent-coupling/upload";
    private static final String DEFECT_DENSITY_API = "http://defect-density-api:8083/api/code-analysis/upload";

    @PostMapping("/{service}/upload")
    public ResponseEntity<String> routeMultipartRequest(
            @PathVariable String service,
            @RequestPart("file") MultipartFile file
    ) {
        String targetUrl;

        switch (service.toLowerCase()) {
            case "afferent-coupling":
                targetUrl = AFFERENT_COUPLING_API;
                break;
            case "efferent-coupling":
                targetUrl = EFFERENT_COUPLING_API;
                break;
            case "code-analysis":
                targetUrl = DEFECT_DENSITY_API;
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid service name: " + service);
        }

        try {
        
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            return restTemplate.exchange(targetUrl, HttpMethod.POST, requestEntity, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while forwarding multipart request: " + e.getMessage());
        }
    }
}
