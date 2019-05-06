package ch.snipy.bc.builtins.mathlib;

import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "a")
public abstract class BcExpBuiltin extends BcBuiltinNode {
    @Specialization
    public BcBigNumber exp(BcBigNumber arg) {
        return arg.exp();
    }
}
