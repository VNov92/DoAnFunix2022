package com.vuntfx17196.service.impl;

import com.vuntfx17196.dto.RechargeDTO;
import com.vuntfx17196.model.Recharge;
import com.vuntfx17196.model.User;
import com.vuntfx17196.repository.RechargeRepository;
import com.vuntfx17196.repository.UserRepository;
import com.vuntfx17196.service.RechargeService;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RechargeServiceImpl implements RechargeService {

  private final RechargeRepository repository;
  private final UserRepository userRepository;

  public RechargeServiceImpl(RechargeRepository repository, UserRepository userRepository) {
    this.repository = repository;
    this.userRepository = userRepository;
  }

  @Override
  public void create(User user, RechargeDTO rechargeDTO) {
    Recharge recharge = new Recharge();
    recharge.setMoney(rechargeDTO.getMoney());
    recharge.setStatus(rechargeDTO.getStatus());
    recharge.setUser(user);
    repository.save(recharge);
    // update points neu trang thai la success
    if (rechargeDTO.getStatus().equals("success")) {
      int points = user.getPoints() + (rechargeDTO.getMoney() / 10000);
      user.setPoints(points);
    }
    user.setLastModifiedDate(Instant.now());
    userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Recharge> getAllPerPage(int pageNum, int pageSize, String sortField, String sortDir) {
    return repository.findAll(getPageable(pageNum, pageSize, sortField, sortDir));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Recharge> getAllPerPageByUser(User user, int pageNum, int pageSize, String sortField,
      String sortDir) {
    return repository.findAllByUser(user, getPageable(pageNum, pageSize, sortField, sortDir));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Recharge> getAllPerPageByStatus(String status, int pageNum, int pageSize,
      String sortField, String sortDir) {
    return repository.findAllByStatus(status, getPageable(pageNum, pageSize, sortField, sortDir));
  }

  @Override
  public int sumRechargeAmount() {
    return repository.sumRechargeAmountSuccess();
  }

  private Pageable getPageable(int pageNum, int pageSize, String sortField, String sortDir) {
    return PageRequest.of(pageNum - 1, pageSize,
        sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
  }
}
