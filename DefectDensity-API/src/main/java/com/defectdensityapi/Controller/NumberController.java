package com.defectdensityapi.Controller;

import com.defectdensityapi.Model.RequestedNumber;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Random;

@RestController
public class NumberController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the Random Number Generator! Use /random to get a random number or /requestedNumber to get the requested number.";
    }

    @GetMapping("/random")
    public RequestedNumber randomNumber() {
        Random rand = new Random();
        return new RequestedNumber(rand.nextInt(1000));
    }

    @GetMapping("/requestedNumber")
    public RequestedNumber requestedNumber(@RequestParam(value = "number", defaultValue = "00") String number) {
        return new RequestedNumber(Integer.parseInt(number));
    }
}