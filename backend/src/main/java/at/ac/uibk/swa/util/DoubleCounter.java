package at.ac.uibk.swa.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DoubleCounter {
    private int matchesFirst = 0;
    private int matchesSecond = 0;

    public void first() {
        this.matchesSecond ++;
    }
    public void second() {
        this.matchesSecond ++;
    }
}