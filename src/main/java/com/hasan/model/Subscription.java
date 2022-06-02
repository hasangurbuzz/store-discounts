package com.hasan.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class Subscription {
    private SubscriptionType type;
    private LocalDate since;
    private LocalDate until;
    private List<Benefit> benefits;
}
