package ch.snipy.bc.node.local;

import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcUndefinedNameException;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "[]")
@NodeChild("identifierNode")
@NodeChild("indexNode")
public abstract class BcReadArrayNode extends BcExpressionNode {

    static final int LIBRARY_LIMIT = 3;

    @Specialization(guards = "arrays.hasArrayElements(identifier)", limit = "LIBRARY_LIMIT")
    protected Object readArray(Object identifier, Object index,
                               @CachedLibrary("identifier") InteropLibrary arrays,
                               @CachedLibrary("index") InteropLibrary numbers) {
        try {
            return arrays.readArrayElement(identifier, numbers.asLong(index));
        } catch (UnsupportedMessageException | InvalidArrayIndexException e) {
            throw BcUndefinedNameException.undefinedIndex(this, index);
        }
    }

}
