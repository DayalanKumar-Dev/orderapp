package com.orderapp.model;

import com.orderapp.event.ProductEvent;
import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    private String productName;

    private String productManufacturerName;

    @OneToOne
    @JoinColumn(name="productEventId")
    private ProductEvent productEvent;
}
