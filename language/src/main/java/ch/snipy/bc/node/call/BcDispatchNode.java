package ch.snipy.bc.node.call;

import ch.snipy.bc.node.BcTypes;
import ch.snipy.bc.node.interop.BcForeignToBcTypeNode;
import ch.snipy.bc.node.interop.BcForeignToBcTypeNodeGen;
import ch.snipy.bc.runtime.BcFunction;
import ch.snipy.bc.runtime.BcUndefinedNameException;
import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;

@ReportPolymorphism
@TypeSystemReference(BcTypes.class)
public abstract class BcDispatchNode extends Node {

    public static final int INLINE_CACHE_SIZE = 2;

    public abstract Object executeDispatch(Object function, Object[] args);

    @Specialization(
            limit = "INLINE_CACHE_SIZE",
            guards = "function.getCallTarget() == cachedTarget",
            assumptions = "callTargetStable"
    )
    @SuppressWarnings("unused")
    protected static Object doDirect(BcFunction function, Object[] args,
                                     @Cached("function.getCallTargetStable()") Assumption callTargetStable,
                                     @Cached("function.getCallTarget()") RootCallTarget cachedTarget,
                                     @Cached("create(cachedTarget)") DirectCallNode callNode) {
        return callNode.call(args);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(BcFunction function, Object[] args,
                                       @Cached("create()") IndirectCallNode callNode) {
        return callNode.call(function.getCallTarget(), args);
    }



    @Fallback
    protected Object unknownFunction(Object function, @SuppressWarnings("unused") Object[] arguments) {
        throw BcUndefinedNameException.undefinedFunction(this, function);
    }

    @Specialization(guards = "isForeignFunction(function)")
    protected Object doForeign(TruffleObject function, Object[] arguments,
                               // The child node to call the foreign function
                               @Cached("createCrossLanguageCallNode()") Node crossLanguageCallNode,
                               // The child node to convert the result of the foreign call to a SL value
                               @Cached("createToSLTypeNode()") BcForeignToBcTypeNode toSLTypeNode) {

        try {
            /* Perform the foreign function call. */
            Object res = ForeignAccess.sendExecute(crossLanguageCallNode, function, arguments);
            /* Convert the result to a SL value. */
            return toSLTypeNode.executeConvert(res);

        } catch (ArityException | UnsupportedTypeException | UnsupportedMessageException e) {
            /* Foreign access was not successful. */
            throw BcUndefinedNameException.undefinedFunction(this, function);
        }
    }

    protected static boolean isForeignFunction(TruffleObject function) {
        return !(function instanceof BcFunction);
    }

    protected static Node createCrossLanguageCallNode() {
        return Message.EXECUTE.createNode();
    }

    protected static BcForeignToBcTypeNode createToSLTypeNode() {
        return BcForeignToBcTypeNodeGen.create();
    }
}
