package ch.snipy.bc.node.interop;

import ch.snipy.bc.node.BcTypes;
import ch.snipy.bc.runtime.BcContext;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.nodes.Node;

import static ch.snipy.bc.runtime.BcBigNumber.*;

@SuppressWarnings("WeakerAccess")
@TypeSystemReference(BcTypes.class)
public abstract class BcForeignToBcTypeNode extends Node {

    @Child private Node isBoxed;
    @Child private Node unbox;

    @Specialization
    protected static Object fromNumber(Number value) {
        return BcContext.fromForeignValue(value);
    }

    @Specialization
    protected static Object fromString(String value) {
        return value;
    }

    @Specialization
    protected static Object fromBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Specialization
    protected static Object fromChar(char value) {
        return String.valueOf(value);
    }

    public static BcForeignToBcTypeNode create() {
        return BcForeignToBcTypeNodeGen.create();
    }

    public abstract Object executeConvert(Object value);

    @Specialization(guards = "isBoxedPrimitive(value)")
    public Object unbox(TruffleObject value) {
        Object unboxed = doUnbox(value);
        return BcContext.fromForeignValue(unboxed);
    }

    @Specialization(guards = "!isBoxedPrimitive(value)")
    public Object fromTruffleObject(TruffleObject value) {
        return value;
    }

    protected final boolean isBoxedPrimitive(TruffleObject object) {
        if (isBoxed == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            isBoxed = insert(Message.IS_BOXED.createNode());
        }
        return ForeignAccess.sendIsBoxed(isBoxed, object);
    }

    protected final Object doUnbox(TruffleObject value) {
        if (unbox == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            unbox = insert(Message.UNBOX.createNode());
        }
        try {
            return ForeignAccess.sendUnbox(unbox, value);
        } catch (UnsupportedMessageException e) {
            return ZERO;
        }
    }
}
