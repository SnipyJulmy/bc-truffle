package ch.snipy;

import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

// TODO
public class BcException extends RuntimeException implements TruffleException {

    // TODO
    public static BcException typeError(Node op, Object... values) {
        return new BcException();
    }

    @Override
    public Node getLocation() {
        return null;
    }

    @Override
    public Object getExceptionObject() {
        return null;
    }

    @Override
    public boolean isSyntaxError() {
        return false;
    }

    @Override
    public boolean isIncompleteSource() {
        return false;
    }

    @Override
    public boolean isInternalError() {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isTimeout() {
        return false;
    }

    @Override
    public boolean isExit() {
        return false;
    }

    @Override
    public int getExitStatus() {
        return 0;
    }

    @Override
    public int getStackTraceElementLimit() {
        return 0;
    }

    @Override
    public SourceSection getSourceLocation() {
        return null;
    }
}
