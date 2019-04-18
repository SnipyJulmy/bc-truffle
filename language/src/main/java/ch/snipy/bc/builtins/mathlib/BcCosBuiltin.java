package ch.snipy.bc.builtins.mathlib;

import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "c")
public abstract class BcCosBuiltin extends BcBuiltinNode {
    @Specialization
    public BcBigNumber cos(BcBigNumber arg) {
        return arg.cos();
    }
}
