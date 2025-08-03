package com.backend.smartmart.service;



import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.backend.smartmart.configuration.JwtRequestFilter;
import com.backend.smartmart.dao.CartDao;
import com.backend.smartmart.dao.ProductDao;
import com.backend.smartmart.dao.UserDao;
import com.backend.smartmart.entity.Cart;
import com.backend.smartmart.entity.Product;
import com.backend.smartmart.entity.User;

@Service
public class ProductService {

	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CartDao cartDao;
	
	public Product addNewProduct(Product product) {
		return productDao.save(product);
	}
	
	public List<Product> getAllProducts(int pageNumber , String searchKey){
		Pageable pageable = PageRequest.of(pageNumber,12); 
		
		if(searchKey.equals(""))
		{
		Page<Product> page = productDao.findAll(pageable);
	    return page.getContent();
		}else {
			return (List<Product>)productDao.findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCase(searchKey, searchKey, pageable);
		}
	}
	
	public Product getProductDetailsById(Integer productId) {
		return productDao.findById(productId).get();
	}
	
	public void deleteProductDetails(Integer productId) {
		productDao.deleteById(productId);
	}
	
	public List<Product> getProductDetails(boolean isSingleProductCheckout , Integer productId) {
		if(isSingleProductCheckout && productId !=0) {
			//we are going to buy single product
			List<Product> list = new ArrayList<>();
			Product product = productDao.findById(productId).get();
			list.add(product);
			return list;
			
		}else {
			//we are going to checkout the entire cart
			String username = JwtRequestFilter .CURRENT_USER;
		    User user = userDao.findById(username).get();
			List<Cart> cart = cartDao.findByUser(user);
			
			return cart.stream().map(x -> x.getProduct()).collect(Collectors.toList());
		}
		
		
	}
}
