package ch.snipy.bc.builtins.mathlib;

import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "s")
public abstract class BcSinBuiltin extends BcBuiltinNode {
    @Specialization
    public BcBigNumber sin(BcBigNumber arg) {
        return arg.sin();
    }
}
