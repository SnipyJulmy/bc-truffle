package ch.snipy.bc.builtins;

import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "length")
public abstract class BcLengthBuiltin extends BcBuiltinNode {
    @Specialization
    public BcBigNumber length(BcBigNumber arg) {
        BcBigNumber res = arg.length();
        return res;
    }
}
