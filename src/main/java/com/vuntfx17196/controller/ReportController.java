package com.vuntfx17196.controller;

import static com.vuntfx17196.controller.AdminController.PAGE_SIZE_DEFAULT;
import static com.vuntfx17196.controller.AdminController.SORT_DIR_DEFAULT;
import static com.vuntfx17196.controller.AdminController.STATUS;
import static com.vuntfx17196.controller.AdminController.SUCCESS;
import static com.vuntfx17196.controller.AdminController.listModel;

import com.vuntfx17196.dto.RechargeDTO;
import com.vuntfx17196.model.Purchase;
import com.vuntfx17196.model.Recharge;
import com.vuntfx17196.model.User;
import com.vuntfx17196.service.PurchaseService;
import com.vuntfx17196.service.RechargeService;
import com.vuntfx17196.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/report")
public class ReportController {

  private final RechargeService rechargeService;
  private final PurchaseService purchaseService;
  private final UserService userService;

  public ReportController(RechargeService rechargeService, PurchaseService purchaseService,
      UserService userService) {
    this.rechargeService = rechargeService;
    this.purchaseService = purchaseService;
    this.userService = userService;
  }

  // report
  @GetMapping("/recharges")
  public String rechargeView(@RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "pageSize", defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
      @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
      @RequestParam(value = "sortDir", defaultValue = SORT_DIR_DEFAULT) String sortDir,
      @RequestParam(value = STATUS, defaultValue = "") String status, Model model) {
    Page<Recharge> recharges = null;
    if (StringUtils.hasText(status)) {
      recharges = rechargeService.getAllPerPageByStatus(status, page, pageSize, sortField, sortDir);
    } else {
      recharges = rechargeService.getAllPerPage(page, pageSize, sortField, sortDir);
    }
    if (recharges.hasContent()) {
      listModel(model, page, pageSize, sortField, sortDir, recharges);
    }
    model.addAttribute(STATUS, status);
    return "admin/rechargeHistory";
  }

  @GetMapping("/recharges/new")
  public String rechargeAdd(Model model) {
    model.addAttribute(new RechargeDTO());
    return "admin/rechargeAdd";
  }

  @PostMapping("/recharges/new")
  public String rechargeAdd(@Valid @ModelAttribute RechargeDTO rechargeDTO,
      BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
    User user = userService.getUserByEmail(rechargeDTO.getEmail());
    if (user == null) {
      bindingResult.rejectValue("email", null, "Email does not exists.");
    }
    if (bindingResult.hasErrors()) {
      model.addAttribute(rechargeDTO);
      return "admin/rechargeAdd";
    }
    // save new recharge vao csdl
    rechargeService.create(user, rechargeDTO);
    redirectAttributes.addFlashAttribute(SUCCESS,
        "Created new recharge for email: " + rechargeDTO.getEmail());
    return "redirect:/admin/report/recharges";
  }

  @GetMapping("/purchases")
  public String purchaseView(@RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "pageSize", defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
      @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
      @RequestParam(value = "sortDir", defaultValue = SORT_DIR_DEFAULT) String sortDir,
      Model model) {
    Page<Purchase> purchases = purchaseService.getAllPerPage(page, pageSize, sortField, sortDir);
    if (purchases.hasContent()) {
      listModel(model, page, pageSize, sortField, sortDir, purchases);
    }
    return "admin/purchaseHistory";
  }
}
