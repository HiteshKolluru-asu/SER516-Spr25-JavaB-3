package com.defectdensityapi.Controller;

import com.defectdensityapi.util.GithubLinkOwnerRepoExtractor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.defectdensityapi.util.LocApiAdapter;

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
    private final LocApiAdapter locApiAdapter;
    
    public GitHubDefectController(LocApiAdapter locApiAdapter) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.locApiAdapter = locApiAdapter;
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

            int totalLinesOfCode = locApiAdapter.getTotalLinesOfCode();

            if (totalLinesOfCode == 0) {
                return "Defect Density: N/A (Total lines of code is zero)";
            }
    
            // Calculate defect density per 1000 lines of code
            double defectDensityPerKLOC = (openIssuesCount * 1000.0) / totalLinesOfCode;

            return String.format("%.2f", defectDensityPerKLOC);

        }
        catch(Exception e){
            return e.getMessage();
        }
    }

    @GetMapping("/loc-mock")
    public ResponseEntity<Map<String, Object>> mockLinesOfCodeApi() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("totalLinesOfCode", locApiAdapter.getTotalLinesOfCode()); 
        return ResponseEntity.ok(mockResponse);
    }
    

}