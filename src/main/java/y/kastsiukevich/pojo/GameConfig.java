package y.kastsiukevich.pojo;

import lombok.Data;
import y.kastsiukevich.pojo.combo.LinearWinCombo;
import y.kastsiukevich.pojo.combo.SameSymbolWinCombo;
import y.kastsiukevich.pojo.symbol.Symbol;

import java.util.List;
import java.util.Map;

@Data
public class GameConfig {

    private int columns;
    private int rows;
    private Map<String, Symbol> symbols;
    private List<Probability> probabilities;
    private Probability bonusProbabilities;
    private int probabilitiesTotalSum;
    private List<SameSymbolWinCombo> sameSymbolWinCombos;
    private List<LinearWinCombo> linearWinCombos;
}
