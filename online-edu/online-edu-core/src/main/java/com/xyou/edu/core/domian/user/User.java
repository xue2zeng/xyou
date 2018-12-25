package com.xyou.edu.core.domian.user;

import com.xyou.edu.core.domian.base.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "user")
public class User extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "user_id")
  Long id;
  @Column(name = "user_name", nullable = false, length = 40)
  String userName;
  @Column(nullable = false, length = 100)
  String password;
  @Column(length = 60)
  String salt;
  @Column(name = "real_name", length = 100)
  String realName;
  @Column(length = 20)
  String phone;
  @Column(length = 4)
  Character gender;
  Integer age;
  Date birthday;
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id") })
  Set<Role> roles;
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_permission", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "permission_id") })
  Set<Permission> permissions;

  public String getCredentialsSalt() {
    return  userName + salt;
  }
}
