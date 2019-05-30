package ts.tsc.system.config.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ts.tsc.system.config.web.base.BaseConfig;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = {"ts.tsc.system.repository", "ts.tsc.authentication.repository"})
@EnableTransactionManagement
@ComponentScan(basePackages  = {"ts.tsc.system", "ts.tsc.authentication"} )
@PropertySource("classpath:application.properties")
public class DataServiceConfig extends BaseConfig {

    private final Environment environment;
    private final static Logger logger
            = LoggerFactory.getLogger(DataServiceConfig.class);

    @Autowired
    public DataServiceConfig(Environment environment) {
        super(environment);
        this.environment = environment;
    }

    @Bean
    @Override
    public Properties hibernateProperties() {
        Properties properties = super.hibernateProperties();
        properties.put("hibernate.dialect",
                Objects.requireNonNull(
                        environment.getProperty("spring.jpa.properties.hibernate.dialect")));
        return properties;
    }

    @Bean
    public DataSource dataSource() {
        try {
            logger.info("Инициализация БД");
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(Objects.requireNonNull(environment
                    .getProperty("spring.datasource.driver-class-name")));
            dataSource.setUrl(environment
                    .getProperty("spring.datasource.url"));
            dataSource.setUsername(environment
                    .getProperty("spring.datasource.username"));
            dataSource.setPassword(environment
                    .getProperty("spring.datasource.password"));
            return dataSource;
        } catch (Exception e) {
            logger.error("Не удалось подключиться к БД", e);
            return null;
        }
    }
}