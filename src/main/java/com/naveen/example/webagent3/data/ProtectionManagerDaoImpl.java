package com.naveen.example.webagent3.data;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.naveen.example.webagent3.data.ProtectionManagerDao;
import com.naveen.example.webagent3.model.ProtectedPolicy;

@Service
public class ProtectionManagerDaoImpl implements ProtectionManagerDao {

	@Override
	public ProtectedPolicy isProtected(String path) {
		String endPointURL = "http://ssologin.naveen.com:8080/SSOLogin/isProtected";
		MultiValueMap<String, String> requestObject = new LinkedMultiValueMap<String, String>();
		requestObject.add("path", path);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> e = new HttpEntity<MultiValueMap<String, String>>(requestObject,
				headers);
		RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
		ProtectedPolicy protectedPolicy = restTemplate.postForObject(endPointURL, e, ProtectedPolicy.class);
		return protectedPolicy;
	}

}
