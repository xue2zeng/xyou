package com.xyou.edu.user.config;

import com.xyou.edu.core.domian.user.Permission;
import com.xyou.edu.core.domian.user.Role;
import com.xyou.edu.core.domian.user.User;
import com.xyou.edu.core.util.codec.EncodeUtils;
import com.xyou.edu.core.util.codec.Sha1Utils;
import com.xyou.edu.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 身份校验核心类
 */
@Slf4j
public class UserRealm extends AuthorizingRealm {
  private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
  // 哈希迭代次数
  public static final int HASH_INTERATIONS = 2;
  public static final int SALT_SIZE = 8;

  @Resource(name = "userService")
  UserService userService;

  /**
   * 生成密文密码，生成随机的16位salt并经过1024次 sha-1 hash
   * @param plainPassword 明文密码
   * @return 16位salt密钥  + 40位hash密码
   */
  public String encryptPassword(String plainPassword) {
    String plain = EncodeUtils.decodeHtml(plainPassword);
    byte[] salt = Sha1Utils.genSalt(SALT_SIZE);
    byte[] hashPassword = Sha1Utils.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
    return EncodeUtils.encodeHex(salt) + EncodeUtils.encodeHex(hashPassword);
  }

  /**
   * 此方法调用 hasRole,hasPermission 的时候才会进行回调.
   * 权限信息.(授权):
   * 1、如果用户正常退出，缓存自动清空；
   * 2、如果用户非正常退出，缓存自动清空；
   * 3、如果我们修改了用户的权限，而用户不退出系统，修改的权限无法立即生效。
   * （需要手动编程进行实现；放在 service 进行调用）
   * 在权限修改后调用 realm 中的方法，realm 已经由 spring 管理，所以从 spring 中获取 realm 实例，
   * 调用 clearCached 方法；
   * :Authorization 是授权访问控制，用于对用户进行的操作授权，证明该用户是否允许进行当前操作，如访问某个链接，某个资源文件等。
   * @param principalCollection
   * @return
   */
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
    User user = (User) principalCollection.getPrimaryPrincipal();
    if (log.isDebugEnabled()) {
      log.debug("===> 用户[{}]进行授权配置", user.getUserName());
    }

    for (Role role : user.getRoles()) {
      authorizationInfo.addRole(role.getName());
      for (Permission permission : role.getPermissions()) {
        authorizationInfo.addStringPermission(permission.getName());
      }
    }

    return authorizationInfo;
  }

  /**
   * 认证信息.(身份验证)
   * @param authenticationToken
   * @return
   * @throws AuthenticationException
   */
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
    String userName = (String) authenticationToken.getPrincipal();
    if (log.isDebugEnabled()) {
      log.debug("===> 用户[{}]进行身份认证", userName);
    }
    User user = userService.findByUserName(userName);

    if (Objects.isNull(user)) {
      return null;
    }


    // 加密方式：交给 AuthenticatingRealm 使用 CredentialsMatcher 进行密码匹配
    SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
            user,
            user.getPassword(),
            ByteSource.Util.bytes(user.getSalt()),
            getName()
    );
    return authenticationInfo;
  }


  /**
   * 设置认证加密方式
   */
  @Override
  public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
    HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
    hashedCredentialsMatcher.setHashAlgorithmName(Sha256Hash.ALGORITHM_NAME);
    hashedCredentialsMatcher.setHashIterations(HASH_INTERATIONS);
    super.setCredentialsMatcher(credentialsMatcher);
  }
}
