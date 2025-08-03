package com.backend.smartmart.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.backend.smartmart.entity.User;

@Repository
public interface UserDao extends CrudRepository<User, String> {

}
