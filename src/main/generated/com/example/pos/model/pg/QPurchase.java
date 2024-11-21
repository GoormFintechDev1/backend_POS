package com.example.pos.model.pg;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPurchase is a Querydsl query type for Purchase
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPurchase extends EntityPathBase<Purchase> {

    private static final long serialVersionUID = -2011298392L;

    public static final QPurchase purchase = new QPurchase("purchase");

    public final StringPath itemName = createString("itemName");

    public final StringPath orderId = createString("orderId");

    public final StringPath paymentKey = createString("paymentKey");

    public final EnumPath<com.example.pos.model.enumset.PaymentStatus> paymentStatus = createEnum("paymentStatus", com.example.pos.model.enumset.PaymentStatus.class);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> purchasedAt = createDateTime("purchasedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> purchaseId = createNumber("purchaseId", Long.class);

    public QPurchase(String variable) {
        super(Purchase.class, forVariable(variable));
    }

    public QPurchase(Path<? extends Purchase> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPurchase(PathMetadata metadata) {
        super(Purchase.class, metadata);
    }

}

