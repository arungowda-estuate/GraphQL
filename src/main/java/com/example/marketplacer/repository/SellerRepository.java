package com.example.marketplacer.repository;

import com.example.marketplacer.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SellerRepository extends JpaRepository<Seller, String> {
}
