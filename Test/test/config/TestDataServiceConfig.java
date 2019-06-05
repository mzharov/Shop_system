package test.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ts.tsc.system.config.web.base.BaseConfig;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = {"ts.tsc.system.repository"})
@EnableTransactionManagement
@ComponentScan(basePackages  = {"ts.tsc.system", "ts.tsc.authentication", "test.config"}, excludeFilters = {
        @ComponentScan.Filter(
                type=FilterType.ASSIGNABLE_TYPE,
                value = ts.tsc.system.config.web.DataServiceConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                value = ts.tsc.system.config.web.WebConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                value = ts.tsc.system.config.web.WebInitializer.class)})
@PropertySource("classpath:application.properties")
@Profile("test")
public class TestDataServiceConfig extends BaseConfig {

    private final Environment environment;
    private final static Logger logger
            = LoggerFactory.getLogger(TestDataServiceConfig.class);

    @Autowired
    public TestDataServiceConfig(Environment environment) {
        super(environment);
        this.environment = environment;
    }

    @Bean
    public Properties hibernateProperties() {
        Properties properties = super.hibernateProperties();
        properties.put("hibernate.dialect",
                Objects.requireNonNull(
                        environment.getProperty("spring.jpa.properties.hibernate.dialect.test")));
        return properties;
    }

    @Bean
    public DataSource dataSource() {
        try {
            logger.info("Инициализация тестовой БД");
            EmbeddedDatabaseBuilder dbBuilder = new EmbeddedDatabaseBuilder();
            return dbBuilder.setType(EmbeddedDatabaseType.H2).build();
        } catch (Exception e) {
            logger.error("Не удалось подключиться к БД", e);
            return null;
        }
    }
}