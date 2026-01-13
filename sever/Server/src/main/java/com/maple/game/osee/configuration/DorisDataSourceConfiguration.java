package com.maple.game.osee.configuration;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.maple.game.*.*.doris.mapper", sqlSessionFactoryRef = "dorisSqlSessionFactory")
public class DorisDataSourceConfiguration {

    @Bean("dorisDataSource")
    @ConfigurationProperties(prefix = "spring.mysql.doris")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("dorisSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {

        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();

        sessionFactory.setDataSource(dataSource());

        // 开启驼峰式命名转换
        sessionFactory.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);

        return sessionFactory.getObject();

    }

    @Bean("dorisSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory());
    }

}
