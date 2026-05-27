package com.jagh.springcloud.msvc.items.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.jagh.springcloud.msvc.items.models.Item;
import com.jagh.springcloud.msvc.items.models.Product;
import com.jagh.springcloud.msvc.items.services.ItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ItemController {

    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemService service;
    private final CircuitBreakerFactory cBreakerFactory;

    public ItemController(@Qualifier("itemServiceWebClient") ItemService service, 
            CircuitBreakerFactory cBreakerFactory) {
        this.cBreakerFactory = cBreakerFactory;
        this.service = service;
    }

    @GetMapping
    public List<Item> list(@RequestParam(name = "name", required = false) String name,
                           @RequestHeader(name = "token-request", required = false) String token) {
        System.out.println(name);
        System.out.println(token);
        return service.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable Long id) {
        Optional<Item> itemOptional = cBreakerFactory.create("items").run(() -> service.findById(id), e -> {
            System.out.println(e.getMessage());
            logger.error(e.getMessage());

            Product product = new Product();
            product.setCreatedAt(
                LocalDate.now());
            product.setId(1L);
            product.setName("Camara Sony");
            product.setPrice(
                500.00);
            return Optional.of(new Item(product,
                    5)); 
        });

        if(itemOptional.isPresent()) {
            return ResponseEntity.ok(itemOptional.get());
        }
        //return ResponseEntity.notFound().build(); << correcto pero devuelve body vacio
        return ResponseEntity.status(404)
                .body(Collections.singletonMap(
                    "message",
                    "No existe el producto en el microservicio msvc-products"));
    }

    @CircuitBreaker(name = "items", fallbackMethod = "getFallBackmethodProduct")
    @GetMapping("/details/{id}")
    public ResponseEntity<?> details2(@PathVariable Long id) {
        Optional<Item> itemOptional = service.findById(id);

        if(itemOptional.isPresent()) {
            return ResponseEntity.ok(itemOptional.get());
        }
        //return ResponseEntity.notFound().build(); << correcto pero devuelve body vacio
        return ResponseEntity.status(404)
                .body(Collections.singletonMap(
                    "message",
                    "No existe el producto en el microservicio msvc-products"));
    }

    public ResponseEntity<?> getFallBackmethodProduct(Throwable e) {
        System.out.println(e.getMessage());
        logger.error(e.getMessage());

        Product product = new Product();
        product.setCreatedAt(
            LocalDate.now());
        product.setId(1L);
        product.setName("Camara Sony");
        product.setPrice(
            500.00);
        return ResponseEntity.ok(new Item(product,
            5));




    }

}
