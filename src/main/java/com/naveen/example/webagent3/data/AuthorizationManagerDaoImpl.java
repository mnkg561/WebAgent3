package com.naveen.example.webagent3.data;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.naveen.example.webagent3.model.AuthorizationDecision;

@Service
public class AuthorizationManagerDaoImpl implements AuthorizationManagerDao {
	@Autowired
	AuthorizationDecision authorizationDecision;
	
	final static Logger logger = Logger.getLogger(AuthorizationManagerDaoImpl.class);
	
	@Override
	public AuthorizationDecision isAuthorized(String path, String cookieName, String cookie) {
		logger.info("Calling SSO Policy server to check Authorization");
		String endPointURL = "http://ssologin.naveen.com:8080/SSOLogin/isAuthorized";
		MultiValueMap<String, String> requestObject = new LinkedMultiValueMap<String, String>();
		requestObject.add("path", path);
		// requestObject.add("sessionID", cookie);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Cookie", cookieName + "=" + cookie);
		HttpEntity<MultiValueMap<String, String>> e = new HttpEntity<MultiValueMap<String, String>>(requestObject,
				headers);
		RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
		ResponseEntity<AuthorizationDecision> response = restTemplate.exchange(endPointURL, HttpMethod.POST, e,
				AuthorizationDecision.class);
		authorizationDecision = response.getBody();
		// AuthorizationDecision authorizationDecision =
		// restTemplate.postForObject(endPointURL, e,
		// AuthorizationDecision.class);
		// if(authorizationDecision.getIsAuthorized().equalsIgnoreCase("true"))isAuthorized
		// = true;
		return authorizationDecision;
	}

}
