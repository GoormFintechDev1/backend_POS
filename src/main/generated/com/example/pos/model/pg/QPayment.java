package com.example.pos.model.pg;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPayment is a Querydsl query type for Payment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayment extends EntityPathBase<Payment> {

    private static final long serialVersionUID = 1031864447L;

    public static final QPayment payment = new QPayment("payment");

    public final StringPath approvedAt = createString("approvedAt");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath currency = createString("currency");

    public final NumberPath<Long> easyPayAmount = createNumber("easyPayAmount", Long.class);

    public final NumberPath<Long> easyPayDiscountAmount = createNumber("easyPayDiscountAmount", Long.class);

    public final StringPath method = createString("method");

    public final StringPath orderId = createString("orderId");

    public final StringPath orderName = createString("orderName");

    public final NumberPath<Long> paymentId = createNumber("paymentId", Long.class);

    public final StringPath paymentKey = createString("paymentKey");

    public final BooleanPath paySuccessYN = createBoolean("paySuccessYN");

    public final EnumPath<com.example.pos.model.enumset.PayType> payType = createEnum("payType", com.example.pos.model.enumset.PayType.class);

    public final StringPath provider = createString("provider");

    public final StringPath requestedAt = createString("requestedAt");

    public final NumberPath<Long> totalAmount = createNumber("totalAmount", Long.class);

    public final NumberPath<Long> vat = createNumber("vat", Long.class);

    public QPayment(String variable) {
        super(Payment.class, forVariable(variable));
    }

    public QPayment(Path<? extends Payment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPayment(PathMetadata metadata) {
        super(Payment.class, metadata);
    }

}

