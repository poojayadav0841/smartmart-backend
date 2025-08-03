package com.backend.smartmart.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.smartmart.dao.RoleDao;
import com.backend.smartmart.dao.UserDao;
import com.backend.smartmart.entity.Role;
import com.backend.smartmart.entity.User;

@Service
public class UserService {

	@Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	

	
	public void initRolesAndUser() {
		Role adminRole = new Role();
		adminRole.setRoleName("Admin");
		adminRole.setRoleDescription("Admin role");
		roleDao.save(adminRole); //save admin role in database
		
		Role userRole = new Role();
		userRole.setRoleName("User");
		userRole.setRoleDescription("Default role for newly created user");
		roleDao.save(userRole); //save user role in database
		
		if (!userDao.existsByUserName(adminUsername)) {
            User adminUser = new User();
            adminUser.setUserFirstName("admin");
            adminUser.setUserLastName("admin");
            adminUser.setUserName(adminUsername);
            adminUser.setUserPassword(getEncodedPassword(adminPassword));
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminUser.setRole(adminRoles);
            userDao.save(adminUser);
        }
		User user= new User();
		user.setUserFirstName("tina");
		user.setUserLastName("sharma");
		user.setUserName("tina@123");
		user.setUserPassword(getEncodedPassword("tina@pass"));
		Set<Role> userRoles= new HashSet<>();
		userRoles.add(userRole); 
		user.setRole(userRoles); //set the role of normal user in roles
		userDao.save(user);
		
	}
	
	public User registerNewUser(User user) {
	   Role role = roleDao.findById("User").get();
	   
	   Set<Role> roleSet = new HashSet<>();
	   roleSet.add(role);	   
	   user.setRole(roleSet);
	   
	   String password = getEncodedPassword(user.getUserPassword());
	   user.setUserPassword(password);
	   
	   return userDao.save(user);
	}
	
	public String getEncodedPassword(String password) {
		return passwordEncoder.encode(password);
	}
}
