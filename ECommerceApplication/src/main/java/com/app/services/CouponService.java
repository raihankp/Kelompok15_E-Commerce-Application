package com.app.services;

import com.app.entites.Category;
import com.app.entites.Coupon;
import com.app.payloads.CategoryDTO;
import com.app.payloads.CategoryResponse;
import com.app.payloads.CouponDTO;
import com.app.payloads.CouponResponse;

public interface CouponService {

    CouponDTO createCoupon(Coupon Coupon);

    CouponResponse getCoupons(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CouponDTO updateCoupon(Coupon coupon, Long couponId);

    String deleteCoupon(Long couponId);

    String applyCoupon(Long couponId, Long productId);
    String removeCoupon(Long productId);
}
