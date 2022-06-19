package com.github.eoniz.nexus.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private Integer x;
    private Integer y;

    public boolean equals(int i, int j) {
        return x == i && j == y;
    }

    public boolean equals(Position position) {
        return x.equals(position.getX()) && y.equals(position.getY());
    }
}
