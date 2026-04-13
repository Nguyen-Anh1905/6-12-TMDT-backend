package com.example.backend_tmdt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone", nullable = false, unique = true, length = 15)
    private String phone;

    @Column(nullable = false)
    private Integer status;

    // User - Address (1 - N)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<AddressEntity> addresses;

    // User -> Role (N-N)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;

    // User - Shop (1 - 1)
    @OneToOne(mappedBy = "user")
    private ShopEntity shop;

    // User - cart (1-1)
    @OneToOne(mappedBy = "user")
    private CartEntity cart;

    // user - order (1-N)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<OrderEntity> orders;

    // user - review (1 -N)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews;
}