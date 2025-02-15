package com.example.efferent_coupling_api.controller;

import com.example.efferent_coupling_api.service.CouplingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/efferent-coupling")
public class CouplingController {
    private final CouplingService couplingService;

    public CouplingController(CouplingService couplingService) {
        this.couplingService = couplingService;
    }

    @PostMapping("/upload")
    public Map<String, Integer> analyzeZipFile(@RequestParam("file") MultipartFile file) throws IOException {
        return couplingService.processZipFile(file);
    }
}