package com.xyou.edu.core.domian.user;

import com.xyou.edu.core.domian.base.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
public class Permission extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "permission_id")
  Long id;
  @Column(name = "parent_id")
  Long parentId;
  @Column(nullable = false, length = 40)
  String name;
  @Column(nullable = false, length = 100)
  String value;
  @Column(nullable = false, length = 4)
  Integer type;
  String uri;
  String icon;
  @Column(nullable = false)
  Integer enabled;
  Integer orderNum;
  @ManyToMany
  @JoinTable(name="role_permission",joinColumns={@JoinColumn(name="permission_id")},inverseJoinColumns={@JoinColumn(name="role_id")})
  Set<Role> roles;
}
