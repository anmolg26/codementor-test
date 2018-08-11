package me.anmol.codementor.security;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import me.anmol.codementor.service.UserAuthenticationService;

public class AuthorizationFilter implements Filter {

	private UserAuthenticationService authenticationService;

	public static final Set<String> openWhiteList = new HashSet<>();
	
	private static String apiBaseUrl;

	{
		String refreshToken = "/access-tokens/refresh";
		String userLoginLogout = "/access-tokens";
		String signUp = "/users";

		openWhiteList.add(refreshToken);
		openWhiteList.add(userLoginLogout);
		openWhiteList.add(signUp);
		
		apiBaseUrl = System.getProperty("api_url");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("Open white list: " + openWhiteList);
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		if (isOpenUrl(httpRequest)) {
			chain.doFilter(httpRequest, response);
		} else {
			System.out.println("It was not an open url");
			String token = httpRequest.getHeader("X-Access-Token");
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			try {
				if (token == null) {
					httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
					System.out.println("Token was null");
					return;
				}
				long key = authenticationService.isTokenValid(token);
				if (key > 0) {					
					httpRequest.setAttribute("userId", key);
					chain.doFilter(httpRequest, httpResponse);
					return;
				} else {
					System.out.println("Admin guid was invalid");
					httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
			} catch (Throwable e) {
				e.printStackTrace();
				System.out.println("Some exception occurred");
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}

		}
	}

	private boolean isOpenUrl(HttpServletRequest httpRequest) {
		String url = httpRequest.getRequestURL().toString();
		String resource = getResource(url);
		System.out.println("Resource url determined as: " + resource);
		if (openWhiteList.contains(resource)) {
			return true;
		}
		return false;
	}

	private String getResource(String url) {
		String resource = url.replaceFirst(apiBaseUrl, "");
		return resource;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		WebApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(filterConfig.getServletContext());
		authenticationService = (UserAuthenticationService) context.getBean("authenticationService");
	}

	@Override
	public void destroy() {
		authenticationService = null;
	}

}
