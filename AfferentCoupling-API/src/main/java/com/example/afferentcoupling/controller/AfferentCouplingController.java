package com.example.afferentcoupling.controller;

import com.example.afferentcoupling.service.AfferentCouplingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/afferent-coupling")
public class AfferentCouplingController {
    private final AfferentCouplingService afferentCouplingService;

    public AfferentCouplingController(AfferentCouplingService afferentCouplingService) {
        this.afferentCouplingService = afferentCouplingService;
    }

    @PostMapping("/upload")
    public Map<String, Integer> uploadZip(@RequestParam("file") MultipartFile file) {
        return afferentCouplingService.processZipFile(file);
    }
}