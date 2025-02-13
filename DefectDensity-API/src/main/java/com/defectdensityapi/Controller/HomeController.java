package com.defectdensityapi.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the Random Number Generator! Use /random to get a random number or /requestedNumber to get the requested number.";
    }
}