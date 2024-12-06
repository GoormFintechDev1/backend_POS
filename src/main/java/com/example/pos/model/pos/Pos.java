package com.example.pos.model.pos;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "pos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pos_id")
    private Long posId;

    // 더블리로 전송할 brNum
    @Column(name = "br_num", nullable = false, unique = true)
    private String brNum;

    @OneToMany(mappedBy = "pos", cascade = CascadeType.ALL)
    private List<Order> orders;
}
