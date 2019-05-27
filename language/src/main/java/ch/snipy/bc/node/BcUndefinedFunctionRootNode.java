package ch.snipy.bc.node;

import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.runtime.BcUndefinedNameException;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * Initial {@link com.oracle.truffle.api.nodes.RootNode} of {@link ch.snipy.bc.runtime.BcFunction}
 * When they are created.
 */
public class BcUndefinedFunctionRootNode extends BcRootNode {
    public BcUndefinedFunctionRootNode(BcLanguage language, String name) {
        super(language, null, null, name);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        throw BcUndefinedNameException.undefinedFunction(null, getName());
    }
}
