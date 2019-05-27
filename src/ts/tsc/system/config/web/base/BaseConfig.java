package ts.tsc.system.config.web.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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
@PropertySource("classpath:application.properties")
public abstract class BaseConfig {

    @Autowired
    private final Environment environment;

    protected BaseConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public abstract DataSource dataSource();

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory());
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public Properties hibernateProperties() {
        Properties hibernateProp = new Properties();
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
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean
                = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("ts.tsc.system.entity");
        factoryBean.setDataSource(dataSource());
        factoryBean.setJpaProperties(hibernateProperties());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.afterPropertiesSet();
        return factoryBean.getNativeEntityManagerFactory();
    }
}
