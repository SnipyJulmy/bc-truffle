package ch.snipy.bc.node.local;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcUndefinedNameException;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "[]=")
@NodeChild("identifierNode")
@NodeChild("indexNode")
@NodeChild("valueNode")
public abstract class BcWriteArrayNode extends BcExpressionNode {

    static final int LIBRARY_LIMIT = 3;

    @Specialization(guards = "arrays.hasArrayElements(identifier)", limit = "LIBRARY_LIMIT")
    protected Object readArray(Object identifier, Object index, Object value,
                               @CachedLibrary("identifier") InteropLibrary arrays,
                               @CachedLibrary("index") InteropLibrary numbers) {
        try {
            arrays.writeArrayElement(identifier, numbers.asLong(index), value);
        } catch (UnsupportedMessageException | UnsupportedTypeException | InvalidArrayIndexException e) {
            throw BcUndefinedNameException.undefinedIndex(this, index);
        }
        return value;
    }
}
