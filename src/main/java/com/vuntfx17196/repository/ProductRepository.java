package com.vuntfx17196.repository;

import com.vuntfx17196.model.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

  Page<Product> findAllByCategory_Id(int id, Pageable pageable);

  Page<Product> findAll(Pageable pageable);

  // search theo chi muc fulltext
  @Query(value = "select * from products where match(title, short_detail, full_detail) against (?1 IN NATURAL LANGUAGE MODE)", nativeQuery = true)
  Page<Product> search(String keyword, Pageable pageable);

  @Query(nativeQuery = true, value = "select * from products where category_id = (?2) and match(title, short_detail, full_detail) against (?1 IN NATURAL LANGUAGE MODE) ")
  Page<Product> searchAllByCategory_Id(String keyword, int id, Pageable pageable);

  boolean existsByTitle(String title);

  List<Product> findTop5ByOrderByLastModifiedDateDesc();

  List<Product> findTop5ByOrderByViewTimesDesc();
}
