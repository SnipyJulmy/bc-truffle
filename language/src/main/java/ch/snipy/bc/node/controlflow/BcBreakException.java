package ch.snipy.bc.node.controlflow;

import com.oracle.truffle.api.nodes.ControlFlowException;

public final class BcBreakException extends ControlFlowException {
    public static final BcBreakException SINGLETON = new BcBreakException();

    private BcBreakException() {
    }
}
