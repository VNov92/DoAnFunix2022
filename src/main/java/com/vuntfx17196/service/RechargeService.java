package com.vuntfx17196.service;

import com.vuntfx17196.dto.RechargeDTO;
import com.vuntfx17196.model.Recharge;
import com.vuntfx17196.model.User;
import org.springframework.data.domain.Page;

public interface RechargeService {

  void create(User user, RechargeDTO recharge);

  Page<Recharge> getAllPerPage(int pageNum, int pageSize, String sortField, String sortDir);

  Page<Recharge> getAllPerPageByUser(User user, int pageNum, int pageSize, String sortField,
      String sortDir);

  Page<Recharge> getAllPerPageByStatus(String status, int pageNum, int pageSize, String sortField,
      String sortDir);

  int sumRechargeAmount();
}
