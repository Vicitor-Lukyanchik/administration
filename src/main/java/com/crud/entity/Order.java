package com.crud.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
@SequenceGenerator(
        name = "orders-gen",
        sequenceName = "orders_id_seq",
        initialValue = 1, allocationSize = 1)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders-gen")
    private Long id;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private String createdAt;

    @ManyToMany(mappedBy = "orders", fetch = FetchType.LAZY)
    private List<Product> products;

    public Order(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Product> getProducts() {
        return products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(user_id, order.user_id) && Objects.equals(status, order.status) && Objects.equals(createdAt, order.createdAt) && Objects.equals(products, order.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user_id, status, createdAt, products);
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
