package com.backend.smartmart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.smartmart.entity.OrderDetail;
import com.backend.smartmart.entity.OrderInput;
import com.backend.smartmart.entity.TransactionDetails;
import com.backend.smartmart.service.OrderDetailService;

@RestController
public class OrderDetailController {

	@Autowired
	private OrderDetailService orderDetailService;
	
	@PreAuthorize("hasRole('User')")
	@PostMapping({"/placeOrder/{isSingleProductCheckout}"})
    public void placeOrder(@PathVariable(name = "isSingleProductCheckout")boolean isSingleProductCheckout,
    		@RequestBody OrderInput orderInput) {
		orderDetailService.placeOrder(orderInput, isSingleProductCheckout);		
	}
	
	@PreAuthorize("hasRole('User')")
	@GetMapping("/getOrderDetails")
	public List<OrderDetail> getOrderDetails() {
		return orderDetailService.getOrderDetails();
	}
	
	@PreAuthorize("hasRole('Admin')")
	@GetMapping("/getAllOrderDetails/{status}")
	public List<OrderDetail> getAllOrderDetails(@PathVariable(name = "status")String status) {
		return orderDetailService.getAllOrderDetails(status);
	}
	
	@PreAuthorize("hasRole('Admin')")
	@GetMapping("/markOrderAsDelivered/{orderId}")
	public void markOrdeAsDelivered(@PathVariable(name="orderId") Integer orderId) {
		orderDetailService.markOrderAsDelivered(orderId);
	}
	
	@PreAuthorize("hasRole('User')")
	@GetMapping("/createTransaction/{amount}")
	public TransactionDetails createTransaction(@PathVariable(name ="amount")Double amount) {
		return orderDetailService.creatTransaction(amount);
	}
}
