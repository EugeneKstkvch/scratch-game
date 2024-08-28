package y.kastsiukevich.pojo.symbol;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class BonusSymbol implements Symbol {
    private String name;
    private String impact;

    @Override
    public String getName() {
        return this.name;
    }

    public abstract double bonusReward(double val);
}
