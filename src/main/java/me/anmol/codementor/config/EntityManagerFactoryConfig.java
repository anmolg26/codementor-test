package me.anmol.codementor.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class EntityManagerFactoryConfig {
	
	@Autowired
	private BasicDataSource basicDataSource;
				
	@Bean 
	public BasicDataSource dataSource(){
		return basicDataSource;
	}
	
	@Bean
	public HibernateJpaVendorAdapter jpaVendorAdapter(){
		return new HibernateJpaVendorAdapter();
	}
	
	@Bean
	public EntityManagerFactory entityManagerFactory(){
		Properties properties = new Properties();
		properties.put("hibernate.hbm2ddl.auto", "update"); //DO not change it to CREATE while using instruments database!!!
		properties.put("hibernate.show_sql", true);
		properties.put("hibernate.jdbc.batch_size", 50);
			properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");						
		properties.put("hibernate.event.merge.entity_copy_observer", "allow");
		properties.put("hibernate.temp.use_jdbc_metadata_defaults", "true"); 
		this.setEnversProperties(properties);
		LocalContainerEntityManagerFactoryBean localContainerEMFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEMFactoryBean.setPackagesToScan("me.anmol.**"); //Only one of this and above line needs to be there. This line will automatically load all annotated entities. 
		localContainerEMFactoryBean.setPersistenceUnitName("persistenceUnit");
		localContainerEMFactoryBean.setDataSource(dataSource());
		localContainerEMFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
		localContainerEMFactoryBean.setJpaProperties(properties);
		localContainerEMFactoryBean.afterPropertiesSet();
		return localContainerEMFactoryBean.getObject();
	}
	
	private void setEnversProperties(Properties properties){
		properties.put("org.hibernate.envers.audit_table_prefix", "spaudprefix_");
		properties.put("org.hibernate.envers.audit_table_suffix", "_spaudsuffix");
		properties.put("org.hibernate.envers.audit_strategy", "org.hibernate.envers.strategy.ValidityAuditStrategy");
		properties.put("org.hibernate.envers.audit_strategy_validity_store_revend_timestamp", "true");
		properties.put("org.hibernate.envers.global_with_modified_flag", "true");		
		properties.put("org.hibernate.envers.track_entities_changed_in_revision", "true");
	}
	
	@Bean
	public JpaTransactionManager transactionManager() {
		return new JpaTransactionManager(entityManagerFactory());
	}

}