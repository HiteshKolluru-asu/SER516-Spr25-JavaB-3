package com.example.afferentcoupling.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ZipExtractorTest {

    @Test
    void extractJavaFiles_ShouldExtractJavaFilesFromZip() throws IOException {
        // Create a mock ZIP file containing Java files
        byte[] zipData = createZipWithJavaFiles();
        MultipartFile mockFile = new MockMultipartFile("file", "test.zip", "application/zip", zipData);

        // Extracting zip file
        List<String> javaFiles = ZipExtractor.extractJavaFiles(mockFile);

        assertFalse(javaFiles.isEmpty(), "Java files should be extracted from ZIP");
        assertTrue(javaFiles.get(0).contains("public class SampleClass"), "Extracted file should contain Java class");
    }

    private byte[] createZipWithJavaFiles() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            // Add a Java file to the ZIP
            zipOutputStream.putNextEntry(new ZipEntry("SampleClass.java"));
            zipOutputStream.write("public class SampleClass {}".getBytes());
            zipOutputStream.closeEntry();
        }
        return byteArrayOutputStream.toByteArray();
    }

}
