package com.jagh.springcloud.msvc.items.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

//import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.jagh.springcloud.msvc.items.models.Item;
import com.jagh.springcloud.msvc.items.models.Product;

//@Primary >> alternativa: usar @Qualifier("itemServiceWebClient") en el constructor del controlador
@Service
public class ItemServiceWebClient implements ItemService {

    private final WebClient.Builder client;

    public ItemServiceWebClient(WebClient.Builder client) {
        this.client = client;
    }

    @Override
    public List<Item> findAll() {
        return this.client.build()
            .get()
            //.uri("http://msvc-products") << innecesario por haberlo configurado en WebClientConfig
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(Product.class)
            .map(product -> new Item(product, new Random().nextInt(10) + 1))
            .collectList()
            .block();
    }

    @Override
    public Optional<Item> findById(Long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);

        //try {
            return Optional.of(client.build()
                .get()
                //.uri("http://msvc-products/{id}", params)  << antes de configurar el baseUrl en WebClientConfig
                .uri("/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class)
                .map(product -> new Item(product, new Random().nextInt(10) + 1))
                .block());
        //} catch (WebClientResponseException e) {
        //    return Optional.empty();
        //}

    }
}
 