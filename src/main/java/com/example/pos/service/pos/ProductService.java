package com.example.pos.service.pos;

import com.example.pos.dto.pos.ProductDTO;
import com.example.pos.model.pos.Product;
import com.example.pos.repository.pos.ProductRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
                .build();

        Product savedProduct = productRepository.save(productEntity);

        return ProductDTO.builder()
                .productId(savedProduct.getProductId())
                .productName(savedProduct.getProductName())
                .productPrice(savedProduct.getProductPrice())
                .build();
    }

    // 상품 조회
    public List<ProductDTO> getAllProduct() {
        return queryFactory.selectFrom(product)
                .fetch()
                .stream()
                .map(p -> ProductDTO.builder()
                        .productId(p.getProductId())
                        .productName(p.getProductName())
                        .productPrice(p.getProductPrice())
                        .build())
                .collect(Collectors.toList());
    }


    // 상품 삭제
    public void deleteProduct(Long productId) {
        Product existingProduct = queryFactory
                .selectFrom(product)
                .where(product.productId.eq(productId))
                .fetchOne();

        if (existingProduct == null) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
        }

        productRepository.delete(existingProduct);
    }

    // 상품 수정
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = queryFactory.selectFrom(product)
                .where(product.productId.eq(productId))
                .fetchOne();

        if (existingProduct == null) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
        }


        existingProduct.setProductName(productDTO.getProductName());
        existingProduct.setProductPrice(productDTO.getProductPrice());

        Product updateProduct = productRepository.save(existingProduct);

        return ProductDTO.builder()
                .productId(updateProduct.getProductId())
                .productName(updateProduct.getProductName())
                .productPrice(updateProduct.getProductPrice())
                .build();
    }




}
