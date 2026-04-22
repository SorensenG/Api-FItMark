package com.Sorensen.FitMark.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teste")
public class TestController {
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public String test() {

        return "Server Rodando";
    }


}
