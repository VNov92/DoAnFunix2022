package com.vuntfx17196.controller;

import com.vuntfx17196.dto.PasswordChangeDTO;
import com.vuntfx17196.global.AccountResourceException;
import com.vuntfx17196.global.BadRequestAlertException;
import com.vuntfx17196.model.Product;
import com.vuntfx17196.model.Purchase;
import com.vuntfx17196.model.Recharge;
import com.vuntfx17196.model.User;
import com.vuntfx17196.security.SecurityUtils;
import com.vuntfx17196.service.ProductService;
import com.vuntfx17196.service.PurchaseService;
import com.vuntfx17196.service.RechargeService;
import com.vuntfx17196.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {

  String downloadDir = System.getProperty("user.dir") + "/src/main/resources/static/downloadSample";
  private final UserService userService;
  private final ProductService productService;
  private final PurchaseService purchaseService;
  private final PasswordEncoder passwordEncoder;
  private final RechargeService rechargeService;

  public UserController(UserService userService, ProductService productService,
      PurchaseService purchaseService, PasswordEncoder passwordEncoder,
      RechargeService rechargeService) {
    this.userService = userService;
    this.productService = productService;
    this.purchaseService = purchaseService;
    this.passwordEncoder = passwordEncoder;
    this.rechargeService = rechargeService;
  }

  @ModelAttribute("name")
  public String getName() {
    Optional<String> currentUser = SecurityUtils.getCurrentNameUser();
    if (currentUser.isPresent()) {
      User user = userService.getUserByEmail(currentUser.get());
      return user.getName();
    }
    return null;
  }

  private User getUser() {
    Optional<String> currentUser = SecurityUtils.getCurrentNameUser();
    return currentUser.map(userService::getUserByEmail).orElse(null);
  }

  @GetMapping("")
  public String user(Model model) {
    return purchaseView(1, 3, "createdDate", "desc", model);
  }

  @PostMapping("")
  public String user(@RequestParam String name, RedirectAttributes redirectAttributes) {
    User user = getUser();
    if (user == null) {
      throw new AccountResourceException("Account was not found.");
    }
    if (name == null || name.isBlank()) {
      redirectAttributes.addFlashAttribute("changeName", "The name can't empty.");
    } else if (!name.equals(user.getName())) {
      userService.updateInfoUser(user, name, "");
      redirectAttributes.addFlashAttribute("success", "The name have been updated.");
    }
    return "redirect:/user/purchases";
  }

  // report
  @GetMapping("/recharges")
  public String rechargeView(@RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
      @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
      @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir, Model model) {
    User user = getUser();
    if (user == null) {
      throw new AccountResourceException("Account was not found.");
    }
    Page<Recharge> recharges = rechargeService.getAllPerPageByUser(user, page, pageSize, sortField,
        sortDir);
    if (recharges.hasContent()) {
      listModel(model, page, pageSize, sortField, sortDir, recharges);
    }
    model.addAttribute("title", "Recharges");
    return "userOption";
  }

  @GetMapping("/purchases")
  public String purchaseView(@RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
      @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
      @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir, Model model) {
    User user = getUser();
    if (user == null) {
      throw new AccountResourceException("Account was not found.");
    }
    Page<Purchase> purchases = purchaseService.getAllPerPageByUser(user, page, pageSize, sortField,
        sortDir);
    if (purchases.hasContent()) {
      listModel(model, page, pageSize, sortField, sortDir, purchases);
    }
    model.addAttribute("title", "Options");
    return "userOption";
  }

  /**
   * @param model       Model
   * @param currentPage current page
   * @param pageSize    page size
   * @param sortField   field sort
   * @param sortDir     sort direction
   * @param content     Page of object
   */
  private void listModel(Model model, Integer currentPage, Integer pageSize, String sortField,
      String sortDir, Page<?> content) {
    AdminController.reverseSortDir(model, currentPage, pageSize, sortField, sortDir, content);
  }

  @GetMapping("/download/{id}")
  @Transactional
  public String download(@PathVariable int id, HttpServletRequest request,
      HttpServletResponse response, RedirectAttributes redirectAttributes) {
    String link = request.getContextPath().concat("/product/" + id);
    Optional<Product> productOptional = productService.getProductNoTranformById(id);
    if (productOptional.isEmpty()) {
      throw new BadRequestAlertException("Invalid product");
    }
    // kiem tra user co the download hay khong
    Optional<String> currentEmail = SecurityUtils.getCurrentNameUser();
    if (currentEmail.isEmpty()) {
      throw new AccountResourceException("User was not found.");
    }
    User user = userService.getUserByEmail(currentEmail.get());
    if (user == null) {
      throw new AccountResourceException("User does not exists.");
    }
    if (user.getPoints() < productOptional.get().getCost()) {
      redirectAttributes.addFlashAttribute("warning", new String[]{"Purchase failed",
          "You do not enough points. <a href=\"#\">Recharge</a> now"});
      return "redirect:" + link;
    }
    purchaseService.create(user, productOptional.get());
    // bat dau thuc hien download
    BufferedInputStream bufferedInputStream = null;
    try {
      File file = new File(Paths.get(downloadDir, "fileDownload.txt").toUri());

      response.setHeader("Cache-Control", "must-revalidate");
      response.setHeader("Pragma", "public");
      response.setHeader("Content-Transfer-Encoding", "binary");
      response.setHeader("Content-disposition", "attachment; ");

      bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
      FileCopyUtils.copy(bufferedInputStream, response.getOutputStream());

    } catch (Exception e) {

    } finally {
      try {
        response.getOutputStream().flush();
        response.getOutputStream().close();
      } catch (Exception ex) {

      }

      try {
        if (bufferedInputStream != null) {
          bufferedInputStream.close();
        }
      } catch (Exception ex) {

      }
    }
    return null;
  }

  @GetMapping("/changepassword")
  public String userPasswordChange(Model model) {
    model.addAttribute(new PasswordChangeDTO());
    return "changePassword";
  }

  @PostMapping("/changepassword")
  public String userPasswordChange(@Valid @ModelAttribute PasswordChangeDTO passwordChangeDTO,
      BindingResult bindingResult, Model model) {
    User user = getUser();
    if (user == null) {
      throw new AccountResourceException("Account was not found.");
    }
    if (!passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword())) {
      bindingResult.rejectValue("currentPassword", null, "Wrong password.");
    }
    if (bindingResult.hasErrors()) {
      model.addAttribute("user", passwordChangeDTO);
    } else {
      // Change password
      userService.updateInfoUser(user, "", passwordChangeDTO.getNewPassword());
      model.addAttribute("success", "Updated password.");
    }
    return "changePassword";
  }

}
