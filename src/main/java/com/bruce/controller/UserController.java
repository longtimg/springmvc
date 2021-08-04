package com.bruce.controller;

import com.bruce.pojo.User;
import com.bruce.service.UserService;
import com.springmvc.annotation.AutoWired;
import com.springmvc.annotation.Controller;
import com.springmvc.annotation.RequestMapping;
import com.springmvc.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class UserController {
    @AutoWired
    private UserService userservice;
    @RequestMapping("/query/user")
    public void findUsers(HttpServletRequest request, HttpServletResponse response,@RequestParam("name") String name){
        List<User> user = userservice.findUser(name);

        response.setContentType("text/html;charset=utf8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print("<h1>pringMvc控制器："+name+"</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
