package com.sawant.demo_jenkins_k8s;


@RestController
public class test {
    @GetMapping ("/Welcome")
    public String Welcome() {
        return "Welcome to my demo on devops";
    }
}
