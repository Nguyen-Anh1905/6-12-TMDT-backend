package com.example.backend_tmdt.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.backend_tmdt.dto.request.CreateProductRequest;
import com.example.backend_tmdt.dto.request.SearchProductRequest;
import com.example.backend_tmdt.dto.request.UpdateProductRequest;
import com.example.backend_tmdt.dto.response.ProductListResponse;
import com.example.backend_tmdt.dto.response.ProductResponse;
import com.example.backend_tmdt.entity.CategoryEntity;
import com.example.backend_tmdt.entity.ProductEntity;
import com.example.backend_tmdt.entity.ShopEntity;
import com.example.backend_tmdt.mapper.ProductMapper;
import com.example.backend_tmdt.repository.CategoryRepository;
import com.example.backend_tmdt.repository.ProductRepository;
import com.example.backend_tmdt.repository.ShopRepository;
import com.example.backend_tmdt.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        CategoryEntity category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        }

        boolean autoApproved = isAutoApprovedCategory(category);

        ShopEntity shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        ProductEntity product = ProductEntity.builder()
                .productName(request.getProductName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .attributes(request.getAttributes())
                .category(category)
                .shop(shop)
                .status(1)
                .averageRating(0F)
                .salesCount(0)
                .isApproved(autoApproved)
                .build();

        ProductEntity saved = productRepository.save(product);
        return productMapper.toProductResponse(saved);
    }

    private boolean isAutoApprovedCategory(CategoryEntity category) {
        return category != null && category.getModerationLevel() != null && category.getModerationLevel() == 1;
    }

    @Override
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (request.getProductName() != null) {
            product.setProductName(request.getProductName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getAttributes() != null) {
            product.setAttributes(request.getAttributes());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        if (request.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(request.getCategoryId()).orElse(null);
            product.setCategory(category);
        }

        ProductEntity updated = productRepository.save(product);
        return productMapper.toProductResponse(updated);
    }

    @Override
    public ProductListResponse getVisibleProducts(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ProductEntity> productsPage = productRepository.findByStatusAndIsApproved(1, true, pageable);

        List<ProductResponse> content = productsPage.getContent()
                .stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());

        return ProductListResponse.builder()
                .content(content)
                .totalPages(productsPage.getTotalPages())
                .totalElements(productsPage.getTotalElements())
                .currentPage(page)
                .pageSize(pageSize)
                .build();
    }

    @Override
    public ProductListResponse searchProducts(SearchProductRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());

        Page<ProductEntity> productsPage;
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            if (request.getCategoryId() != null) {
                List<Long> categoryIds = collectCategoryAndDescendantIds(request.getCategoryId());
                productsPage = productRepository.searchProductsByCategoryIdsAndFilters(
                        request.getKeyword(),
                        request.getMinPrice() != null ? request.getMinPrice() : 0L,
                        request.getMaxPrice() != null ? request.getMaxPrice() : Long.MAX_VALUE,
                        categoryIds,
                        1,
                        true,
                        pageable);
            } else {
                productsPage = productRepository.searchProductsByFilters(
                        request.getKeyword(),
                        request.getMinPrice() != null ? request.getMinPrice() : 0L,
                        request.getMaxPrice() != null ? request.getMaxPrice() : Long.MAX_VALUE,
                        1,
                        true,
                        pageable);
            }
        } else if (request.getCategoryId() != null) {
            List<Long> categoryIds = collectCategoryAndDescendantIds(request.getCategoryId());
            productsPage = productRepository.findByCategoryCategoryIdInAndStatusAndIsApproved(
                    categoryIds, 1, true, pageable);
        } else {
            productsPage = productRepository.findByStatusAndIsApproved(1, true, pageable);
        }

        List<ProductResponse> content = productsPage.getContent()
                .stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());

        return ProductListResponse.builder()
                .content(content)
                .totalPages(productsPage.getTotalPages())
                .totalElements(productsPage.getTotalElements())
                .currentPage(request.getPage())
                .pageSize(request.getPageSize())
                .build();
    }

    @Override
    public ProductResponse getProductDetails(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStatus() == 0 || !product.getIsApproved()) {
            throw new RuntimeException("This product is not available");
        }

        return productMapper.toProductResponse(product);
    }

    @Override
    public ProductListResponse getProductsByShop(Long shopId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ProductEntity> productsPage = productRepository.findByShopShopIdAndStatusAndIsApproved(
                shopId, 1, true, pageable);

        List<ProductResponse> content = productsPage.getContent()
                .stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());

        return ProductListResponse.builder()
                .content(content)
                .totalPages(productsPage.getTotalPages())
                .totalElements(productsPage.getTotalElements())
                .currentPage(page)
                .pageSize(pageSize)
                .build();
    }

            @Override
    public ProductListResponse getProductsByCategory(Long categoryId, Integer page, Integer pageSize) {
            Pageable pageable = PageRequest.of(page, pageSize);
            List<Long> categoryIds = collectCategoryAndDescendantIds(categoryId);
            Page<ProductEntity> productsPage = productRepository.findByCategoryCategoryIdInAndStatusAndIsApproved(
                categoryIds, 1, true, pageable);

            List<ProductResponse> content = productsPage.getContent()
                .stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());

            return ProductListResponse.builder()
                .content(content)
                .totalPages(productsPage.getTotalPages())
                .totalElements(productsPage.getTotalElements())
                .currentPage(page)
                .pageSize(pageSize)
                .build();
            }

    @Override
    public ProductListResponse searchProductsByImage(MultipartFile image, Integer page, Integer pageSize) {
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("Vui long upload anh can tim kiem");
        }

        int safePage = page != null && page >= 0 ? page : 0;
        int safePageSize = pageSize != null && pageSize > 0 ? pageSize : 20;
        Path queryImagePath = null;

        try {
            queryImagePath = Files.createTempFile("image-search-", getSafeImageSuffix(image.getOriginalFilename()));
            image.transferTo(queryImagePath);

            PythonImageSearchOutput output = runImageSearchScript(queryImagePath);
            List<PythonImageMatch> matches = output.matches() != null ? output.matches() : List.of();

            if (matches.isEmpty()) {
                return ProductListResponse.builder()
                        .content(List.of())
                        .totalPages(0)
                        .totalElements(0L)
                        .currentPage(safePage)
                        .pageSize(safePageSize)
                        .build();
            }

            Map<Long, Float> similarityByProductId = matches.stream()
                    .filter(match -> match.productId() != null && match.similarity() != null)
                    .collect(Collectors.toMap(
                            PythonImageMatch::productId,
                            match -> match.similarity().floatValue(),
                            Math::max
                    ));

            List<Long> productIds = matches.stream()
                    .map(PythonImageMatch::productId)
                    .filter(similarityByProductId::containsKey)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Long, ProductEntity> productsById = productRepository.findAllById(productIds).stream()
                    .filter(product -> product.getStatus() != null && product.getStatus() == 1)
                    .filter(product -> Boolean.TRUE.equals(product.getIsApproved()))
                    .collect(Collectors.toMap(ProductEntity::getProductId, Function.identity()));

            List<ProductResponse> allResults = productIds.stream()
                    .map(productsById::get)
                    .filter(product -> product != null)
                    .map(product -> {
                        ProductResponse response = productMapper.toProductResponse(product);
                        response.setImageSimilarity(similarityByProductId.get(product.getProductId()));
                        return response;
                    })
                    .sorted(Comparator.comparing(
                            ProductResponse::getImageSimilarity,
                            Comparator.nullsLast(Comparator.reverseOrder())
                    ))
                    .collect(Collectors.toList());

            int fromIndex = Math.min(safePage * safePageSize, allResults.size());
            int toIndex = Math.min(fromIndex + safePageSize, allResults.size());
            List<ProductResponse> pageContent = allResults.subList(fromIndex, toIndex);
            int totalPages = allResults.isEmpty() ? 0 : (int) Math.ceil((double) allResults.size() / safePageSize);

            return ProductListResponse.builder()
                    .content(pageContent)
                    .totalPages(totalPages)
                    .totalElements((long) allResults.size())
                    .currentPage(safePage)
                    .pageSize(safePageSize)
                    .build();
        } catch (IOException exception) {
            throw new RuntimeException("Khong xu ly duoc anh tim kiem", exception);
        } finally {
            if (queryImagePath != null) {
                try {
                    Files.deleteIfExists(queryImagePath);
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public void deleteProduct(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.deleteById(productId);
    }

    private List<Long> collectCategoryAndDescendantIds(Long rootCategoryId) {
        CategoryEntity rootCategory = categoryRepository.findById(rootCategoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        List<Long> categoryIds = new ArrayList<>();
        Deque<Long> queue = new ArrayDeque<>();
        queue.add(rootCategory.getCategoryId());

        while (!queue.isEmpty()) {
            Long currentId = queue.poll();
            categoryIds.add(currentId);

            List<CategoryEntity> children = categoryRepository.findByParentCategoryCategoryId(currentId);
            for (CategoryEntity child : children) {
                queue.add(child.getCategoryId());
            }
        }

        return categoryIds;
    }

    private PythonImageSearchOutput runImageSearchScript(Path queryImagePath) throws IOException {
        Path scriptPath = findProjectPath("python", "searchModel.py");
        Path featureFilePath = findProjectPath("python", "image_features_resnet50.pkl");

        ProcessBuilder processBuilder = new ProcessBuilder(
                "python",
                scriptPath.toString(),
                "--query-image",
                queryImagePath.toString(),
                "--output",
                featureFilePath.toString(),
                "--threshold",
                "0.8",
                "--top-k",
                "200",
                "--json",
                "--device",
                "cpu"
        );
        processBuilder.directory(scriptPath.getParent().toFile());

        Process process = processBuilder.start();
        String stdout;
        String stderr;

        try {
            stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Python image search failed: " + stderr);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Python image search was interrupted", exception);
        }

        if (stdout.isBlank()) {
            throw new RuntimeException("Python image search returned empty result" + (stderr.isBlank() ? "" : ": " + stderr));
        }

        return objectMapper.readValue(stdout, PythonImageSearchOutput.class);
    }

    private Path findProjectPath(String first, String second) {
        Path userDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        List<Path> candidates = List.of(
                userDir.resolve(first).resolve(second),
                userDir.resolve("..").resolve(first).resolve(second),
                userDir.resolve("..").resolve("..").resolve(first).resolve(second)
        );

        return candidates.stream()
                .map(path -> path.toAbsolutePath().normalize())
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Khong tim thay file: " + first + "/" + second));
    }

    private String getSafeImageSuffix(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".jpg";
        }

        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        return List.of(".jpg", ".jpeg", ".png", ".bmp", ".webp").contains(suffix) ? suffix : ".jpg";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PythonImageSearchOutput(List<PythonImageMatch> matches) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PythonImageMatch(Long productId, Double similarity) {
    }
}
