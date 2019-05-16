package ts.tsc.system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = {"ts.tsc.system.repositories"})
@ComponentScan(basePackages  = {"ts.tsc.system"} )
@PropertySource("classpath:application.properties")
public class DataServiceConfig {

    private final static Logger logger = LoggerFactory.getLogger(DataServiceConfig.class);
    private final Environment environment;

    @Autowired
    public DataServiceConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {
        try {
            logger.info("Инициализация БД");
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource
                    .setDriverClassName(Objects.requireNonNull(environment
                            .getProperty("spring.datasource.driver-class-name")));
            dataSource.setUrl(environment.getProperty("spring.datasource.url"));
            dataSource.setUsername(environment.getProperty("spring.datasource.username"));
            dataSource.setPassword(environment.getProperty("spring.datasource.password"));
            return dataSource;
        } catch (Exception e) {
            logger.error("Не удалось подключиться к БД!", e);
            return null;
        }
    }

    @Bean
    public Properties hibernateProperties() {
        Properties hibernateProp = new Properties();
        hibernateProp.put("hibernate.dialect",
                Objects.requireNonNull(
                        environment.getProperty("spring.jpa.properties.hibernate.dialect")));
        hibernateProp.put("hibernate.hbm2ddl.auto",
                Objects.requireNonNull(
                        environment.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto")));
        hibernateProp.put("hibernate.show_sql",
                Objects.requireNonNull(
                        environment.getProperty("spring.jpa.properties.hibernate.show_sql")));
        hibernateProp.put("hibernate.max_fetch_depth",
                Objects.requireNonNull(
                        environment.getProperty("spring.jpa.properties.hibernate.max_fetch_depth")));
        hibernateProp.put("hibernate.jdbc.batch_size",
                Objects.requireNonNull(
                        environment.getProperty("spring.jpa.properties.hibernate.jdbc.batch_size")));
        hibernateProp.put("hibernate.jdbc.fetch_size",
                Objects.requireNonNull(
                        environment.getProperty("spring.jpa.properties.hibernate.jdbc.fetch_size")));
        hibernateProp.put("hibernate.jmx.enabled",
                Objects.requireNonNull(
                        environment.getProperty("spring.jpa.properties.hibernate.jmx.enabled")));
        hibernateProp.put("hibernate.generate_statistics",
                Objects.requireNonNull(
                        environment.getProperty("spring.jpa.properties.hibernate.generate_statistics")));
        hibernateProp.put("hibernate.session_factory_name",
                Objects.requireNonNull(
                        environment.getProperty("spring.jpa.properties.hibernate.session_factory_name")));
        return hibernateProp;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory());
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("ts.tsc.system.entities");
        factoryBean.setDataSource(dataSource());
        factoryBean.setJpaProperties(hibernateProperties());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.afterPropertiesSet();
        return factoryBean.getNativeEntityManagerFactory();
    }
}