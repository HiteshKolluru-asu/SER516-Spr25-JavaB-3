package com.defectdensityapi.Controller;

import com.defectdensityapi.util.GithubLinkOwnerRepoExtractor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/defects")
public class GitHubDefectController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GitHubDefectController() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping("/{owner}/{repo}")
    public int getDefectCount(@PathVariable String owner, @PathVariable String repo) throws Exception {
        // Build the GitHub API endpoint for the repository metadata
        String url = "https://api.github.com/repos/" + owner + "/" + repo;

        // Retrieve the JSON response from GitHub
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Parse the JSON to extract the "open_issues_count" field
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        int openIssuesCount = rootNode.get("open_issues_count").asInt();

        // Return the number of open issues in the repo
        return openIssuesCount;
    }

    @GetMapping("/repo")
    public String getDefectRepoCount(@RequestParam(value="url") String url1) throws Exception {

        try{
            String repoUrl = GithubLinkOwnerRepoExtractor.extractOwnerRepo(url1);
            if(repoUrl == null){
                return "URL is bad"; // something is wrong with the url
            }

            String url = "https://api.github.com/repos/" + repoUrl;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            int openIssuesCount = rootNode.get("open_issues_count").asInt();

            return Integer.toString(openIssuesCount);
        }
        catch(Exception e){
            return e.getMessage();
        }
    }

    @GetMapping("/loc-mock")
    public ResponseEntity<Map<String, Object>> mockLinesOfCodeApi() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("totalLinesOfCode", 5000); // some placeholder value for the mocking response
        return ResponseEntity.ok(mockResponse);
    }

    private int getTotalLinesOfCode() {
        String locApiUrl = "http://localhost:8083/api/defects/loc-mock"; // Mocking API URL
    
        RestTemplate restTemplate = new RestTemplate();
    
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(locApiUrl, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (int) response.getBody().get("totalLinesOfCode");
            }
        } catch (Exception e) {
            System.out.println("Error calling LOC API: " + e.getMessage());
        }
    
        return 1000; // the base return value
    }
    

}