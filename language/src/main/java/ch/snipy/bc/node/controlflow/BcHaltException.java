package ch.snipy.bc.node.controlflow;

import com.oracle.truffle.api.nodes.ControlFlowException;

public final class BcHaltException extends ControlFlowException {
    public static final BcHaltException SINGLETON = new BcHaltException();

    private BcHaltException() {
    }
}
