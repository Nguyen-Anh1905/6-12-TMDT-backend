package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

    List<AddressEntity> findByUserUserIdOrderByIsDefaultDescAddressIdDesc(Long userId);

    Optional<AddressEntity> findByAddressIdAndUserUserId(Long addressId, Long userId);
}
