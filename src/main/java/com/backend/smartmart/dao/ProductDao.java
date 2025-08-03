package com.backend.smartmart.dao;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.smartmart.entity.Product;

@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {

	public Page<Product> findAll(Pageable pageable);
	
	public List<Product> findByProductNameContainingIgnoreCaseOrProductDescriptionContainingIgnoreCase(
			String key1, String key2, Pageable pageable
			);

	
}
