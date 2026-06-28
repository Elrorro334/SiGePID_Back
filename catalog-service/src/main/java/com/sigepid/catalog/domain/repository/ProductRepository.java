package com.sigepid.catalog.domain.repository;

import com.sigepid.catalog.domain.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByCategoryId(String categoryId);

    Optional<Product> findBySku(String sku);

    List<Product> findByActiveTrue();

    List<Product> findByStockLessThan(Integer threshold);
}
