package com.example.backend_tmdt.repository;

import com.example.backend_tmdt.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
	List<CategoryEntity> findByParentCategoryCategoryId(Long parentCategoryId);

	List<CategoryEntity> findAllByOrderByParentCategoryCategoryIdAscCategoryNameAsc();
}
