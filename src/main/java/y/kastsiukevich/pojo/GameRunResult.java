package y.kastsiukevich.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import y.kastsiukevich.pojo.symbol.BonusSymbol;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameRunResult {

    private String[][] matrix;
    private Double reward;
    @JsonProperty(value = "applied_winning_combinations")
    private Map<String, List<String>> winningCombinations;
    @JsonIgnore
    private BonusSymbol appliedBonusSymbol;


    @JsonProperty(value = "applied_bonus_symbol")
    public String getBonusSymbol() {
        return appliedBonusSymbol != null ? appliedBonusSymbol.getName() : null;
    }
}
