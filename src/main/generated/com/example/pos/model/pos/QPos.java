package com.example.pos.model.pos;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPos is a Querydsl query type for Pos
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPos extends EntityPathBase<Pos> {

    private static final long serialVersionUID = 406664714L;

    public static final QPos pos = new QPos("pos");

    public final StringPath brNum = createString("brNum");

    public final ListPath<Order, QOrder> orders = this.<Order, QOrder>createList("orders", Order.class, QOrder.class, PathInits.DIRECT2);

    public final NumberPath<Long> posId = createNumber("posId", Long.class);

    public QPos(String variable) {
        super(Pos.class, forVariable(variable));
    }

    public QPos(Path<? extends Pos> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPos(PathMetadata metadata) {
        super(Pos.class, metadata);
    }

}

