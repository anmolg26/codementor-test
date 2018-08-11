package me.anmol.codementor.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import me.anmol.codementor.model.JWTWrapper;
import me.anmol.codementor.model.RefreshTokens;
import me.anmol.codementor.model.RefreshTokens_;
import me.anmol.codementor.model.IdeaPoolUser;
import me.anmol.codementor.model.IdeaPoolUser_;
import me.anmol.codementor.model.UserToken;
import me.anmol.codementor.service.GenericDAO;

public class UserAuthenticationService {

	private GenericDAO<IdeaPoolUser, Long> userReaderDao;

	private GenericDAO<UserToken, Long> tokenCudDao;

	private PasswordEncoder passwordEncoder;
	
	private GenericDAO<RefreshTokens, Long> refreshTokenDao;
	
	public UserAuthenticationService(GenericDAO<IdeaPoolUser, Long> userReaderDao,
			GenericDAO<UserToken, Long> tokenCudDao,
			PasswordEncoder passwordEncoder, GenericDAO<RefreshTokens, Long> refreshTokenDao) {
		super();
		this.userReaderDao = Objects.requireNonNull(userReaderDao, "Reader DAO must not be null");
		this.tokenCudDao = Objects.requireNonNull(tokenCudDao, "Toke CUD Dao must not be null.");		
		this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "Password encoder must not be null.");
		this.refreshTokenDao = Objects.requireNonNull(refreshTokenDao, "Refresh token dao must not be null.");
	}

	public JWTWrapper authenticate(String userName, String password) throws AuthenticationError {
		ReadCriteria<IdeaPoolUser> readCriteria = (CriteriaBuilder builder) -> {
			CriteriaQuery<IdeaPoolUser> criteriaQuery = builder.createQuery(IdeaPoolUser.class);
			Root<IdeaPoolUser> root = criteriaQuery.from(IdeaPoolUser.class);
			Path<String> userNameInDb = root.get(IdeaPoolUser_.email);
			criteriaQuery.where(builder.equal(userNameInDb, userName));
			return criteriaQuery;
		};
		List<IdeaPoolUser> users = userReaderDao.findAllByCriteria(readCriteria);
		if (users.size() == 1) {
			IdeaPoolUser theUser = users.get(0);
			String realPassword = theUser.password();
			boolean isAuthenticated = passwordEncoder.matches(new String(password), realPassword);
			if (isAuthenticated) {
				RefreshTokens refreshTokens = getRefreshToken(theUser.id());	
				String newRefreshToken;
				if(refreshTokens == null){
					refreshTokens = new RefreshTokens(theUser.id());
					newRefreshToken = refreshTokens.generateNewRefreshToken();
					refreshTokens = refreshTokenDao.persist(refreshTokens);
				}
				else {
					newRefreshToken = refreshTokens.generateNewRefreshToken();
					refreshTokens = refreshTokenDao.update(refreshTokens);
				}							
				String accessToken = getAccessToken(theUser.id());
				JWTWrapper wrapper = new JWTWrapper();
				wrapper.setJwt(accessToken);
				wrapper.setRefresh_token(newRefreshToken);
				return wrapper;
			} else {
				throw new AuthenticationError("Invalid credentials");
			}
		} else {
			throw new AuthenticationError("Invalid credentials");
		}
	}

	public String generateHashedPassword(String password) {
		return passwordEncoder.encode(new String(password));
	}
	
	public void logout(String refreshToken, String accessToken){
		try {
			this.invalidateToken(accessToken);	
		}
		finally {
			RefreshTokens token = getRefreshToken(refreshToken);
			token.getTokens().remove(token);
			refreshTokenDao.update(token);			
		}		
	}

	public String getValidToken(String refreshToken) throws AuthenticationError {
		RefreshTokens refreshTokenObj = getRefreshToken(refreshToken);
		if(refreshTokenObj == null){
			throw new AuthenticationError("Invalid refresh token");
		}
		long userId = refreshTokenObj.getUserId();
		return getAccessToken(userId);
	}

	private String getAccessToken(long userId) {
		long issueTime = LocalDateTime.now().toInstant(offset).getEpochSecond();
		Date expiration = Date.from(LocalDateTime.now().plusSeconds(validitySeconds).toInstant(offset));		
		String token = Jwts.builder().setSubject(new Long(userId).toString()).setExpiration(expiration)
				.setIssuer(ISSUER).signWith(SignatureAlgorithm.HS512, secretKey).compact();
		UserToken userToken = tokenCudDao.findById(userId);
		if (userToken == null) {
			UserToken newUserToken = UserToken.createNewUserToken(userId, issueTime,
					token);
			tokenCudDao.persist(newUserToken);
		} else {			
			userToken.addToken(issueTime, token);
			tokenCudDao.update(userToken);
			return token;			
		}
		return token;
	}

	private RefreshTokens getRefreshToken(long userId){
		ReadCriteria<RefreshTokens> readCriteria = (CriteriaBuilder builder) -> {
			CriteriaQuery<RefreshTokens> criteriaQuery = builder.createQuery(RefreshTokens.class);
			Root<RefreshTokens> root = criteriaQuery.from(RefreshTokens.class);
			Path<Long> userIdInDb = root.get(RefreshTokens_.userId);
			criteriaQuery.where(builder.equal(userIdInDb, userId));
			return criteriaQuery;
		};
		List<RefreshTokens> tokens = refreshTokenDao.findAllByCriteria(readCriteria);
		if(tokens.size() == 0){
			return null;
		}		
		return tokens.get(0);				
	}
	
	private RefreshTokens getRefreshToken(String refreshToken) {
		ReadCriteria<RefreshTokens> readCriteria = (CriteriaBuilder builder) -> {
			CriteriaQuery<RefreshTokens> criteriaQuery = builder.createQuery(RefreshTokens.class);
			Root<RefreshTokens> root = criteriaQuery.from(RefreshTokens.class);
			Path<String> refreshTokenInDb = root.join(RefreshTokens_.tokens); //TODO Check
			criteriaQuery.where(builder.equal(refreshTokenInDb, refreshToken));
			return criteriaQuery;
		};
		List<RefreshTokens> tokens = refreshTokenDao.findAllByCriteria(readCriteria);
		if(tokens.size() == 0){
			return null;
		}
		else {
			return tokens.get(0);
		}		
	}

	private final ZoneOffset offset = ZoneOffset.of("+05:30");

	public long isTokenValid(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			long key = new Long(claims.getBody().getSubject().toString());
			Date expiration = claims.getBody().getExpiration();
			logger.debug("Received token as: " + token + " for member id: " + key);
			boolean isTokenExpired = checkIfTokenIsExpired(expiration);
			if (isTokenExpired) {
				logger.debug("Token is expired");
				return -1;
			} else {
				UserToken userToken = tokenCudDao.findById(key);
				if (userToken == null) {
					return -1;
				}				
				for (String currentToken : userToken.getTokens()) {
					if (token.equals(currentToken)) {						
						return key;						
					}
				}
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	private long validitySeconds = 600;

	private final String ISSUER = "me.anmol";

	private boolean checkIfTokenIsExpired(Date expiration) {
		Date now = Date.from(LocalDateTime.now().toInstant(offset));
		if (now.after(expiration)) {
			return true;
		} else {
			return false;
		}
	}

	public void invalidateToken(String token) {
		Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
		long key = new Long(claims.getBody().getSubject().toString());
		Date expiration = claims.getBody().getExpiration();		
		boolean isTokenExpired = checkIfTokenIsExpired(expiration);
		if (!isTokenExpired) {			
			UserToken userToken = tokenCudDao.findById(key);			
			int index = 0;
			boolean wasTokenFound = false;
			for (String currentToken : userToken.getTokens()) {
				if (token.equals(currentToken)) {
					wasTokenFound = true;
					break;
				}
				index++;
			}
			int indexToBeRemoved = index;
			if (wasTokenFound) {
				userToken.getTokens().remove(indexToBeRemoved);
				tokenCudDao.update(userToken);
			}
		}
	}

	private final byte[] secretKey = "PBKDF2WithHmacSHA1".getBytes();

	private Logger logger = LoggerFactory.getLogger(this.getClass());

}
