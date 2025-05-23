package com.example.marketplacer.repository;

import com.example.marketplacer.model.Seller;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SellerRepository extends JpaRepository<Seller, String> {

    Optional<Seller> findByEmail(String email);


}
