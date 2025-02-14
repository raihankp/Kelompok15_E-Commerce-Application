package com.app.controllers;

import com.app.config.AppConstants;
import com.app.entites.Brand;
import com.app.entites.Coupon;
import com.app.payloads.BrandDTO;
import com.app.payloads.BrandResponse;
import com.app.payloads.CouponDTO;
import com.app.payloads.CouponResponse;
import com.app.services.BrandService;
import com.app.services.CouponService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")

public class CouponController {
    @Autowired
    private CouponService couponService;
    @PostMapping("/admin/coupons")
    public ResponseEntity<CouponDTO> createCoupon(@Valid @RequestBody Coupon coupon) {
        CouponDTO savedCouponDTO = couponService.createCoupon(coupon);
        return new ResponseEntity<>(savedCouponDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/coupons")
    public ResponseEntity<CouponResponse> getCoupons(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "couponId", required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "asc", required = false) String sortOrder) {

        CouponResponse couponResponse = couponService.getCoupons(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(couponResponse, HttpStatus.OK);
    }

    @PutMapping("/admin/coupons/{couponId}")
    public ResponseEntity<CouponDTO> updateCoupon(@RequestBody Coupon coupon, @PathVariable Long couponId) {
        CouponDTO updatedCoupon = couponService.updateCoupon(coupon, couponId);
        return new ResponseEntity<>(updatedCoupon, HttpStatus.OK);
    }

    @DeleteMapping("/admin/coupons/{couponId}")
    public ResponseEntity<String> deleteCoupon(@PathVariable Long couponId) {
        String status = couponService.deleteCoupon(couponId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/admin/coupons/{couponId}/apply/{productId}")
    public ResponseEntity<String> applyCoupon(@PathVariable Long couponId, @PathVariable Long productId) {
        String status = couponService.applyCoupon(couponId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/admin/coupons/remove/{productId}")
    public ResponseEntity<String> removeCoupon(@PathVariable Long productId) {
        String status = couponService.removeCoupon(productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}