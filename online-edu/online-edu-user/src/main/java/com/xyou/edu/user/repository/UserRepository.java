package com.xyou.edu.user.repository;


import com.xyou.edu.core.domian.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  User findByUserName(String userName);
}
