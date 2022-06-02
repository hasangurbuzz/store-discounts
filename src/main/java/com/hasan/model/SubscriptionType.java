package com.hasan.model;

public enum SubscriptionType {
    GOLD_CARD(0),
    SILVER_CARD(1),
    AFFILIATE(2),
    TWO_YEAR_MEMBERSHIP(3);

    private int priority;

    SubscriptionType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
