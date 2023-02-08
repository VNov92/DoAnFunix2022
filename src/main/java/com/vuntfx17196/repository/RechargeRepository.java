package com.vuntfx17196.repository;

import com.vuntfx17196.model.Recharge;
import com.vuntfx17196.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RechargeRepository extends JpaRepository<Recharge, Integer> {

  Page<Recharge> findAllByUser(User user, Pageable pageable);

  Page<Recharge> findAllByStatus(String status, Pageable pageable);

  @Query("select sum(r.money) from Recharge r where r.status = 'success'")
  int sumRechargeAmountSuccess();
}
