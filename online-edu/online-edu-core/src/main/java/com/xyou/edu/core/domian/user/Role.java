package com.xyou.edu.core.domian.user;

import com.xyou.edu.core.domian.base.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Role extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "role_id")
  Long id;
  @Column(nullable = false, length = 40)
  String name;
  @Column(length = 100)
  String title;
  @Column(nullable = false, length = 2)
  Integer enabled;
  Integer orderNum;
  // 用户 - 角色关系定义:一个角色对应多个用户;
  @ManyToMany
  @JoinTable(name="user_role",joinColumns={@JoinColumn(name="role_id")},inverseJoinColumns={@JoinColumn(name="user_id")})
  Set<User> users;
  //角色 -- 权限关系：多对多关系;
  @ManyToMany(fetch= FetchType.EAGER)
  @JoinTable(name="role_permission",joinColumns={@JoinColumn(name="role_id")},inverseJoinColumns={@JoinColumn(name="permission_id")})
  Set<Permission> permissions;
}
