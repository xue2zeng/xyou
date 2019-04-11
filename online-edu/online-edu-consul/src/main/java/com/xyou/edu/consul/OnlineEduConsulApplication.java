package com.xyou.edu.consul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class OnlineEduConsulApplication {
  public static void main(String[] args) {
    SpringApplication.run(OnlineEduConsulApplication.class, args);
  }
}
