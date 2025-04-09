package io.github.hoo47.musinsa_assignment.application.usecase;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.hoo47.musinsa_assignment.application.category.service.CategoryQueryService;
import io.github.hoo47.musinsa_assignment.application.product.dto.response.BrandProductSummaryResponse;
import io.github.hoo47.musinsa_assignment.domain.product.dto.BrandCategoryPriceInfo;
import io.github.hoo47.musinsa_assignment.application.product.service.ProductQueryService;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BrandLowestPriceUsecase {

    private final CategoryQueryService categoryQueryService;
    private final ProductQueryService productQueryService;

    public BrandProductSummaryResponse getBrandWithLowestTotalPrice() {
        // 1. 카테고리별 모든 브랜드의 최저가 상품 조회
        List<BrandCategoryPriceInfo> results = productQueryService.findCheapestProductsGroupByBrandAndCategory();

        if (results.isEmpty()) {
            return null;
        }

        // 2. 전체 카테고리 개수 확인
        long totalCategories = categoryQueryService.getCategoryCount();

        // 3. 브랜드별 상품 정보 그룹핑 및 브랜드명 저장
        Map<Long, Map<Long, BrandCategoryPriceInfo>> brandCategoryProducts = new HashMap<>();
        Map<Long, String> brandNames = new HashMap<>();

        for (BrandCategoryPriceInfo info : results) {
            brandNames.put(info.brandId(), info.brandName());
            brandCategoryProducts.computeIfAbsent(info.brandId(), k -> new HashMap<>())
                    .put(info.categoryId(), info);
        }

        // 4. 모든 카테고리를 커버하는 브랜드 중 총 가격 계산
        record BrandTotalPrice(Long brandId, BigDecimal totalPrice) { }
        List<BrandTotalPrice> brandTotalPrices = brandCategoryProducts.entrySet().stream()
                .filter(entry -> entry.getValue().size() == totalCategories)
                .map(entry -> {
                    BigDecimal total = entry.getValue().values().stream()
                            .map(BrandCategoryPriceInfo::price)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new BrandTotalPrice(entry.getKey(), total);
                })
                .sorted(Comparator.comparing(BrandTotalPrice::totalPrice)
                        .thenComparing(bt -> brandNames.get(bt.brandId())))
                .toList();

        if (brandTotalPrices.isEmpty()) {
            return null;
        }

        // 5. 최저가 브랜드 선택 후 카테고리별 가격 정보 매핑
        BrandTotalPrice lowestPriceBrand = brandTotalPrices.get(0);
        Long targetBrandId = lowestPriceBrand.brandId();

        List<BrandProductSummaryResponse.CategoryPrice> categoryPrices = brandCategoryProducts.get(targetBrandId).values().stream()
                .map(info -> new BrandProductSummaryResponse.CategoryPrice(info.categoryName(), info.price()))
                .sorted(Comparator.comparing(BrandProductSummaryResponse.CategoryPrice::categoryName))
                .collect(Collectors.toList());

        // 6. BrandProductSummaryResponse로 반환
        return BrandProductSummaryResponse.of(brandNames.get(targetBrandId), categoryPrices, lowestPriceBrand.totalPrice());
    }
}
