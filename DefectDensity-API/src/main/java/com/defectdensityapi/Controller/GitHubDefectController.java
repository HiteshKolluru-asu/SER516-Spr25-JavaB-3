package com.defectdensityapi.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.defectdensityapi.util.GithubLinkOwnerRepoExtractor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

        try {
            String repoUrl = GithubLinkOwnerRepoExtractor.extractOwnerRepo(url1);
            if (repoUrl == null) {
                return "Error: Invalid GitHub repository URL format."; // More descriptive error message
            }
        
            // Validate that the URL belongs to GitHub
            if (!url1.startsWith("https://github.com/")) {
                return "Error: Provided URL is not a valid GitHub repository.";
            }
        
            String url = "https://api.github.com/repos/" + repoUrl;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
            // Check if response body is null
            if (response.getBody() == null) {
                return "Error: Empty response from GitHub API.";
            }
        
            JsonNode rootNode = objectMapper.readTree(response.getBody());
        
            // Ensure "open_issues_count" exists in the response
            if (!rootNode.has("open_issues_count")) {
                return "Error: 'open_issues_count' field is missing in GitHub API response.";
            }
        
            int openIssuesCount = rootNode.get("open_issues_count").asInt();
        
            return Integer.toString(openIssuesCount);
        } 
        catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            return "Error: Repository not found. Please check the GitHub URL.";
        } 
        catch (org.springframework.web.client.RestClientException e) {
            return "Error: Unable to reach GitHub API. Please try again later.";
        } 
        catch (Exception e) {
            return "Error: An unexpected error occurred - " + e.getMessage();
        }
        
    }
}