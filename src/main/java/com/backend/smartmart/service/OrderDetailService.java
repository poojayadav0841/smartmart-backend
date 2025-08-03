package com.backend.smartmart.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.smartmart.configuration.JwtRequestFilter;
import com.backend.smartmart.dao.CartDao;
import com.backend.smartmart.dao.OrderDetailDao;
import com.backend.smartmart.dao.ProductDao;
import com.backend.smartmart.dao.UserDao;
import com.backend.smartmart.entity.Cart;
import com.backend.smartmart.entity.OrderDetail;
import com.backend.smartmart.entity.OrderInput;
import com.backend.smartmart.entity.OrderProductQuantity;
import com.backend.smartmart.entity.Product;
import com.backend.smartmart.entity.TransactionDetails;
import com.backend.smartmart.entity.User;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class OrderDetailService {

	private static final String ORDER_PLACED = "Placed";
	@Value("${razorpay.KEY}")
	private String KEY;
	@Value("${razorpay.KEY_SECRET}")
	private String KEY_SECRET;
	private static final String CURRENCY = "INR";
	
	@Autowired
	private OrderDetailDao orderDetailDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CartDao cartDao;
	
	public List<OrderDetail> getAllOrderDetails(String status) {
		List<OrderDetail> orderDetails= new ArrayList<>();
		
		if(status.equals("All")) {
			orderDetailDao.findAll().forEach(
					x -> orderDetails.add(x)
					);
		}else {
			orderDetailDao.findByOrderStatus(status).forEach(
					x-> orderDetails.add(x)
					);
		}
//		orderDetailDao.findAll().forEach(x -> orderDetails.add(x));
		return orderDetails;
	}
	
	public List<OrderDetail> getOrderDetails() {
		String currentUser = JwtRequestFilter.CURRENT_USER;
		User user = userDao.findById(currentUser).get();
		
		return orderDetailDao.findByUser(user);
	}
	
	public void placeOrder(OrderInput orderInput, boolean isSingleProductCheckout) {
		List<OrderProductQuantity> productQuantityList = orderInput.getOrderProductQuantityList();
		
		for(OrderProductQuantity o : productQuantityList) {
			Product product = productDao.findById(o.getProductId()).get();
			
			String currentUser = JwtRequestFilter.CURRENT_USER;
			User user = userDao.findById(currentUser).get();
			
			OrderDetail orderDetail = new OrderDetail(
					orderInput.getFullName(),
					orderInput.getFullAddress(),
					orderInput.getContactNumber(),
					orderInput.getAlternateContactNumber(),
					ORDER_PLACED,
					product.getProductDiscountedPrice() * o.getQuantity(),
					product,
					user,
					orderInput.getTransactionId()
					
					);
			
			//empty the cart
			if(!isSingleProductCheckout) {
				List<Cart> carts= cartDao.findByUser(user);
				carts.stream().forEach(x -> cartDao.deleteById(x.getCartId()));
			}
			
			orderDetailDao.save(orderDetail);
		}
	}
	
	//100 - smallest value
	public void markOrderAsDelivered(Integer orderId) {
		OrderDetail orderDetail = orderDetailDao.findById(orderId).get();
		
		if(orderDetail != null) {
			orderDetail.setOrderStatus("Delivered");
			orderDetailDao.save(orderDetail);
		}
	}
	
	public TransactionDetails creatTransaction(Double amount) {
	    try {
	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("amount", (int)(amount * 100));  // amount in paise
	        jsonObject.put("currency", CURRENCY);
	        jsonObject.put("payment_capture", 1);
	        jsonObject.put("receipt", "txn_" + System.currentTimeMillis());

	        RazorpayClient razorpayClient = new RazorpayClient(KEY, KEY_SECRET);
	        Order order = razorpayClient.orders.create(jsonObject);

	        return prepareTransactionDetails(order);
	    } catch (Exception e) {
	        System.out.println("Error in createTransaction: " + e.getMessage());
	        e.printStackTrace();
	    }

	    return null;
	}

	
	private TransactionDetails prepareTransactionDetails(Order order) {
		String orderId= order.get("id");
		String currency = order.get("currency");
		Integer amount = order.get("amount");
		
		TransactionDetails transactionDetails = new TransactionDetails(orderId,currency,amount,KEY);
		
		return transactionDetails;
	}
}
