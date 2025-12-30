package com.hackerrank.orm.model;

import com.hackerrank.orm.enums.ItemStatus;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId; 

    private String itemName;

    private String itemEnteredByUser;

    @CreationTimestamp
    private Timestamp itemEnteredDate;

    private Double itemBuyingPrice;

    private Double itemSellingPrice;

    @UpdateTimestamp
    private Timestamp itemLastModifiedDate;

    private String itemLastModifiedByUser;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus = ItemStatus.AVAILABLE;
}
