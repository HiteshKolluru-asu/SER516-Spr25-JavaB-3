package com.defectdensityapi.Controller;

import com.defectdensityapi.Service.CodeAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/code-analysis")
public class DefectDensityAnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(DefectDensityAnalysisController.class);
    private final CodeAnalysisService codeAnalysisService;

    public DefectDensityAnalysisController(CodeAnalysisService codeAnalysisService) {
        this.codeAnalysisService = codeAnalysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeCode(@RequestParam("file") MultipartFile zipFile) {
        File tempFile = null;
        try {
            // Save the uploaded file to a temporary file.
            tempFile = File.createTempFile("uploaded", ".zip");
            zipFile.transferTo(tempFile);

            // Delegate analysis to the service layer.
            Map<String, Object> analysis = codeAnalysisService.analyzeCode(tempFile);
            return ResponseEntity.ok(analysis);
        } catch (IOException e) {
            logger.error("Error processing ZIP file: {}", e.getMessage(), e);
            return buildErrorResponse("Error processing ZIP file: " + e.getMessage());
        } finally {
            if (tempFile != null && tempFile.exists()) {
                if (!tempFile.delete()) {
                    logger.warn("Temporary file {} could not be deleted.", tempFile.getAbsolutePath());
                }
            }
        }
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.badRequest().body(error);
    }
}