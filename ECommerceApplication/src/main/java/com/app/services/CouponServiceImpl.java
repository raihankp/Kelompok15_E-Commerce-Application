package com.app.services;

import com.app.entites.Category;
import com.app.entites.Coupon;
import com.app.entites.Product;
import com.app.exceptions.APIException;
import com.app.exceptions.ResourceNotFoundException;
import com.app.payloads.CategoryDTO;
import com.app.payloads.CategoryResponse;
import com.app.payloads.CouponDTO;
import com.app.payloads.CouponResponse;
import com.app.repositories.CouponRepo;
import com.app.repositories.ProductRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService{

    @Autowired
    private CouponRepo couponRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepo productRepo;

    @Override
    public CouponDTO createCoupon(Coupon coupon) {
        Coupon savedCoupon = couponRepo.findByCouponName(coupon.getCouponName());

        if (savedCoupon != null){
            throw new APIException("Coupon with the name '" + coupon.getCouponName() + "' already exists !!!");
        }

        savedCoupon = couponRepo.save(coupon);

        return modelMapper.map(savedCoupon, CouponDTO.class);
    }

    @Override
    public CouponResponse getCoupons(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Coupon> pageCoupons = couponRepo.findAll(pageDetails);

        List<Coupon> coupons = pageCoupons.getContent();

        if (coupons.size() == 0) {
            throw new APIException("No category is created till now");
        }

        List<CouponDTO> couponDTOs = coupons.stream()
                .map(category -> modelMapper.map(category, CouponDTO.class)).collect(Collectors.toList());

        CouponResponse couponResponse = new CouponResponse();

        couponResponse.setContent(couponDTOs);
        couponResponse.setPageNumber(pageCoupons.getNumber());
        couponResponse.setPageSize(pageCoupons.getSize());
        couponResponse.setTotalElements(pageCoupons.getTotalElements());
        couponResponse.setTotalPages(pageCoupons.getTotalPages());
        couponResponse.setLastPage(pageCoupons.isLast());

        return couponResponse;
    }

    @Override
    public CouponDTO updateCoupon(Coupon coupon, Long couponId) {
        Coupon savedCoupon = couponRepo.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "coupon", couponId));

        coupon.setCouponId(couponId);

        savedCoupon = couponRepo.save(coupon);

        return modelMapper.map(savedCoupon, CouponDTO.class);
    }

    @Transactional
    @Override
    public String deleteCoupon(Long couponId) {
        Coupon coupon = couponRepo.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "couponId", couponId));

        List<Product> products = coupon.getProducts();
        // Remove coupon from associated products
        products.forEach(product -> {
            product.setCoupon(null);
            product.setDiscount(0);
            product.setSpecialPrice(product.getPrice()); // Restore original price
            productRepo.save(product);
        });

        productRepo.saveAll(products);

        couponRepo.delete(coupon);

        return "Coupon with couponId: " + couponId + " deleted successfully !!!";
    }

    @Override
    public String applyCoupon(Long couponId, Long productId) {
        Coupon savedCoupon = couponRepo.findById(couponId)
                .orElseThrow(() -> new APIException("Coupon with the id '" + couponId + "' not found !!!"));

        Product savedProduct = productRepo.findById(productId)
                .orElseThrow(() -> new APIException("Product with the id '" + productId + "' not found !!!"));

        if (Optional.ofNullable(savedProduct.getCoupon()).isPresent()) {
            throw new APIException("Product with the id '" + productId + "' already has a coupon applied !!!");
        }

        savedProduct.setCoupon(savedCoupon);
        savedProduct.setDiscount(savedCoupon.getDiscountPercentage());
        savedProduct.setSpecialPrice(savedProduct.getPrice() - (savedProduct.getPrice() * savedCoupon.getDiscountPercentage()/100));
        productRepo.save(savedProduct);

        return "Coupon successfully applied";
    }

    @Override
    public String removeCoupon(Long productId) {
        Product savedProduct = productRepo.findById(productId)
                .orElseThrow(() -> new APIException("Product with the id '" + productId + "' not found !!!"));

        if (Optional.ofNullable(savedProduct.getCoupon()).isEmpty()) {
            throw new APIException("Product with the id '" + productId + "' has no coupon applied !!!");
        }

        savedProduct.setCoupon(null);
        savedProduct.setDiscount(0);
        savedProduct.setSpecialPrice(0);
        productRepo.save(savedProduct);

        return "Coupon successfully removed";
    }
}
