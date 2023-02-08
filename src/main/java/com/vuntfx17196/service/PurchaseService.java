package com.vuntfx17196.service;

import com.vuntfx17196.model.Product;
import com.vuntfx17196.model.Purchase;
import com.vuntfx17196.model.User;
import org.springframework.data.domain.Page;

public interface PurchaseService {
    void create(User user, Product product);

    Page<Purchase> getAllPerPage(int pageNum, int pageSize, String sortField, String sortDir);

    Page<Purchase> getAllPerPageByUser(User user, int pageNum, int pageSize, String sortField, String sortDir);

    Page<Purchase> getAllPerPageByProduct(Product product, int pageNum, int pageSize, String sortField, String sortDir);
}
