package ch.snipy.node.controlflow;

import com.oracle.truffle.api.nodes.ControlFlowException;

public final class BcContinueException extends ControlFlowException {
    public static final BcContinueException SINGLETON = new BcContinueException();
    private BcContinueException() {
    }
}
