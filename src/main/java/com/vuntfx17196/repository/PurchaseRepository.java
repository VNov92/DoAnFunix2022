package com.vuntfx17196.repository;

import com.vuntfx17196.model.Product;
import com.vuntfx17196.model.Purchase;
import com.vuntfx17196.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
    Page<Purchase> findAllByUser(User user, Pageable pageable);

    Page<Purchase> findAllByProduct(Product product, Pageable pageable);
}
