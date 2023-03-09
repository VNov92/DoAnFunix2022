package com.vuntfx17196.controller;

import static com.vuntfx17196.security.AuthoritiesConstants.ADMIN;

import com.vuntfx17196.dto.ProductDTO;
import com.vuntfx17196.dto.UserDTO;
import com.vuntfx17196.dto.UserUpdateDTO;
import com.vuntfx17196.global.AccountResourceException;
import com.vuntfx17196.global.BadRequestAlertException;
import com.vuntfx17196.model.Category;
import com.vuntfx17196.model.Product;
import com.vuntfx17196.model.Role;
import com.vuntfx17196.model.User;
import com.vuntfx17196.repository.RoleRepository;
import com.vuntfx17196.service.CategoryService;
import com.vuntfx17196.service.IGoogleDriveFile;
import com.vuntfx17196.service.MailService;
import com.vuntfx17196.service.ProductService;
import com.vuntfx17196.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private final CategoryService categoryService;
  private final ProductService productService;
  private final UserService userService;
  private final RoleRepository roleRepository;
  private final HttpSession session;
  private final MailService mailService;
  private final IGoogleDriveFile iGoogleDriveFile;
  String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";
  static final String SORT_FIELD_DEFAULT = "lastModifiedDate";
  static final String SORT_DIR_DEFAULT = "desc";
  static final int PAGE_NUM_DEFAULT = 1;
  static final String PAGE_SIZE_DEFAULT = "3";
  static final String STATUS = "status";
  static final String SUCCESS = "success";
  static final String IMG_ID = "oldImageNameForDelete";
  private String keywordDefault = "";
  private Integer idDefault = 0;

  public AdminController(CategoryService categoryService, ProductService productService,
      UserService userService, RoleRepository roleRepository, HttpSession session,
      MailService mailService, IGoogleDriveFile iGoogleDriveFile) {
    this.categoryService = categoryService;
    this.productService = productService;
    this.userService = userService;
    this.roleRepository = roleRepository;
    this.session = session;
    this.mailService = mailService;
    this.iGoogleDriveFile = iGoogleDriveFile;
  }

  @ModelAttribute("categories")
  public List<Category> categoryList() {
    return categoryService.getAllCategory();
  }

  @ModelAttribute("roles")
  public List<Role> roleList() {
    return roleRepository.findAll();
  }


  /**
   * @param model       Model
   * @param currentPage current page
   * @param pageSize    page size
   * @param sortField   field sort
   * @param sortDir     sort direction
   * @param content     Page of object
   */
  static void listModel(Model model, Integer currentPage, Integer pageSize, String sortField,
      String sortDir, Page<?> content) {
    reverseSortDir(model, currentPage, pageSize, sortField, sortDir, content);
  }

  static void reverseSortDir(Model model, Integer currentPage, Integer pageSize, String sortField,
      String sortDir, Page<?> content) {
    String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
    model.addAttribute("currentPage", currentPage);
    model.addAttribute("pageSize", pageSize);
    model.addAttribute("totalPages", content.getTotalPages());
    model.addAttribute("totalElements", content.getTotalElements());
    model.addAttribute("sortField", sortField);
    model.addAttribute("sortDir", sortDir);
    model.addAttribute("reverseSortDir", reverseSortDir);
    model.addAttribute("content", content.getContent());
  }

  @GetMapping({"", "/dashboard"})
  public String dashboard(Model model) {
    model.addAttribute("totalMembers", userService.countByRole("ROLE_USER"));
    model.addAttribute("totalAdmins", userService.countByRole("ROLE_ADMIN"));
    model.addAttribute("totalCategories", categoryService.countAllCategories());
    model.addAttribute("totalProducts", productService.countAllProducts());
    return "admin/dashboard";
  }

  // category
  @GetMapping("/categories")
  public String categories(Model model) {
    return "admin/category";
  }

  @GetMapping("/categories/new")
  public String categoryAdd(Model model) {
    idDefault = 0;
    model.addAttribute("category", new Category());
    model.addAttribute(STATUS, "Add");
    return "/admin/categoryAdd";
  }

  @PostMapping("/categories/add")
  public String categoryAdd(@Valid @ModelAttribute("category") Category category,
      BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("category", category);
      model.addAttribute(STATUS, "Add");
      return "admin/categoryAdd";
    }

    if (!Objects.equals(category.getId(), idDefault)) {
      throw new BadRequestAlertException("Invalid id.");
    }

    categoryService.updateCategory(category);
    redirectAttributes.addFlashAttribute(SUCCESS, "Added new category: " + category.getTitle());
    return "redirect:/admin/categories";
  }

  @GetMapping("/categories/edit/{id}")
  public String categoryEdit(@PathVariable int id, Model model) {
    Category category = categoryService.getCategoryById(id);

    if (category == null) {
      throw new BadRequestAlertException("Invalid id.");
    }

    idDefault = id;

    model.addAttribute("category", category);
    model.addAttribute(STATUS, "Edit");
    return "admin/categoryAdd";
  }

  @GetMapping("/categories/delete/{id}")
  public String categoryDelete(@PathVariable int id, RedirectAttributes redirectAttributes) {
    Category category = categoryService.getCategoryById(id);
    if (category == null) {
      throw new BadRequestAlertException("Invalid id.");
    }
    categoryService.deleteCategoryById(id);
    redirectAttributes.addFlashAttribute(SUCCESS, "Deleted category with id: " + id);
    return "redirect:/admin/categories";
  }

  // product
  @GetMapping("/products")
  public String productPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "pageSize", defaultValue = PAGE_SIZE_DEFAULT) Integer pageSize,
      @RequestParam(value = "sortField", defaultValue = SORT_FIELD_DEFAULT) String sortField,
      @RequestParam(value = "sortDir", defaultValue = SORT_DIR_DEFAULT) String sortDir,
      @RequestParam(value = "cate", required = false) Integer categoryId,
      @RequestParam(value = "keyword", required = false) String keyword, Model model) {

    Page<Product> pageable = null;
    if (categoryId == null || categoryId < 1) {
      if (!StringUtils.hasText(keyword)) {
        pageable = productService.getAllProduct(page, pageSize, sortField, sortDir);
      } else {
        if (!keyword.equals(keywordDefault)) {
          page = PAGE_NUM_DEFAULT;
          keywordDefault = keyword;
        }
        pageable = productService.search(keyword, page, pageSize);
      }
    } else {
      if (!StringUtils.hasText(keyword)) {
        pageable = productService.getAllProductByCategoryId(page, pageSize, categoryId, sortField,
            sortDir);
      } else {
        if (!keyword.equals(keywordDefault)) {
          page = PAGE_NUM_DEFAULT;
          keywordDefault = keyword;
        }
        pageable = productService.searchAllProductByKeywordWithCategory(keyword, page, pageSize,
            categoryId, sortField, sortDir);
      }
    }
    model.addAttribute("cate", categoryId);
    model.addAttribute("keyword", keyword);
    listModel(model, page, pageSize, sortField, sortDir, pageable);
    return "admin/product";
  }

  @GetMapping("/products/new")
  public String productAdd(Model model) {
    idDefault = 0;
    model.addAttribute("product", new ProductDTO());
    session.setAttribute(STATUS, "Add");
    return "admin/productAdd";
  }

  @PostMapping("/products/new")
  public String productAdd(@Valid @ModelAttribute("product") ProductDTO productDTO,
      BindingResult bindingResult, @RequestParam("productImage") MultipartFile fileProductImage,
      @RequestParam("fileUploadGGDrive") MultipartFile fileUploadGGDrive,
      Model model, RedirectAttributes redirectAttributes)
      throws IOException {
    // kiem tra id truoc khi thao tac
    if (productDTO.getId() != idDefault.intValue()) {
      throw new BadRequestAlertException("Invalid id.");
    }
    // kiem tra tieu de neu id = 0 (new or copy)
    if (productDTO.getId() == 0 && productService.isExistProductTitle(productDTO.getTitle())) {
      bindingResult.rejectValue("title", null, "Title already exists");
    }

    // Lay Path cua image
    String imgUUID = "";

    if (!fileProductImage.isEmpty()) {
      imgUUID = fileProductImage.getOriginalFilename();
    } else if (session.getAttribute(IMG_ID) != null) {
      imgUUID = session.getAttribute(IMG_ID).toString();
    }
    productDTO.setUrl(imgUUID);

    if (bindingResult.hasErrors()) {
      model.addAttribute("product", productDTO);
      return "admin/productAdd";
    }
    // Neu co nhap file, upload len Google Drive, tra ve ten file de luu vao CSDL
    if (!fileUploadGGDrive.isEmpty()) {
      String filePath = categoryService.getCategoryById(productDTO.getCategoryId()).getTitle();
      productDTO.setGgId(iGoogleDriveFile.uploadFile(fileUploadGGDrive, filePath, true));
    }

    productService.updateProduct(productDTO);
    if (session.getAttribute(IMG_ID) != null && !fileProductImage.isEmpty()) {
      productService.removeImages(uploadDir,
          session.getAttribute(IMG_ID).toString());
    }
    session.removeAttribute(IMG_ID);
    session.removeAttribute(STATUS);
    if (!fileProductImage.isEmpty()) {
      productService.addImages(uploadDir, imgUUID, fileProductImage);
    }

    redirectAttributes.addFlashAttribute(SUCCESS, "Updated product.");
    return "redirect:/admin/products";
  }

  @GetMapping("/products/edit/{id}")
  public String productEdit(@PathVariable int id, Model model)
      throws GeneralSecurityException, IOException {
    Optional<ProductDTO> product = productService.getProductById(id);
    if (product.isEmpty()) {
      throw new BadRequestAlertException("Invalid id.");
    }

    idDefault = product.get().getId();
    String ggId = product.get().getGgId();
    if (ggId != null && !ggId.isBlank()) {
      model.addAttribute("fileGGDrive",
          iGoogleDriveFile.getFile(ggId).getThumbnailLink());
      System.out.println(iGoogleDriveFile.getFile(ggId).getThumbnailLink());
    } else {
      System.out.println("Google Drive ID is null");
    }

    model.addAttribute("product", product.get());
    session.setAttribute(STATUS, "Edit");
    session.setAttribute(IMG_ID, product.get().getUrl());
    return "admin/productAdd";
  }

  @GetMapping("/products/copy/{id}")
  public String productCopy(@PathVariable int id, Model model) {
    Optional<ProductDTO> optional = productService.getProductById(id);
    ProductDTO productDTO = null;
    if (optional.isPresent()) {
      productDTO = optional.get();
      productDTO.setId(0);
    } else {
      throw new BadRequestAlertException("Invalid id.");
    }
    model.addAttribute("product", productDTO);
    model.addAttribute(STATUS, "Copy");
    return "admin/productAdd";
  }

  @GetMapping("/products/delete/{id}")
  public String productDelete(@PathVariable int id, RedirectAttributes redirectAttributes)
      throws IOException {
    Optional<Product> product = productService.getProductNoTranformById(id);
    if (product.isPresent()) {
      productService.removeProduct(product.get(), uploadDir);
      redirectAttributes.addFlashAttribute(SUCCESS, "Deleted product with id: " + id);
      return "redirect:/admin/products";
    }
    throw new BadRequestAlertException("Invalid id.");
  }

  @GetMapping("/users")
  public String userViewPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "pageSize", defaultValue = PAGE_SIZE_DEFAULT) int pageSize,
      @RequestParam(value = "sortField", defaultValue = SORT_FIELD_DEFAULT) String sortField,
      @RequestParam(value = "sortDir", defaultValue = SORT_DIR_DEFAULT) String sortDir,
      @RequestParam(value = "role", required = false) Integer role,
      @RequestParam(value = "keyword", required = false) String keyword, Model model) {
    Page<User> pageable = null;
    // kiem tra role
    Optional<Role> rolePage = Optional.empty();
    if (role != null && role > 0) {
      rolePage = roleRepository.findById(role);
      if (rolePage.isEmpty()) {
        throw new BadRequestAlertException("Invalid role");
      }
    }
    if (!StringUtils.hasText(keyword)) {
      if (role == null || role == 0) {
        pageable = userService.getAllUsers(page, pageSize, sortField, sortDir);
      } else {
        pageable = userService.getAllUsersByRole(rolePage.get(), page, pageSize, sortField,
            sortDir);
      }
    } else {
      if (!keyword.equals(keywordDefault)) {
        page = PAGE_NUM_DEFAULT;
        keywordDefault = keyword;
      }
      if (role == null || role == 0) {
        pageable = userService.searchAllByEmail(keyword, page, pageSize, sortField,
            sortDir);
      } else {
        pageable = userService.searchAllByEmailAndRole(keyword, rolePage.get(), page, pageSize,
            sortField, sortDir);
      }
    }
    listModel(model, page, pageSize, sortField, sortDir, pageable);
    model.addAttribute("role", role);
    model.addAttribute("keyword", keyword);
    return "admin/user";
  }

  @GetMapping("/users/new")
  public String userAdd(Model model) {
    model.addAttribute("user", new UserDTO());
    model.addAttribute(STATUS, "Add");
    return "admin/userAdd";
  }

  @PostMapping("/users/new")
  public String userAdd(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult,
      Model model, RedirectAttributes redirectAttributes) {
    // kiem tra id truoc khi thao tac
    checkValidId(userDTO.getId());
    // kiem tra tieu de neu id = 0 (new)
    if (userService.getUserByEmail(userDTO.getEmail()) != null) {
      bindingResult.rejectValue("email", null, "User already exists");
    }
    if (bindingResult.hasErrors()) {
      model.addAttribute("user", userDTO);
      model.addAttribute(STATUS, "Add");
      return "admin/userAdd";
    }
    userService.createUser(userDTO);
    redirectAttributes.addFlashAttribute(SUCCESS,
        "Created user with email: " + userDTO.getEmail());

    return "redirect:/admin/users";
  }

  @PostMapping("/users/edit")
  public String userEdit(@Valid @ModelAttribute("user") UserUpdateDTO userUpdateDTO,
      BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes,
      HttpServletRequest request) {
    // kiem tra id truoc khi thao tac
    checkValidId(userUpdateDTO.getId());
    Optional<User> userOptional = userService.getUserById(userUpdateDTO.getId());
    if (userOptional.isEmpty()) {
      throw new AccountResourceException("Account was not exists.");
    }
    if (userOptional.get().getRoles().stream().map(Role::getId).toList().contains(1)) {
      if (!userUpdateDTO.getRoleIds().contains(1)) {
        bindingResult.rejectValue("roleIds", null, "Can't remove admin role of this user.");
      }
    }
    if (bindingResult.hasErrors()) {
      model.addAttribute("user", userUpdateDTO);
      model.addAttribute(STATUS, "Edit");
      return "admin/userAdd";
    }
    userService.updateUser(userUpdateDTO);
    redirectAttributes.addFlashAttribute(SUCCESS,
        "Updated user with id: " + userUpdateDTO.getId());

    return "redirect:/admin/users";
  }

  @GetMapping("/users/resetpassword/{id}")
  public String resetPasswordByAdmin(@PathVariable int id, RedirectAttributes redirectAttributes) {
    Optional<User> userOptional = userService.getUserById(id);
    if (userOptional.isEmpty()) {
      throw new BadRequestAlertException("Invalid id");
    }
    String resetPassword = userService.resetPasswordByAdmin(userOptional.get());
    mailService.sendPasswordResetByAdmin(userOptional.get(), resetPassword);
    String[] content = {"Reset password user", userOptional.get().getEmail() + "," + resetPassword};
    redirectAttributes.addFlashAttribute("warning", content);
    return "redirect:/admin/users";
  }

  @GetMapping("/users/edit/{id}")
  public String userEdit(@PathVariable int id, Model model) {
    Optional<UserUpdateDTO> userDTO = userService.getUserById(id).map(UserUpdateDTO::new);
    if (userDTO.isEmpty()) {
      throw new BadRequestAlertException("Invalid id");
    }
    idDefault = id;
    model.addAttribute("user", userDTO.get());
    model.addAttribute(STATUS, "Edit");
    return "admin/userAdd";
  }

  @GetMapping("/users/delete/{id}")
  public String userDelete(@PathVariable int id, RedirectAttributes redirectAttributes) {
    Optional<User> userOptional = userService.getUserById(id);
    if (userOptional.isEmpty()) {
      throw new BadRequestAlertException("Invalid id");
    }
    boolean isAdmin = userOptional.get().getRoles().contains(roleRepository.findByName(ADMIN));
    if (isAdmin) {
      String[] content = {"Delete User", "Can't delete ADMIN user."};
      redirectAttributes.addFlashAttribute("warning", content);
      return "redirect:/admin/users";
    }

    userService.removeUserById(userOptional.get());
    redirectAttributes.addFlashAttribute(SUCCESS, "Deleted user with id: " + id);
    return "redirect:/admin/users";
  }

  private void checkValidId(Integer id) {
    if (id == null || !Objects.equals(id, idDefault)) {
      throw new BadRequestAlertException("Invalid id.");
    }
  }

}
















