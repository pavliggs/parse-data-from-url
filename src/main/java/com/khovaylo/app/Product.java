package com.khovaylo.app;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

/**
 * Объект, содержащий информацию о товаре
 * @author Pavel Khovaylo
 */
public class Product {
    /**
     * описание товара
     */
    private final String title;
    /**
     * минимальная цена без скидки
     */
    private final Double originalMinPrice;
    /**
     * максимальная цена без скидки
     */
    private final Double originalMaxPrice;
    /**
     * минимальная цена со скидкой
     */
    private final Double minPrice;
    /**
     * максимальная цена со скидкой
     */
    private final Double maxPrice;
    /**
     * размер скидки
     */
    private final Double discount;
    /**
     * остаток товара
     */
    private final Integer stock;
    /**
     * количество заказов от начала проведения акции
     */
    private final Integer ordersFromStartPromotion;
    /**
     * общее количество заказов
     */
    private final Integer totalOrders;
    /**
     * оценка товара по пятизвездочной системе
     */
    private final Double productAverageStar;
    /**
     * количество отзывов о товаре
     */
    private final Integer reviewsNumber;
    /**
     * время и дата добавления товара в онлайн-магазин
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final ZonedDateTime created;
    /**
     * время и дата начала акции
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final ZonedDateTime startPromotion;
    /**
     * время и дата окончания акции
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final ZonedDateTime endPromotion;

    public Product(String title,
                   Double originalMinPrice,
                   Double originalMaxPrice,
                   Double minPrice,
                   Double maxPrice,
                   Double discount,
                   Integer stock,
                   Integer ordersFromStartPromotion,
                   Integer totalOrders,
                   Double productAverageStar,
                   Integer reviewsNumber,
                   ZonedDateTime created,
                   ZonedDateTime startPromotion,
                   ZonedDateTime endPromotion) {
        this.title = title;
        this.originalMinPrice = originalMinPrice;
        this.originalMaxPrice = originalMaxPrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.discount = discount;
        this.stock = stock;
        this.ordersFromStartPromotion = ordersFromStartPromotion;
        this.totalOrders = totalOrders;
        this.productAverageStar = productAverageStar;
        this.reviewsNumber = reviewsNumber;
        this.created = created;
        this.startPromotion = startPromotion;
        this.endPromotion = endPromotion;
    }

    public String getTitle() {
        return title;
    }

    public Double getOriginalMinPrice() {
        return originalMinPrice;
    }

    public Double getOriginalMaxPrice() {
        return originalMaxPrice;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public Double getDiscount() {
        return discount;
    }

    public Integer getStock() {
        return stock;
    }

    public Integer getOrdersFromStartPromotion() {
        return ordersFromStartPromotion;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public Double getProductAverageStar() {
        return productAverageStar;
    }

    public Integer getReviewsNumber() {
        return reviewsNumber;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public ZonedDateTime getStartPromotion() {
        return startPromotion;
    }

    public ZonedDateTime getEndPromotion() {
        return endPromotion;
    }

    @Override
    public String toString() {
        return "Product{" +
                "title='" + title + '\'' +
                ", originalMinPrice=" + originalMinPrice +
                ", originalMaxPrice=" + originalMaxPrice +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", discount=" + discount +
                ", stock=" + stock +
                ", ordersFromStartPromotion=" + ordersFromStartPromotion +
                ", totalOrders=" + totalOrders +
                ", productAverageStar=" + productAverageStar +
                ", reviewsNumber=" + reviewsNumber +
                ", created=" + created +
                ", startPromotion=" + startPromotion +
                ", endPromotion=" + endPromotion +
                '}';
    }
}