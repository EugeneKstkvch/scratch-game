package y.kastsiukevich.pojo.combo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SameSymbolWinCombo implements WinCombo {
    private String name;
    private Double multiplier;
    private Integer count;
}
