package com.example.springexample.cloudeservice.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class controllerMainPage {

    @RequestMapping(value = {"/{path:[^\\.]*}", "/api/"})
    public String forward() {
        return "forward:/index.html";
    }

}
