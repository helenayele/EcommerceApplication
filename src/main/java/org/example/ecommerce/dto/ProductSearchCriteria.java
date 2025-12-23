package org.example.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSearchCriteria {
    private String name;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean active;
}