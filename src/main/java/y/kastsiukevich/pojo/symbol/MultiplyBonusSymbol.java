package y.kastsiukevich.pojo.symbol;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MultiplyBonusSymbol extends BonusSymbol {
    private String name;
    private String impact;
    private Double multiply;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public double bonusReward(double val) {
        return val * this.multiply;
    }
}
