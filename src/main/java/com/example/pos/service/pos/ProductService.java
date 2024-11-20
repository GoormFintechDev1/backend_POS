package com.example.pos.service.pos;

import com.example.pos.dto.pos.ProductDTO;
import com.example.pos.model.pos.Product;
import com.example.pos.repository.pos.ProductRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.pos.model.pos.QProduct.product;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final JPAQueryFactory queryFactory;
    private final ProductRepository productRepository;


    // 상품 생성
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product productEntity = Product.builder()
                .productName(productDTO.getProductName())
                .productPrice(productDTO.getProductPrice())
                .stockQuantity(productDTO.getStockQuantity())
                .build();

        Product savedProduct = productRepository.save(productEntity);

        return ProductDTO.builder()
                .productId(savedProduct.getProductId())
                .productName(savedProduct.getProductName())
                .productPrice(savedProduct.getProductPrice())
                .stockQuantity(savedProduct.getStockQuantity())
                .build();
    }


    // 상품 삭제
    public void deleteProduct(Long productId) {
        Product exsitingProduct = queryFactory
                .selectFrom(product)
                .where(product.productId.eq(productId))
                .fetchOne();

        if (exsitingProduct == null) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
        }

        productRepository.delete(exsitingProduct);
    }

    // 상품 수정
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product exsitingProduct = queryFactory.selectFrom(product)
                .where(product.productId.eq(productId))
                .fetchOne();

        if (exsitingProduct == null) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
        }


        exsitingProduct.setProductName(productDTO.getProductName());
        exsitingProduct.setProductPrice(productDTO.getProductPrice());
        exsitingProduct.setStockQuantity(productDTO.getStockQuantity());

        Product updateProduct = productRepository.save(exsitingProduct);

        return ProductDTO.builder()
                .productId(updateProduct.getProductId())
                .productName(updateProduct.getProductName())
                .productPrice(updateProduct.getProductPrice())
                .stockQuantity(updateProduct.getStockQuantity())
                .build();
    }


}
