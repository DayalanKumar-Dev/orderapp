package com.orderapp.event;


import com.orderapp.model.Product;
import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductEvent {

    @Id
    @GeneratedValue
    private Integer productEventId;

    @Enumerated(EnumType.STRING)
    private ProductEventType productEventType;

    @OneToOne(mappedBy="productEvent", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Product product;
}
