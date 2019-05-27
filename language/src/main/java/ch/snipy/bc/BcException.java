package ch.snipy.bc;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

// TODO : improve the generated exception
public class BcException extends RuntimeException implements TruffleException {

    private static final long serial = 31415L;

    private final Node location;

    @TruffleBoundary
    public BcException(String msg, Node location) {
        super(msg);
        this.location = location;
    }

    public static BcException typeError(Node op, Object... values) {
        StringBuilder builder = new StringBuilder();
        builder.append("type error : ");
        builder.append(op.getClass().toString());
        builder.append(" ");
        for (Object value : values)
            builder.append(value).append(" ");
        return new BcException(builder.toString(), op);
    }

    @SuppressWarnings("sync-override")
    @Override
    public final Throwable fillInStackTrace() {
        return this;
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
