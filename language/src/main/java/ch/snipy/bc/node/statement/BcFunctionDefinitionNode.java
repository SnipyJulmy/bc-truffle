package ch.snipy.bc.node.statement;

import ch.snipy.bc.node.BcStatementNode;
import ch.snipy.bc.runtime.BcContext;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * Statement which indicate when a function is defined, so we can update it
 * in the function registry.
 */
public class BcFunctionDefinitionNode extends BcStatementNode {

    private final String identifier;
    private final RootCallTarget callTarget;
    private final BcContext context;
    private final boolean isVoid;

    public BcFunctionDefinitionNode(String identifier, RootCallTarget callTarget, BcContext context, boolean isVoid) {
        this.identifier = identifier;
        this.callTarget = callTarget;
        this.context = context;
        this.isVoid = isVoid;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        // update the function registry
        context.getFunctionRegistry().register(identifier, callTarget);
    }

    public boolean isVoid() {
        return isVoid;
    }
}
