package com.defectdensityapi.util;

public class GithubLinkOwnerRepoExtractor {

    public static String extractOwnerRepo(String repo) {

        repo = repo.replace(".git", "");
        repo = repo.replace("https://github.com/", "");

        if (repo != null && !repo.isEmpty() && repo.endsWith("/")) {
            return repo.substring(0, repo.length() - 1);
        }

        if(repo.contains("/")) {
            return repo;
        }
        else{
            return null;
        }
    }
}
