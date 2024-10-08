package com.quickpik.entities;

import java.util.ArrayList;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Builder.Default
    private String exampleField = "defaultValue";

    @Id
    private String orderId;

    private String orderNumber;

    // PENDING, DISPATCHED, DELIVERED
    @Column(nullable = false, columnDefinition = "varchar(20) default 'PENDING'")    
    private String orderStatus;

    // NOT-PAID, PAID
    @Column(nullable = false, columnDefinition = "varchar(10) default 'NOT PAID'")    
    private String paymentStatus;

    @Column(nullable = false)
    private double orderAmount;

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private String shippingAddress;

    @Column(length = 6, nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String province;

    @Column(length=10, nullable = false)
    private String shippingPhone;

    @Column(nullable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveredDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final List<OrderItem> orderItems = new ArrayList<>();

    // Properly close the method and class
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems.clear(); // Clear existing items
        if (orderItems != null) {
            this.orderItems.addAll(orderItems);
        }
    }
}
