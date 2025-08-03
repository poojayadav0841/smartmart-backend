package com.backend.smartmart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.smartmart.entity.Role;
import com.backend.smartmart.service.RoleService;

@RestController
public class RoleController {

	@Autowired
	private RoleService roleService;
	
	
	@PostMapping({"/createNewRole"})
	public Role createNewRole(@RequestBody Role role) {
		return roleService.createNewRole(role); //role of a user
	}
}
