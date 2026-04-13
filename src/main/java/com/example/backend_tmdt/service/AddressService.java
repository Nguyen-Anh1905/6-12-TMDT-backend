package com.example.backend_tmdt.service;

import com.example.backend_tmdt.entity.AddressEntity;
import com.example.backend_tmdt.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public List<AddressEntity> findAll() {
        return addressRepository.findAll();
    }

    public Optional<AddressEntity> findById(Long id) {
        return addressRepository.findById(id);
    }

    public AddressEntity save(AddressEntity entity) {
        return addressRepository.save(entity);
    }

    public void deleteById(Long id) {
        addressRepository.deleteById(id);
    }
}
