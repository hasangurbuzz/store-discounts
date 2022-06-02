package com.hasan.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class User {
    private String username;
    private List<Subscription> subscriptions;
}
