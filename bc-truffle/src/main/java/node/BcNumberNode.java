package node;

import com.oracle.truffle.api.source.SourceSection;

public class BcNumber extends BcExpressionNode {
    private final double value;

    public BcNumber(SourceSection sourceSection, double value) {
        super(sourceSection);
        this.value = value;
    }
}
