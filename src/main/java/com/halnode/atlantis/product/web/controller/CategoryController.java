package com.halnode.atlantis.product.web.controller;

import com.halnode.atlantis.product.persistence.model.Category;
import com.halnode.atlantis.product.persistence.repository.CategoryRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    @NonNull
    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryRepository.save(category));
    }
}
