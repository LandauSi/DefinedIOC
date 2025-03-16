package com.atyun;

import com.atyun.application.classpackage.controller.UserController;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        MyApplicationContext myApplicationContext = new MyApplicationContext("com.atyun.application.classpackage.controller");
        UserController userController = (UserController) myApplicationContext.getBean("UserController");
        userController.sayHello();
        System.out.println( "Hello World!" );
    }
}
