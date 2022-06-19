package com.jeffy.hotsearch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SearchHistory {
    @NonNull
    private String userId;

    @NonNull
    private String searchKey;
}
