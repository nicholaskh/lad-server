package com.lad.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/12/27
 */
@Configuration
@EnableSwagger2
@EnableWebMvc
@ComponentScan(basePackages ={"com.lad.controller"})
public class SwaggerConfig extends WebMvcConfigurationSupport {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).forCodeGeneration(true).
                select().apis(RequestHandlerSelectors.basePackage("com.lad"))
                //过滤生成链接
                .paths(PathSelectors.any()).build()
                .apiInfo(apiInfo());
    }

    //api接口作者相关信息
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().license("Apache License Version 2.0")
                .title("ttlaoyou后端RESTful API V1.0").description("接口文档说明")
                .contact(new Contact("dongensi", "", "dongensi@ttlaoyou.com"))
                .version("1.0")
                .termsOfServiceUrl("http://180.76.173.200:9999")
                .build();
    }
}
