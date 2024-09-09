package com.sample.controller;

import com.sample.model.Product;
import com.sample.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/all")
    public List<Product> getAllTheProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById((long) id));
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable int id) {
        try {
            productService.deleteProduct((long) id);
            return ResponseEntity.status(HttpStatus.OK).body("SUCCESS DELETE");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Delete Product");
        }
    }

    @PostMapping("/new")
    public ResponseEntity<String> addNewProduct(@RequestBody Product product) {
        try {
            log.info("addNewProduct {}",product);
            return ResponseEntity.status(HttpStatus.OK).body(productService.saveProduct(product).toString());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Save Product");
        }

    }

}
