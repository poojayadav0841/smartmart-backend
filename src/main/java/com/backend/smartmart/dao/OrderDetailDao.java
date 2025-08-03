package com.backend.smartmart.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.backend.smartmart.entity.OrderDetail;
import com.backend.smartmart.entity.User;

public interface OrderDetailDao extends CrudRepository<OrderDetail , Integer>{

	  public List<OrderDetail> findByUser(User user);
	  
	  public List<OrderDetail> findByOrderStatus(String status);
}
