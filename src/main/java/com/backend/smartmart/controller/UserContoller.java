package com.backend.smartmart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.smartmart.entity.User;
import com.backend.smartmart.service.UserService;

import jakarta.annotation.PostConstruct;


@RestController
public class UserContoller {

	@Autowired
	private UserService userService;
	
	@PostConstruct
	public void initRolesandUsers() {
		userService.initRolesAndUser(); //to add the default data
	}
	
	@PostMapping({"/registerNewUser"})
	public User registerNewUser(@RequestBody User user) {
		return userService.registerNewUser(user);
	}
	
	
	@GetMapping({"/forAdmin"})
	@PreAuthorize("hasRole('Admin')")
	public String forAdmin() {
		return "This feature is accessible only to the admin";
	}
	
	@GetMapping({"/forUser"})
	@PreAuthorize("hasRole('User')")
	public String forUSer() {
		return "This feature is accessible only to the user " ;
	}
}
