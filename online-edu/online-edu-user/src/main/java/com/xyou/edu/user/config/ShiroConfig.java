package com.xyou.edu.user.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

  /**
   * 开启对注解 `@RequirePermission` 的支持，要结合DefaultAdvisorAutoProxyCreator一起使用，或者导入aop的依赖
   */
  @Bean
  public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
    AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
    authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
    return authorizationAttributeSourceAdvisor;
  }

  /**
   * 自己实现的 Realm，Shiro 的认证最终都交给 Realm 进行执行了。我们需要自己实现一个 Realm，继承自 AuthrozingRealm
   */
  @Bean
  public UserRealm userRealm() {
    return new UserRealm();
  }

  @Bean
  public SecurityManager securityManager() {
    return new DefaultWebSecurityManager(userRealm());
  }

  /**
   * Shiro认证过滤器，设置过滤规则
   * authc 表示需要验证身份才能访问，anon 表示不需要
   * @param securityManager
   * @return
   */
  @Bean
  public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
    ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
    shiroFilterFactoryBean.setSecurityManager(securityManager);
    // 拦截器.
    Map<String,String> filterChainDefinitionMap = Maps.newLinkedHashMap();
    /// 权限配置
    // filterChainDefinitionMap.put("/stu/addStu","perms[student:aaaa]");
    // 配置不会被拦截的链接 顺序判断  相关静态资源
    filterChainDefinitionMap.put("/assets/**", "anon");
    filterChainDefinitionMap.put("/css/**", "anon");
    filterChainDefinitionMap.put("/font/**", "anon");
    filterChainDefinitionMap.put("/images/**", "anon");
    filterChainDefinitionMap.put("/js/**", "anon");
    filterChainDefinitionMap.put("/products/**", "anon");
    filterChainDefinitionMap.put("/Widget/**", "anon");
    filterChainDefinitionMap.put("/swagger-ui.html", "anon");
    filterChainDefinitionMap.put("/swagger-resources", "anon");
    filterChainDefinitionMap.put("/swagger-resources/configuration/security", "anon");
    filterChainDefinitionMap.put("/swagger-resources/configuration/ui", "anon");
    filterChainDefinitionMap.put("/v2/api-docs", "anon");
    filterChainDefinitionMap.put("/webjars/springfox-swagger-ui/**", "anon");

    //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
    filterChainDefinitionMap.put("/logout", "logout");

    //<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;

    //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
    filterChainDefinitionMap.put("/**", "authc");
    // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
    shiroFilterFactoryBean.setLoginUrl("/login");
    // 登录成功后要跳转的链接
    shiroFilterFactoryBean.setSuccessUrl("/index");

    //未授权界面;
    shiroFilterFactoryBean.setUnauthorizedUrl("/403");
    shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    return shiroFilterFactoryBean;
  }
}
