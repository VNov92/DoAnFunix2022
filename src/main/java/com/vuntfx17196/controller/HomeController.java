package com.vuntfx17196.controller;

import com.vuntfx17196.global.BadRequestAlertException;
import com.vuntfx17196.model.Category;
import com.vuntfx17196.model.Product;
import com.vuntfx17196.model.User;
import com.vuntfx17196.repository.ProductsViewRepository;
import com.vuntfx17196.security.SecurityUtils;
import com.vuntfx17196.service.CategoryService;
import com.vuntfx17196.service.ProductService;
import com.vuntfx17196.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

  private final CategoryService categoryService;
  private final ProductService productService;
  private final UserService userService;
  private final ProductsViewRepository productsViewRepository;
  private static final String PAGE_SIZE_DEFAULT = "3";

  public HomeController(CategoryService categoryService, UserService userService,
      ProductService productService, ProductsViewRepository productsViewRepository) {
    this.categoryService = categoryService;
    this.productService = productService;
    this.userService = userService;
    this.productsViewRepository = productsViewRepository;
  }

  @ModelAttribute("categories")
  public List<Category> categoryList() {
    return categoryService.getAllCategory();
  }

  @ModelAttribute("points")
  public Integer pointView() {
    Integer points = null;
    if (SecurityUtils.isAuthenticated()) {
      Optional<String> currentNameUser = SecurityUtils.getCurrentNameUser();
      if (currentNameUser.isPresent()) {
        User user = userService.getUserByEmail(currentNameUser.get());
        points = user.getPoints();
      }
    }
    return points;
  }

  @GetMapping({"", "/", "/home"})
  public String home(Model model) {
    return "index";
  }

  @GetMapping("/category")
  public String categoryWithPage(@RequestParam(value = "cate") Integer cate,
      @RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "pageSize", defaultValue = PAGE_SIZE_DEFAULT) int pageSize,
      @RequestParam(value = "sortField", defaultValue = "lastModifiedDate") String sortField,
      @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir, Model model) {
    Category category = categoryService.getCategoryById(cate);
    if (category == null) {
      throw new BadRequestAlertException("Invalid id.");
    }
    Page<Product> pageable = productService.getAllProductByCategoryId(page, pageSize, cate,
        sortField, sortDir);
    if (pageable.hasContent()) {
      model.addAttribute("cate", cate);
      listModel(model, page, pageSize, sortField, sortDir, pageable);
    }
    model.addAttribute("title", category.getTitle());
    return "category";
  }

  private void listModel(Model model, Integer currentPage, Integer pageSize, String sortField,
      String sortDir, Page<?> content) {
    AdminController.reverseSortDir(model, currentPage, pageSize, sortField, sortDir, content);
  }

  @GetMapping("/search")
  public String search(@RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
    String title = "Search";
    Page<Product> productPage = null;
    if (keyword != null && !keyword.isBlank()) {
      title = "Search for '" + keyword + "'";
      productPage = productService.search(keyword, page, Integer.parseInt(PAGE_SIZE_DEFAULT));
    }

    if (productPage != null && productPage.hasContent()) {
      listModel(model, page, null, "", "", productPage);
    }
    model.addAttribute("keyword", keyword);
    model.addAttribute("title", title);
    return "search";
  }

  @GetMapping("/product/{id}")
  public String product(@PathVariable int id, Model model) {
    Optional<Product> product = productService.getProductNoTranformById(id);
    if (product.isEmpty()) {
      throw new BadRequestAlertException("Invalid id.");
    }

    model.addAttribute("product", product.get());
    // Aside
    model.addAttribute("contentLeft", productService.getTop5ProductsOrderByLMD());
    model.addAttribute("contentRight", productsViewRepository.findAll());

    return "product";
  }
}
