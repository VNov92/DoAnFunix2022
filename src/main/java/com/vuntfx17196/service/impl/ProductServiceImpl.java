package com.vuntfx17196.service.impl;

import com.vuntfx17196.dto.ProductDTO;
import com.vuntfx17196.model.Product;
import com.vuntfx17196.repository.CategoryRepository;
import com.vuntfx17196.repository.ProductRepository;
import com.vuntfx17196.service.ProductService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

  ProductRepository productRepository;
  CategoryRepository categoryRepository;

  public ProductServiceImpl(ProductRepository productRepository,
      CategoryRepository categoryRepository) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Product> getTopViewTimes() {
    return productRepository.findTop5ByOrderByViewTimesDesc();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Product> getTop5ProductsOrderByLMD() {
    return productRepository.findTop5ByOrderByLastModifiedDateDesc();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Product> getAllProduct(int pageNum, int pageSize, String sortField, String sortDir) {
    return productRepository.findAll(getPageable(pageNum, pageSize, sortField, sortDir));
  }

  private Pageable getPageable(int pageNum, int pageSize, String sortField, String sortDir) {
    return PageRequest.of(pageNum - 1, pageSize,
        sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Product> getAllProductByCategoryId(int pageNum, int pageSize, int categoryId,
      String sortField, String sortDir) {
    return productRepository.findAllByCategory_Id(categoryId,
        getPageable(pageNum, pageSize, sortField, sortDir));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Product> searchAllProductByKeyword(String keyword, int pageNum, int pageSize,
      String sortField, String sortDir) {
    return productRepository.search(keyword,
        fieldDefaultFix(pageNum, pageSize, sortField, sortDir));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Product> search(String keyword, int pageNum, int pageSize) {
    Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
    return productRepository.search(keyword, pageable);
  }

  private Pageable fieldDefaultFix(int pageNum, int pageSize, String sortField, String sortDir) {
    if (sortField.equals("lastModifiedDate")) {
      sortField = "last_modified_date";
    }
    if (sortField.equals("category")) {
      sortField = "category_id";
    }
    return PageRequest.of(pageNum - 1, pageSize,
        sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Product> searchAllProductByKeywordWithCategory(String keyword, int pageNum,
      int pageSize, int categoryId, String sortField, String sortDir) {
    return productRepository.searchAllByCategory_Id(keyword, categoryId,
        fieldDefaultFix(pageNum, pageSize, sortField, sortDir));
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<ProductDTO> getProductById(int id) {
    return productRepository.findById(id).map(ProductDTO::new);
  }

  @Override
  public Optional<Product> getProductNoTranformById(int id) {
    return productRepository.findById(id);
  }

  @Override
  public long countAllProducts() {
    return productRepository.count();
  }

  @Override
  @Transactional(rollbackFor = {Throwable.class})
  public void removeProduct(Product product) throws IOException {
    productRepository.deleteById(product.getId());
  }

  @Override
  public void updateProduct(ProductDTO productDTO) {
    Product product = new Product();
    product.setId(productDTO.getId());
    product.setTitle(productDTO.getTitle());
    product.setCost(productDTO.getCost());
    product.setCategory(categoryRepository.findById(productDTO.getCategoryId()));
    product.setShortDetail(productDTO.getShortDetail());
    product.setFullDetail(productDTO.getFullDetail());
    product.setGgId(productDTO.getGgId());
    product.setImageThumbnail(productDTO.getImageThumbnail());

    productRepository.save(product);
  } // add or update dua vao pri-key

  @Override
  public void updateViewTimes(Product product) {
    product.setViewTimes(product.getViewTimes() + 1);
    productRepository.save(product);
  }

  @Override
  public boolean isExistProductTitle(String title) {
    return productRepository.existsByTitle(title);
  }

}