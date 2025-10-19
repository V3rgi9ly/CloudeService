package com.example.springexample.cloudeservice.controller;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class controllerMainPage {

    @Hidden
    @Tag(name = "Контроллер работы с приложением React", description = "Позволяет получить информацию в приложение в react и отобразить ее на странице браузера")
    @RequestMapping(value = {"/{path:[^\\.]*}", "/api/"})
    public String forward() {
        return "forward:/index.html";
    }

}
