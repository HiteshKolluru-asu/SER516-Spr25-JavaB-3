package com.example.afferentcoupling.controller;

import com.example.afferentcoupling.service.AfferentCouplingService;
import com.example.afferentcoupling.util.ZipExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AfferentCouplingControllerTest {

    private AfferentCouplingController afferentCouplingController;
    private AfferentCouplingService afferentCouplingService;

    @BeforeEach
    void setUp() {
        afferentCouplingService = new AfferentCouplingService();
        afferentCouplingController = new AfferentCouplingController(afferentCouplingService);
    }

    @Test
    void uploadZip_ShouldReturnProcessedData() throws IOException {
        MultipartFile mockZipFile = mock(MultipartFile.class);

        // Mock Static ZipExtractor
        try (MockedStatic<ZipExtractor> mockedZipExtractor = Mockito.mockStatic(ZipExtractor.class)) {
            mockedZipExtractor.when(() -> ZipExtractor.extractJavaFiles(mockZipFile))
                    .thenReturn(Arrays.asList(
                            "package com.example.demo;\nimport com.example.service.UserService;\npublic class UserController {}",
                            "package com.example.service;\npublic class UserService {}"
                    ));

            Map<String, Integer> result = afferentCouplingController.uploadZip(mockZipFile);

            assertEquals(1, result.get("com.example.service.UserService"));
            assertEquals(0, result.get("com.example.demo.UserController"));
        }
    }
}
