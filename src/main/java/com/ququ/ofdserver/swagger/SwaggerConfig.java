package com.ququ.ofdserver.swagger;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfig {

    @Bean
    public Docket docket() {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ququ.ofdserver.controller")) //配置扫描路径
                .paths(PathSelectors.any()) // 配置过滤哪些
                .build();
    }

    // api基本信息
    private ApiInfo apiInfo() {
        return new ApiInfo("OFD转换生成服务",
                "从PDF或Word转换生成OFD格式文件并添加删除附件、电子签章",
                "v1.0",
                "",
                new Contact("Macro Zhang", "", "229413@qq.com"),  //作者信息
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList());
    }
}
