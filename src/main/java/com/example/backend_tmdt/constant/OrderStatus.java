package com.example.backend_tmdt.constant;

public final class OrderStatus {

    public static final int PENDING = 0;
    public static final int CONFIRMED = 1;
    public static final int SHIPPING = 2;
    public static final int DELIVERED = 3;
    public static final int CANCELLED = 4;
    public static final int COMPLETED = 5;

    private OrderStatus() {
    }

    public static String label(int status) {
        return switch (status) {
            case PENDING -> "Cho xac nhan";
            case CONFIRMED -> "Cho lay hang";
            case SHIPPING -> "Dang giao";
            case DELIVERED -> "Da giao";
            case CANCELLED -> "Da huy";
            case COMPLETED -> "Hoan thanh";
            default -> "Khong xac dinh";
        };
    }
}
