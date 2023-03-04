package com.vuntfx17196.service;

import com.vuntfx17196.dto.ProductDTO;
import com.vuntfx17196.model.Product;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

  List<Product> getTopViewTimes();

  List<Product> getTop5ProductsOrderByLMD();

  Page<Product> getAllProduct(int pageNum, int pageSize, String sortField, String sortDir);

  Page<Product> getAllProductByCategoryId(int pageNum, int pageSize, int categoryId,
      String sortField, String sortDir);

  Page<Product> searchAllProductByKeyword(String keyword, int pageNum, int pageSize,
      String sortField, String sortDir);

  Page<Product> search(String keyword, int pageNum, int pageSize);

  Page<Product> searchAllProductByKeywordWithCategory(String keyword, int pageNum, int pageSize,
      int categoryId, String sortField, String sortDir);

  Optional<ProductDTO> getProductById(int id);

  Optional<Product> getProductNoTranformById(int id);

  long countAllProducts();

  void removeProduct(Product product, String imageDir) throws IOException;

  void removeListProductById(Integer[] listProductIdDelete, String imageDir);

  void updateProduct(ProductDTO product);

  void updateViewTimes(Product product);

  boolean isExistProductTitle(String title);

  void addImages(String imageDir, String imageName, MultipartFile multipartFile) throws IOException;

  void removeImages(String imageDir, String imageName) throws IOException;

}
