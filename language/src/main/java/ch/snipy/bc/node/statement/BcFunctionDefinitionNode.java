package ch.snipy.bc.node.statement;

import ch.snipy.bc.node.BcStatementNode;
import ch.snipy.bc.runtime.BCContext;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

public class BcFunctionDefinitionNode extends BcStatementNode {

    private final String identifier;
    private final RootCallTarget callTarget;
    private final BCContext context;

    public BcFunctionDefinitionNode(String identifier, RootCallTarget callTarget, BCContext context) {
        this.identifier = identifier;
        this.callTarget = callTarget;
        this.context = context;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        // update the function registry
        context.getFunctionRegistry().register(identifier, callTarget);
    }
}
