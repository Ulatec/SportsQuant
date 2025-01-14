package BaseballQuant.Config;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("application.properties")
@EnableJpaRepositories("BaseballQuant.Repository")
public class Config {

    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(env.getProperty("sportsquant.mlb-db.package"));
        factory.setDataSource(dataSource());
        factory.setJpaProperties(getHibernateProperties());
        return factory;
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        // Driver class name
        ds.setDriverClassName(env.getProperty("sportsquant.mlb-db.driver"));
        // Set URL
        ds.setUrl(env.getProperty("sportsquant.mlb-db.url"));
        // Set username & password
        ds.setUsername(env.getProperty("sportsquant.mlb-db.username"));
        ds.setPassword(env.getProperty("sportsquant.mlb-db.password"));

        return ds;
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        properties.put("hibernate.implicit_naming_strategy",env.getProperty("hibernate.implicit_naming_strategy"));
        properties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        return properties;
    }
//    @Configuration
//    @PropertySource({"classpath:persistence-multiple-db-boot.properties"})
//    @EnableJpaRepositories(
//            basePackages = "com.baeldung.multipledb.dao.product",
//            entityManagerFactoryRef = "productEntityManager",
//            transactionManagerRef = "productTransactionManager")
//    public class PersistenceProductAutoConfiguration {
//
//        @Bean
//        @ConfigurationProperties(prefix="spring.second-datasource")
//        public DataSource productDataSource() {
//            return DataSourceBuilder.create().build();
//        }

        // productEntityManager bean

        // productTransactionManager bean
 //   }

}
