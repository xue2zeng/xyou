package com.xyou.edu.user.controller;

import com.xyou.edu.user.service.UserService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class UserController {
  @Resource(name = "userService")
  UserService userService;
}
