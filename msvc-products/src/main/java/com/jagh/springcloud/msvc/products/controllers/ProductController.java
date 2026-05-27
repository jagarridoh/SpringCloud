package com.jagh.springcloud.msvc.products.controllers;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jagh.springcloud.msvc.products.entities.Product;
import com.jagh.springcloud.msvc.products.services.ProductService;

@RestController
//@RequestMapping("/api/products")
public class ProductController {

    final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable Long id) throws InterruptedException {
        if(id.equals(10L)) {
            throw new IllegalStateException("Product not found");
        }
        if(id.equals(7L)) {
            TimeUnit.SECONDS.sleep(4L);
        }
        Optional<Product> productOptional = service.findById(id);

        if (productOptional.isPresent()) {
            return ResponseEntity.ok(productOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }
}
