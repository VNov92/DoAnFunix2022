package com.vuntfx17196.repository;

import com.vuntfx17196.model.ProductsView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsViewRepository extends JpaRepository<ProductsView, Integer> {

}
