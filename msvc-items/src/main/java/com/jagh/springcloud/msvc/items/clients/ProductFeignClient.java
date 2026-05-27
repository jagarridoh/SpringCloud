package com.jagh.springcloud.msvc.items.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.jagh.springcloud.msvc.items.models.Product;

@FeignClient(name="msvc-products", path="/api/products")
public interface ProductFeignClient {
    @GetMapping
    List<Product> findAll();

    @GetMapping("/{id}")
    Product details(@PathVariable Long id);
}
