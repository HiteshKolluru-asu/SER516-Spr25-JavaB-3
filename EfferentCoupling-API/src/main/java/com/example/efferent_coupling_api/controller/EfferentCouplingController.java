package com.example.efferent_coupling_api.controller;

import com.example.efferent_coupling_api.service.EfferentCouplingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/efferent-coupling")
public class EfferentCouplingController {
    private final EfferentCouplingService couplingService;

    public EfferentCouplingController(EfferentCouplingService couplingService) {
        this.couplingService = couplingService;
    }

    @PostMapping("/upload")
    public Map<String, Integer> analyzeZipFile(@RequestParam("file") MultipartFile file) throws IOException {
        return couplingService.processZipFile(file);
    }
}