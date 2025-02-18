package com.defectdensityapi.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CustomErrorController implements ErrorController {


    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        return "Oops! The page you're looking for doesn't exist. Use /random to get a random number or /requestedNumber to get the requested number";
    }

    public String getErrorPath() {
        return "/error";
    }
}