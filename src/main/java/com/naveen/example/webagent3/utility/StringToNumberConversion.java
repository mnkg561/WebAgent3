package com.naveen.example.webagent3.utility;

import org.springframework.stereotype.Component;

@Component
public class StringToNumberConversion {

	public long getNumber(String number){
		long sum=0;
		
		for(int i=0; i<number.length(); i++){
			sum=sum*10+number.charAt(i)-'0';
		}
		return sum;
	}
}
