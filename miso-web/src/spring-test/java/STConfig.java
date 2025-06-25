package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
// idk what's up with the imports
/*
 * 
 * 
 * import org.springframework.transaction.annotation.EnableTransactionManagement; import
 * org.springframework.orm.jpa.JpaTransactionManager; import
 * org.springframework.context.annotation.Bean; import
 * org.springframework.jdbc.datasource.DriverManagerDataSource; import
 * org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean; import
 * org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter; import
 * org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean; import java.util.Properties;
 * import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter; import
 * org.springframework.web.context.support.ServletContextResource;
 * 
 * import jakarta.persistence.EntityManagerFactory;
 * 
 * import org.springframework.mock.web.MockServletContext; import
 * org.springframework.orm.jpa.JpaTransactionManager; import
 * org.springframework.jdbc.core.JdbcTemplate; import org.springframework.core.io.ClassPathResource;
 * import org.springframework.transaction.support.TransactionTemplate;
 * 
 * import org.springframework.web.servlet.view.InternalResourceViewResolver; import
 * org.springframework.web.servlet.view.JstlView; import
 * uk.ac.bbsrc.tgac.miso.service.security.DefaultAuthorizationManager; import
 * uk.ac.bbsrc.tgac.miso.core.service.naming.resolvers.NamingSchemeResolverService; import
 * uk.ac.bbsrc.tgac.miso.core.service.naming.resolvers.StaticMappedNamingSchemeResolverService;
 * import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager; import
 * org.springframework.web.context.support.XmlWebApplicationContext; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.orm.jpa.vendor.HibernateJpaDialect; import
 * org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor; import
 * org.springframework.beans.factory.annotation.Value; import
 * org.springframework.orm.hibernate5.HibernateTransactionManager;
 */
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.eaglegenomics.simlims.core.manager.LocalSecurityManager;

import jakarta.persistence.EntityManagerFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.resolvers.StaticMappedNamingSchemeResolverService;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateDeletionDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSecurityDao;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultUserService;
import uk.ac.bbsrc.tgac.miso.service.security.DefaultAuthorizationManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;

@Configuration
@EnableTransactionManagement
@PropertySource(value = {"classpath:/tomcat-config/miso.it.properties"})
@WebAppConfiguration("src/it/resources")
public class STConfig {

  @Bean
  public DriverManagerDataSource dataSource(@Value("${db.host}") String host, @Value("${db.port}") String port,
      @Value("${db.user}") String user, @Value("${db.pass}") String password, @Value("${db.name}") String name) {

    DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();

    driverManagerDataSource.setUrl("jdbc:mysql://" + host + ":" + port + "/" + name);
    driverManagerDataSource.setUsername(user);
    driverManagerDataSource.setPassword(password);

    return driverManagerDataSource;
  }

  @Bean
  public HibernateJpaDialect jpaDialect() {
    return new HibernateJpaDialect();
  }

  @Bean
  public PersistenceExceptionTranslationPostProcessor postProcessor() {
    return new PersistenceExceptionTranslationPostProcessor();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DriverManagerDataSource dataSource,
      HibernateJpaVendorAdapter jpaVendorAdapter) {
    LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

    entityManagerFactory.setDataSource(dataSource);
    entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);
    entityManagerFactory.setPackagesToScan("com.eaglegenomics.simlims.core", "uk.ac.bbsrc.tgac.miso.core.data",
        "uk.ac.bbsrc.tgac.miso.core.event");

    Properties properties = new Properties();
    properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
    properties.put("hibernate.enable_lazy_load_no_trans", "true");

    entityManagerFactory.setJpaProperties(properties);
    return entityManagerFactory;

  }

  @Bean
  public HibernateJpaVendorAdapter jpaVendorAdapter() {
    HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
    jpaVendorAdapter.setShowSql(false);
    jpaVendorAdapter.setGenerateDdl(false);

    return jpaVendorAdapter;
  }

  @Bean
  public MisoPropertyExporter propertyConfigurer() {
    MisoPropertyExporter propertyExporter = new MisoPropertyExporter();

    ClassPathResource properties = new ClassPathResource("/tomcat-config/miso.it.properties");
    propertyExporter.setLocation(properties);

    return propertyExporter;
  }

  @Bean
  public MockServletContext servletContext() {
    return new MockServletContext();
  }

  @Bean
  public JpaTransactionManager transactionManager(DriverManagerDataSource dataSource,
      EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setDataSource(dataSource);
    transactionManager.setEntityManagerFactory(entityManagerFactory);

    return transactionManager;

  }

  @Bean
  public JdbcTemplate interfaceTemplate(DriverManagerDataSource dataSource) {
    JdbcTemplate interfaceTemplate = new JdbcTemplate();
    interfaceTemplate.setDataSource(dataSource);
    return interfaceTemplate;
  }

  @Bean
  public TransactionTemplate transactionTemplate(JpaTransactionManager transactionManager) {
    TransactionTemplate template = new TransactionTemplate();
    template.setTransactionManager(transactionManager);
    return template;
  }

  @Bean
  public InternalResourceViewResolver jspViewResolver() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setViewClass(JstlView.class);
    return viewResolver;
  }

  @Bean
  public DefaultAuthorizationManager authorizationManager() {
    return new DefaultAuthorizationManager();
  }

  @Bean
  public DefaultUserService userService() {
    return new DefaultUserService();
  }

  @Bean
  public HibernateSecurityDao securityStore() {
    return new HibernateSecurityDao();
  }

  @Bean
  public HibernateDeletionDao deletionStore() {
    return new HibernateDeletionDao();
  }


  @Bean
  public LocalSecurityManager securityManager() {
    return new LocalSecurityManager();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


  @Bean
  public MisoFilesManager misoFilesManager() {
    MisoFilesManager manager = new MisoFilesManager();
    manager.setFileStorageDirectory("/storage/miso/files");
    return manager;
  }

  @Bean
  public StaticMappedNamingSchemeResolverService namingSchemeResolverService() {
    return new StaticMappedNamingSchemeResolverService();
  }

  @Bean
  public XmlWebApplicationContext wac(MockServletContext servletContext) {
    XmlWebApplicationContext wac = new XmlWebApplicationContext();
    wac.setServletContext(servletContext);
    return wac;

  }

  @Bean
  public StandardServletMultipartResolver multipartResolver() {
    return new StandardServletMultipartResolver();
  }

}
