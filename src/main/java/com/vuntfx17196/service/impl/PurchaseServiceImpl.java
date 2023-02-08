package com.vuntfx17196.service.impl;

import com.vuntfx17196.model.Product;
import com.vuntfx17196.model.Purchase;
import com.vuntfx17196.model.User;
import com.vuntfx17196.repository.PurchaseRepository;
import com.vuntfx17196.repository.UserRepository;
import com.vuntfx17196.service.PurchaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseRepository repository;
    private final UserRepository userRepository;

    public PurchaseServiceImpl(PurchaseRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public void create(User user, Product product) {
        Purchase purchase = new Purchase();
        purchase.setCost(product.getCost());
        purchase.setUser(user);
        purchase.setProduct(product);
        repository.save(purchase);
        // updated points
        int points = user.getPoints() - product.getCost();
        user.setPoints(points);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Purchase> getAllPerPage(int pageNum, int pageSize, String sortField, String sortDir) {
        return repository.findAll(getPageable(pageNum, pageSize, sortField, sortDir));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Purchase> getAllPerPageByUser(User user, int pageNum, int pageSize, String sortField, String sortDir) {
        return repository.findAllByUser(user, getPageable(pageNum, pageSize, sortField, sortDir));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Purchase> getAllPerPageByProduct(Product product, int pageNum, int pageSize, String sortField, String sortDir) {
        return repository.findAllByProduct(product, getPageable(pageNum, pageSize, sortField, sortDir));
    }

    private Pageable getPageable(int pageNum, int pageSize, String sortField, String sortDir) {
        return PageRequest.of(pageNum - 1, pageSize, sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
    }
}
