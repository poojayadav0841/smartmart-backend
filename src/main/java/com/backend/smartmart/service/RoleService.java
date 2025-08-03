package com.backend.smartmart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.smartmart.dao.RoleDao;
import com.backend.smartmart.entity.Role;

@Service
public class RoleService {

	@Autowired
	private RoleDao roleDao;
	public Role createNewRole(Role role) {
		return roleDao.save(role); //save the role of registered user
		
	}
}
