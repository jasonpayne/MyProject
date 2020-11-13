package com.payne.school.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 数据库配置
 */
@Configuration
@MapperScan(basePackages = "com.payne.school.mapper", sqlSessionTemplateRef = "schoolSqlSessionTemplate")
public class SchoolDataBaseConfig {

    @Primary
    @Bean(name = "schoolSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("schoolSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Primary
    @Bean(name = "schoolSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("schoolDataSource") DataSource schoolDataSource) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(schoolDataSource);
        bean.setMapperLocations(resolver.getResources("classpath:com/payne/school/mapper/*.xml"));
        return bean.getObject();
    }

    @Primary
    @Bean(name = "schoolDataSource")
    @ConfigurationProperties(prefix = "db.school")
    public DataSource schoolDataSource() {
        return DataSourceBuilder.create().build();
    }
}
