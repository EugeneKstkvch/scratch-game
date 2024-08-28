package y.kastsiukevich.pojo.symbol;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StandardSymbol implements Symbol {
    private String name;
    private Double multiplier;

    @Override
    public String getName() {
        return this.name;
    }
}
