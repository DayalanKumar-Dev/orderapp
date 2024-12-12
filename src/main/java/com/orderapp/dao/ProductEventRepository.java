package com.orderapp.dao;

import com.orderapp.event.ProductEvent;
import org.springframework.data.repository.CrudRepository;

public interface ProductEventRepository extends CrudRepository<ProductEvent, Integer> {
}
