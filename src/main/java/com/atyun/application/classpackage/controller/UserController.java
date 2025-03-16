package com.atyun.application.classpackage.controller;

import com.atyun.application.annotation.Autowired;
import com.atyun.application.annotation.Component;
import com.atyun.application.classpackage.controller.Service.UserService;

@Component
public class UserController {


    @Autowired
    private UserService userService;

    public void sayHello() {
        userService.method();
        System.out.println("hello world!!!");
    }

}
