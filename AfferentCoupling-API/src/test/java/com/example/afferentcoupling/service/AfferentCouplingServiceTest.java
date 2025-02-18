package com.example.afferentcoupling.service;

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
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AfferentCouplingServiceTest {

    private AfferentCouplingService afferentCouplingService;

    @BeforeEach
    void setUp() {
        afferentCouplingService = new AfferentCouplingService();
    }

    @Test
    void processZipFile_ShouldReturnCorrectCoupling() throws IOException {
        MultipartFile mockZipFile = mock(MultipartFile.class);

        // Mock Static ZipExtractor
        try (MockedStatic<ZipExtractor> mockedZipExtractor = Mockito.mockStatic(ZipExtractor.class)) {
            mockedZipExtractor.when(() -> ZipExtractor.extractJavaFiles(mockZipFile))
                    .thenReturn(Arrays.asList(
                            "package com.example.test;\nimport com.example.service.TestService;\npublic class TestController {}",
                            "package com.example.service;\npublic class TestService {}"
                    ));

            Map<String, Integer> result = afferentCouplingService.processZipFile(mockZipFile);
            assertEquals(1, result.get("com.example.service.TestService"));
            assertEquals(0, result.get("com.example.test.TestController"));
        }
    }
}
