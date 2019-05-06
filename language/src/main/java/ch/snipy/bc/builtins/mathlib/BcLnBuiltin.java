package ch.snipy.bc.builtins.mathlib;

import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "ln")
public abstract class BcLnBuiltin extends BcBuiltinNode {
    @Specialization
    public BcBigNumber ln(BcBigNumber arg) {
        return arg.ln();
    }
}
