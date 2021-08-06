package com.amr.project.webapp.rest_controller;

import com.amr.project.converter.AddressMapper;
import com.amr.project.converter.CategoryMapper;
import com.amr.project.model.dto.CategoryDto;
import com.amr.project.service.abstracts.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/category")
public class CategoryRestController {

    private CategoryService categoryService;

    @Autowired
    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getAddress(@PathVariable("id") Long id) {
        return new ResponseEntity<>(CategoryMapper.INSTANCE.categoryToDto(categoryService.getByKey(id)), HttpStatus.OK);
    }


    @PutMapping("/save")
    public ResponseEntity<Long> saveCategory(@RequestBody CategoryDto categoryDto) {
        categoryService.update(CategoryMapper.INSTANCE.dtoToCategory(categoryDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/add")
    public ResponseEntity<Long> addCategory(@RequestBody CategoryDto categoryDto) {
        categoryService.persist(CategoryMapper.INSTANCE.dtoToCategory(categoryDto));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        categoryService.deleteByKeyCascadeIgnore(id);
    }
}
