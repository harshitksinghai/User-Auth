package com.harshitksinghai.UserEntry.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/main")
public class MainController {
    @GetMapping("/hi")
    public String hi(){
        return "inside /api/main/hi";
    }
}
