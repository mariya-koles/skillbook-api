package com.skillbook.platform.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;


@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // maps to templates/index.html
    }
}
