package com.defectdensityapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ZipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAnalyzeWithValidZipContainingJavaFile() throws Exception {
        // Create an in-memory ZIP file with a single Java file.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry("Test.java"));
            // This sample Java code includes a potential defect pattern (System.out.println(null))
            String content = "public class Test {\n" +
                             "  public static void main(String[] args) {\n" +
                             "    System.out.println(null);\n" +
                             "  }\n" +
                             "}\n";
            zos.write(content.getBytes());
            zos.closeEntry();
        }
        byte[] zipBytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile("file", "test.zip", "application/zip", zipBytes);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/code-analysis/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLinesOfCode").exists())
                .andExpect(jsonPath("$.totalDefects").exists())
                .andExpect(jsonPath("$.defectDensity").exists());
    }

    @Test
    public void testAnalyzeWithZipNoJavaFiles() throws Exception {
        // Create an in-memory ZIP file that contains a non-Java file.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry("notes.txt"));
            String content = "This is just a text file.";
            zos.write(content.getBytes());
            zos.closeEntry();
        }
        byte[] zipBytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile("file", "test.zip", "application/zip", zipBytes);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/code-analysis/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLinesOfCode").value(0))
                .andExpect(jsonPath("$.totalDefects").value(0))
                .andExpect(jsonPath("$.defectDensity").value("0.00"));
    }

    @Test
    public void testAnalyzeWithInvalidZip() throws Exception {
        // Provide an invalid ZIP file (random bytes) to simulate an error.
        byte[] invalidZip = "Not a zip file".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "invalid.zip", "application/zip", invalidZip);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/code-analysis/upload")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void testAnalyzeWithMultipleJavaFiles() throws Exception {
        // Create an in-memory ZIP file with multiple Java files.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            // First Java file
            zos.putNextEntry(new ZipEntry("FileOne.java"));
            String content1 = "public class FileOne {\n" +
                              "  void foo() { System.out.println(\"Hello\"); }\n" +
                              "}\n";
            zos.write(content1.getBytes());
            zos.closeEntry();

            // Second Java file
            zos.putNextEntry(new ZipEntry("FileTwo.java"));
            String content2 = "public class FileTwo {\n" +
                              "  void bar() { int a = 5; }\n" +
                              "}\n";
            zos.write(content2.getBytes());
            zos.closeEntry();
        }
        byte[] zipBytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile("file", "multiple.zip", "application/zip", zipBytes);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/code-analysis/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLinesOfCode").exists())
                .andExpect(jsonPath("$.totalDefects").exists())
                .andExpect(jsonPath("$.defectDensity").exists());
    }
}
