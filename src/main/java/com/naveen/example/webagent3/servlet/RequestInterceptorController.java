package com.naveen.example.webagent3.servlet;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.naveen.example.webagent3.data.ProtectionManagerDao;
import com.naveen.example.webagent3.data.AuthenticationManagerDao;
import com.naveen.example.webagent3.data.AuthorizationManagerDao;
import com.naveen.example.webagent3.model.AuthorizationDecision;
import com.naveen.example.webagent3.model.ProtectedPolicy;

@Component
public class RequestInterceptorController extends HandlerInterceptorAdapter {
	@Autowired
	ProtectionManagerDao protectionManagerDaoImpl;

	@Autowired
	AuthenticationManagerDao authenticationManagerDaoImpl;

	@Autowired
	AuthorizationManagerDao authorizationManagerDaoImpl;

	private final static String SESSIONNAME = "NAVEENSESSIONID";
	private final static String FORMFREECREDCOOKIE = "formFreeCredCookie";

	final static Logger logger = Logger.getLogger(RequestInterceptorController.class);

	/*
	 * * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
	 * preHandle(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object)
	 * 
	 * This below method is to intercept every request before it being served by
	 * controllers Once intercepted, WebAgent calls Policy server (SSOLogin)
	 * module to check IsProtected?, isAuthenticated? and isAuthorized? calls
	 * depends on previous step response.
	 * 
	 * 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean result = false;
		boolean isProtected = false;
		boolean isAuthenticated = false;
		boolean isAuthorized = false;
		boolean mfaEnabled = false;
		logger.info("Incoming request intercepted by Web Agent interceptor ");
		String path = request.getRequestURI();
		String redirectUrl = "";
		Map<String, String> sessionObject = null;

		logger.info("Incoming request URL " + request.getRequestURL());

		// isProtectedCall to Policy server
		logger.info(
				"Handingover the request to Protection Manager to check if the target resource is protected by calling SSO Policy server");
		ProtectedPolicy policy = protectionManagerDaoImpl.isProtected(request.getRequestURI());
		isProtected = policy.getThisProtected();

		// if protected check if user have any valid session cookies in user's
		// browser
		if (isProtected) {
			logger.info("Target resource is protected");
			redirectUrl = policy.getProtectedBy() + "?target=" + request.getRequestURL();
			// retrieve the cookies from the browser
			String[] cookieArray = cookieRetrieval(request, response);

			// if user have any valid cookie then proceed for validation
			if (cookieArray != null && cookieArray[0].equalsIgnoreCase("true")) {
				// Hand over to Authentication Manager to validate session
				// cookies
				logger.info("Handing over the request to Authentication Manager to validate if user is Authenticated?");
				sessionObject = authenticationManagerDaoImpl.validateAndCreateSession(cookieArray[1], cookieArray[2]);
				if (sessionObject != null && sessionObject.get("validation").equalsIgnoreCase("success")) {
					logger.info("User have valid session cookie in the browser");
					// Adding session cookie to browser
					Cookie sessionCookie = new Cookie(SESSIONNAME, sessionObject.get("sessionID"));
					sessionCookie.setDomain(".naveen.com");
					sessionCookie.setMaxAge(600);
					sessionCookie.setPath("/");
					response.addCookie(sessionCookie);
					logger.info("Session Cookie has been added to user's browser");

					Cookie userCookie = new Cookie("userId", sessionObject.get("userId"));
					userCookie.setDomain(".naveen.com");
					userCookie.setMaxAge(600);
					userCookie.setPath("/");
					response.addCookie(userCookie);
					logger.info("UserId cookie is also added to user's browser");

					isAuthenticated = true;
				}
				if (isProtected && isAuthenticated) {
					// If URL is protected resource and user is successfully
					// authenticated then check whether user is authorized to
					// use target
					// url
					// Hand over the request to AthorizationManager
					logger.info("User is authenticated");
					logger.info(
							"Handing over to Authorization Manager to check if user is authorized to access target resource");
					AuthorizationDecision authorizationDecision = authorizationManagerDaoImpl.isAuthorized(path,
							SESSIONNAME, sessionObject.get("sessionID"));
					isAuthorized = authorizationDecision.isAuthorized();
					mfaEnabled = authorizationDecision.isMfaEnabled();
					if (isAuthorized) {
						result = true;
						logger.info("User is authorized to access protected resource");
						logger.info("Redirecting the user to target resource " + request.getRequestURL());
					}
				}
			}
		}

		if (!isProtected) {
			// Target is not protected resource
			logger.info("Target resource is not protected by SSO");
			logger.info("Redirecting the user to target resource " + request.getRequestURL());
			result = true;
		}
		if (isProtected && !isAuthenticated) {
			// User doesn't have any valid session
			logger.info("Target resource is protected and user doesn't have any valid session cookies");
			logger.info("Redirecting to SSO URL " + redirectUrl);
			response.sendRedirect(redirectUrl);
			result = false;
		}
		if (isAuthenticated && !isAuthorized) {
			// If user is not authorized to view the page then redirect the user
			// to Unauthorized page
			if (!mfaEnabled) {
				logger.info("User is not authorized to access target resource");
				logger.info("Redirecting to Unauthorized page");
				response.sendRedirect("http://ssologin.naveen.com:8080/SSOLogin/unauthorized");
			} else {
				logger.info("MFA enabled Application, redirecting to OTP page");
				String targetURL = "http://ssologin.naveen.com:8080/SSOLogin/openotp?target=" + request.getRequestURL();
				response.sendRedirect(targetURL);
			}
			result = false;
		}
		return result;
	}

	public static String[] cookieRetrieval(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		String sessionID = "";
		String formFreeCredCookie = "";
		String[] cookieArray = new String[3];
		cookieArray[0] = "false";
		if (cookies != null && cookies.length != 0) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equalsIgnoreCase(SESSIONNAME)) {
					// Check if user has a session
					sessionID = cookies[i].getValue();
					cookieArray[0] = "true";
					cookieArray[1] = sessionID;
					cookieArray[2] = SESSIONNAME;
					logger.info("Session Cookie found in the browser " + sessionID);
				}
				if (cookies[i].getName().equalsIgnoreCase(FORMFREECREDCOOKIE)) {
					// Check if user is returning from login page with
					// formFreeCredCookie
					formFreeCredCookie = cookies[i].getValue();
					logger.info("formFreecookie found in the browser " + formFreeCredCookie);
					// If found, save it in a variable to process and remove
					// it..
					cookieArray[0] = "true";
					cookieArray[1] = formFreeCredCookie;
					cookieArray[2] = FORMFREECREDCOOKIE;
					Cookie cookie = new Cookie(FORMFREECREDCOOKIE, formFreeCredCookie);
					cookie.setDomain(".naveen.com");
					cookie.setMaxAge(0);
					cookie.setPath("/");
					response.addCookie(cookie);
				}
			}
		}
		return cookieArray;
	}
}
