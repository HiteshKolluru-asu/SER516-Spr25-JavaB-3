package com.example.efferent_coupling_api.service;

import com.example.efferent_coupling_api.util.JavaParserUtil;
import com.example.efferent_coupling_api.util.ZipExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class EfferentCouplingService {
    private static final String UPLOAD_DIR = "uploaded-codebases/";

    public Map<String, Integer> processZipFile(MultipartFile file) throws IOException {
        File extractedDir = ZipExtractor.extractZipFile(file, UPLOAD_DIR);
        return JavaParserUtil.computeEfferentCoupling(extractedDir);
    }
}