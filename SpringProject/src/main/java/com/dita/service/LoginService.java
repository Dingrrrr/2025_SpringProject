package com.dita.service;

import com.dita.persistence.LoginPageRepository;

public class LoginService {
	
	private final LoginPageRepository loginReop;
	
	public LoginService(LoginPageRepository loginReop) {
		this.loginReop = loginReop;
	}
	
	
	
}
