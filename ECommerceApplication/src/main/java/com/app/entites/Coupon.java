package com.app.entites;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "coupons")
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @NotBlank
    @Size(min = 2, message = "coupon name must contain at least 2 characters")
    private String couponName;

    @Min(1)
    @Max(99)
    private Long discountPercentage;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<Product> products;
}
