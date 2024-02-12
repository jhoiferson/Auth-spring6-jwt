package lpj.web.developers.auth.com.persistence;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")

public class DataSourceConfiguration {

	
	@Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Value("${spring.datasource.driver-class-name}")
    private String datasourceDriverClassName;
    
	// Configuración del EntityManagerFactory para JPA y Hibernate
	@Bean
	public EntityManagerFactory entityManagerFactory() {
		HibernateJpaVendorAdapter jpaAdapter = new HibernateJpaVendorAdapter();
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(jpaAdapter);
		factory.setPackagesToScan(new String[] { "lpj.web.developers.auth.com.models.entity" });
		factory.setDataSource(dataSource());
		factory.setJpaProperties(addProperties());
		factory.afterPropertiesSet();
		factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
		return factory.getObject();
	}

	  @Bean
	    public DataSource dataSource() {
	        DriverManagerDataSource driver = new DriverManagerDataSource();
	        driver.setDriverClassName(datasourceDriverClassName);
	        driver.setUrl(datasourceUrl);
	        driver.setUsername(datasourceUsername);
	        driver.setPassword(datasourcePassword);
	        return driver;
	    }

	// Configuración del JdbcTemplate para acceder a la base de datos de forma
	// tradicional
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource datasource) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		return jdbcTemplate;
	}

	// Configuración del NamedParameterJdbcTemplate para acceder a la base de datos
	// con parámetros nombrados
	@Bean
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	// Configuración del administrador de transacciones para JPA
	@Bean
	public PlatformTransactionManager transactionManager() {
		EntityManagerFactory emf = entityManagerFactory();
		return new JpaTransactionManager(emf);
	}

	
	// Propiedades específicas de Hibernate
	Properties addProperties() {
	    Properties properties = new Properties();
	    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect"); // Usamos MySQL8Dialect para MySQL 8.x
	    properties.setProperty("hibernate.hbm2ddl.auto", "update");
	    properties.setProperty("hibernate.show_sql", "true");
	    properties.setProperty("hibernate.format_sql", "true");
	    return properties;
	}
}
