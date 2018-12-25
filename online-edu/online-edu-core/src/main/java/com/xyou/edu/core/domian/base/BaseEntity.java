package com.xyou.edu.core.domian.base;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {
  String comments;
  @CreatedDate
  @Column(name = "creation_time", nullable = false)
  Date creationTime;
  @Column(name = "creation_user", nullable = false)
  Integer creationUser;
  @Column(name = "creation_user_name", nullable = false, length = 100)
  String creationUserName;
  @LastModifiedDate
  @Column(name = "modified_time")
  Date modifiedTime;
  @Column(name = "modified_user")
  Integer modifiedUser;
  @Column(name = "modified_user_name")
  String modifiedUserName;
}
