package com.hackerrank.orm.model;

import com.hackerrank.orm.enums.ItemStatus;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemId;
    private String itemName;
    private String itemEnteredByUser;
    @CreationTimestamp
    private Timestamp itemEnteredDate;
    private double itemBuyingPrice;
    private double itemSellingPrice;
    private Date itemLastModifiedDate = new Date();
    private String itemLastModifiedByUser;
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus = ItemStatus.AVAILABLE;
}
