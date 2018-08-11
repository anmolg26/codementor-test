package me.anmol.codementor.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestDataSourceConfig {

	@Bean
	public BasicDataSource testDataSource() {
		// This way of configuration is only for test application.
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName("org.postgresql.Driver");
		String url = System.getProperty("db_url");
		valid(url,"database url as db_url");
		basicDataSource.setUrl(url);
		String dbUsername = System.getProperty("db_username");
		valid(dbUsername,"database user name as db_username");
		basicDataSource.setUsername(dbUsername); // Enter username
		String dbPassword = System.getProperty("db_password");
		valid(dbPassword,"database password as db_password");
		basicDataSource.setPassword(dbPassword); // Enter password
		basicDataSource.setDefaultAutoCommit(false);
		return basicDataSource;
	}

	private void valid(String url, String string) {
		if(url == null){
			throw new IllegalArgumentException("Looks like " + string + " has not been provided in environment variables.");
		}
		
	}

}
