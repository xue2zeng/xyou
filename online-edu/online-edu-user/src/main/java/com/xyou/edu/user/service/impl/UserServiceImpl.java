package com.xyou.edu.user.service.impl;

import com.xyou.edu.core.domian.user.User;
import com.xyou.edu.user.repository.UserRepository;
import com.xyou.edu.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

  @Autowired
  UserRepository userRepository;

  @Override
  public User findByUserName(String userName) {
    return userRepository.findByUserName(userName);
  }
}
