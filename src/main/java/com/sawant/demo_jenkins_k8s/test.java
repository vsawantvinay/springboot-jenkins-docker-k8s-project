package com.sawant.demo_jenkins_k8s;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class test {
    @GetMapping ("/Welcome")
    public String Welcome() {
        return "Welcome to my demo on devops!!!";
    }
}
