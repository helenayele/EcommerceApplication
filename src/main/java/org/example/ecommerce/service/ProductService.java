package org.example.ecommerce.service;

import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.dto.CreateProductRequest;
import org.example.ecommerce.dto.PagedResponse;
import org.example.ecommerce.dto.ProductDTO;
import org.example.ecommerce.dto.ProductSearchCriteria;
import org.example.ecommerce.entity.Product;
import org.example.ecommerce.exception.BusinessException;
import org.example.ecommerce.exception.ResourceNotFoundException;
import org.example.ecommerce.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductDTO getProduct(Long id) {
        log.info("Fetching product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return modelMapper.map(product, ProductDTO.class);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductDTO> getAllProducts(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Product> products = productRepository.findByActiveTrue(pageable);

        return mapToPagedResponse(products);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductDTO> searchProducts(ProductSearchCriteria criteria,
                                                    int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> products = productRepository.searchProducts(
                criteria.getName(),
                criteria.getCategory(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getActive(),
                pageable
        );

        return mapToPagedResponse(products);
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO createProduct(CreateProductRequest request) {
        log.info("Creating product: {}", request.getName());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .active(true)
                .build();

        Product saved = productRepository.save(product);
        return modelMapper.map(saved, ProductDTO.class);
    }

    @CacheEvict(value = "products", key = "#id")
    public ProductDTO updateProduct(Long id, CreateProductRequest request) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());

        Product updated = productRepository.save(product);
        return modelMapper.map(updated, ProductDTO.class);
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        log.info("Soft deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setActive(false);  // Soft delete
        productRepository.save(product);
    }

    public void decreaseStock(Long productId, Integer quantity) {
        int updated = productRepository.decreaseStock(productId, quantity);
        if (updated == 0) {
            throw new BusinessException("Failed to decrease stock for product: " + productId);
        }
    }

    private PagedResponse<ProductDTO> mapToPagedResponse(Page<Product> page) {
        List<ProductDTO> content = page.getContent().stream()
                .map(p -> modelMapper.map(p, ProductDTO.class))
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}