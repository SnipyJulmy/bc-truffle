package ch.snipy.bc.node;

import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.runtime.BcUndefinedNameException;
import com.oracle.truffle.api.frame.VirtualFrame;

public class BcUnderfinedFunctionRootNode extends BcRootNode {
    public BcUnderfinedFunctionRootNode(BcLanguage language, String name) {
        super(language, null, null, name);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        throw BcUndefinedNameException.undefinedFunction(null, getName());
    }
}
