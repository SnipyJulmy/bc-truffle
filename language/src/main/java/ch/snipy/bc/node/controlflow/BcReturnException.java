package ch.snipy.bc.node.controlflow;

import com.oracle.truffle.api.nodes.ControlFlowException;

public final class BcReturnException extends ControlFlowException {

    private final Object result;

    public BcReturnException(Object result) {
        this.result = result;
    }

    public Object result() {
        return result;
    }
}
