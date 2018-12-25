package com.xyou.edu.user.service;

import com.xyou.edu.core.domian.user.User;
import org.springframework.stereotype.Service;

public interface UserService {
  User findByUserName(String userName);
}
