package com.payne;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@SpringBootApplication
@RestController
public class PayneBabyApp {

    public static void main(String[] args) {
        SpringApplication.run(PayneBabyApp.class, args);
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getCommonsMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(20971520);
        multipartResolver.setMaxInMemorySize(1048576);
        return multipartResolver;
    }

    /**
     * 配置文件上传大小
     */
//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        //单个文件大小30mb
//        factory.setMaxFileSize(DataSize.ofMegabytes(30L));
//        //设置总上传数据大小30mb
//        factory.setMaxRequestSize(DataSize.ofMegabytes(30L));
//        return factory.createMultipartConfig();
//    }
}
