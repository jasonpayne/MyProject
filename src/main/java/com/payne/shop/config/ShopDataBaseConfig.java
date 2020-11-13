package com.payne.shop.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 数据库配置
 */
@Configuration
@MapperScan(basePackages = "com.payne.shop.mapper", sqlSessionTemplateRef = "shopSqlSessionTemplate")
public class ShopDataBaseConfig {

    @Bean(name = "shopSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("shopSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "shopSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("shopDataSource") DataSource shopDataSource) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(shopDataSource);
        bean.setMapperLocations(resolver.getResources("classpath:com/payne/shop/mapper/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "shopDataSource")
    @ConfigurationProperties(prefix = "db.shop")
    public DataSource shopDataSource() {
        return DataSourceBuilder.create().build();
    }
}
