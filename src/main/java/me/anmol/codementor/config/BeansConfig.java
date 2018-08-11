package me.anmol.codementor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import me.anmol.codementor.model.Idea;
import me.anmol.codementor.model.RefreshTokens;
import me.anmol.codementor.model.IdeaPoolUser;
import me.anmol.codementor.model.UserToken;
import me.anmol.codementor.service.GenericDAO;
import me.anmol.codementor.service.HibernateDAO;
import me.anmol.codementor.service.IdeaService;
import me.anmol.codementor.service.UserAuthenticationService;
import me.anmol.codementor.service.UserService;

@Configuration
public class BeansConfig {

	@Autowired
	private EntityManagerFactoryConfig emConfig;
	
	@Bean
	public GenericDAO<Idea, String> ideaDao(){
		return new HibernateDAO<>(emConfig.entityManagerFactory(), Idea.class);
	}
	
	@Bean
	public GenericDAO<IdeaPoolUser, Long> userDao() {
		return new HibernateDAO<>(emConfig.entityManagerFactory(), IdeaPoolUser.class);
	}
	
	@Bean
	public IdeaService ideaService(){
		return new IdeaService(userDao(), ideaDao());
	}
	
	@Bean
	public UserService userService(){
		return new UserService(authenticationService(), userDao());
	}
	
	//Token dao
	@Bean
	public GenericDAO<UserToken, Long> userTokenDao() {
		return new HibernateDAO<>(emConfig.entityManagerFactory(), UserToken.class);
	}

	//Refresh token dao
	@Bean
	public GenericDAO<RefreshTokens, Long> refreshTokenDao() {
		return new HibernateDAO<>(emConfig.entityManagerFactory(), RefreshTokens.class);
	}

	//Password encoder	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//Auth service
	@Bean
	public UserAuthenticationService authenticationService(){
		return new UserAuthenticationService(userDao(), userTokenDao(), passwordEncoder(), refreshTokenDao());
	}
	
}
