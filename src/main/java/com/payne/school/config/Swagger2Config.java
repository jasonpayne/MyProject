package com.payne.school.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket swaggerSpringMvcPlugin() {

        List<Parameter> pars = new ArrayList<Parameter>();

        /*User userEntity = userService.getUserByName("bitmain");
        if(userEntity!=null){
            ParameterBuilder tokenPar = new ParameterBuilder();
            long _100yearSec = 100L * 365 * 24 * 60 * 60;
            String token = authService.getToken(userEntity,_100yearSec);
            tokenPar.name(Constants.JWT_HEADER_NAME).description("令牌").modelRef(new ModelRef("string")).
                    parameterType("header").defaultValue(Constants.JWT_TOKEN_PREFIX + token).required(false).build();
            pars.add(tokenPar.build());
        }*/

        return new Docket(DocumentationType.SWAGGER_2).globalOperationParameters(pars).select().
                apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).build();
    }
}
