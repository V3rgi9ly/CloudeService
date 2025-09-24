package com.example.springexample.cloudeservice.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class controllerMainPage {


//    @GetMapping(value = {
//            "/",
//            "/login",
//            "/register",
//            "/dashboard",
//            "/profile",
//            "/{path:[^\\.]*}"  // Любой путь без точки (не статический файл)
//    })
//    public String forwardToReact() {
//        return "forward:/index.html"; // React index.html
//    }

    @RequestMapping(value = {"/{path:[^\\.]*}", "/api/"})
    public String forward() {
        return "forward:/index.html";
    }

}
