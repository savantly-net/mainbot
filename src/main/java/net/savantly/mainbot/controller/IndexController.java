package net.savantly.mainbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class IndexController {
    
    @RequestMapping
    public String get() {
        return "index";
    }
}
