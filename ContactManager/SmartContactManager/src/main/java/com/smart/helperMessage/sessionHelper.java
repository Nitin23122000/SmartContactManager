package com.smart.helperMessage;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;



@Component
public class sessionHelper {

	public void removeMessagefromSession() {
		
		try {
			
			HttpSession session = (HttpSession) ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
			session.removeAttribute("message");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
