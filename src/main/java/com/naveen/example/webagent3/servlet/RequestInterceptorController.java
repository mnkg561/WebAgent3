package com.naveen.example.webagent3.servlet;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean result = false;
		boolean isProtected = false;
		boolean isAuthenticated = false;
		boolean isAuthorized = false;
		boolean mfaEnabled = false;

		String path = request.getRequestURI();
		String redirectUrl = "";
		Map<String, String> sessionObject = null;
		System.out.println(" URL " + request.getRequestURL());
		System.out.println(" URI " + request.getRequestURI());
		System.out.println(" I'm in pre handle webagent");

		// isProtectedCall to Policy server
		ProtectedPolicy policy = protectionManagerDaoImpl.isProtected(request.getRequestURI());
		isProtected = policy.getThisProtected();
		if (isProtected) {
			redirectUrl = policy.getProtectedBy() + "?target=" + request.getRequestURL();
			String[] cookieArray = cookieRetrieval(request, response);
			if (cookieArray != null && cookieArray[0].equalsIgnoreCase("true")) {
				sessionObject = authenticationManagerDaoImpl.validateAndCreateSession(cookieArray[1], cookieArray[2]);
				if (sessionObject != null && sessionObject.get("validation").equalsIgnoreCase("success")) {
					System.out.println(" SUCCESSFUL ");
					Cookie sessionCookie = new Cookie(SESSIONNAME, sessionObject.get("sessionID"));
					sessionCookie.setDomain(".naveen.com");
					sessionCookie.setMaxAge(600);
					sessionCookie.setPath("/");
					response.addCookie(sessionCookie);
					System.out.println("User is Authenticated");
					Cookie userCookie = new Cookie("userId", sessionObject.get("userId"));
					userCookie.setDomain(".naveen.com");
					userCookie.setMaxAge(600);
					userCookie.setPath("/");
					response.addCookie(userCookie);
					
					isAuthenticated = true;
				}
				if (isProtected && isAuthenticated) {
					// If URL is protected resource and user is successfully
					// authenticated then check whether user is authorized to
					// use target
					// url
					// Hand over the request to AthorizationManager
					AuthorizationDecision authorizationDecision = authorizationManagerDaoImpl.isAuthorized(path, SESSIONNAME,
							sessionObject.get("sessionID"));
					isAuthorized = authorizationDecision.isAuthorized();
					mfaEnabled = authorizationDecision.isMfaEnabled();
					if (isAuthorized) {
						result = true;
						System.out.println("I'm authorized user too");
					}
				}
			}
		}

		if (!isProtected)
			result = true;
		if (isProtected && !isAuthenticated) {
			// User doesn't have any valid session
			System.out.println("I Should redirect this to SSO URL " + redirectUrl);
			response.sendRedirect(redirectUrl);
			result = false;
		}
		if (isAuthenticated && !isAuthorized) {
			// If user is not authorized to view the page then redirect the user
			// to Unauthorized page
			if(!mfaEnabled){
			System.out.println("Unauthorized user");
			response.sendRedirect("http://ssologin.naveen.com:8080/SSOLogin/unauthorized");
			}else{
				System.out.println("MFA Enabled application.. Redirecting to open OTP page");
				String targetURL = "http://ssologin.naveen.com:8080/SSOLogin/openotp?target="+request.getRequestURL();
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
				}
				if (cookies[i].getName().equalsIgnoreCase(FORMFREECREDCOOKIE)) {
					// Check if user is returning from login page with
					// formFreeCredCookie
					formFreeCredCookie = cookies[i].getValue();
					// If found, save it in a variable to process and remove
					// it..
					cookieArray[0] = "true";
					cookieArray[1] = formFreeCredCookie;
					cookieArray[2] = FORMFREECREDCOOKIE;
					Cookie cookie = new Cookie(FORMFREECREDCOOKIE, formFreeCredCookie);
					cookie.setDomain(".naveen.com");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
		return cookieArray;
	}
}
