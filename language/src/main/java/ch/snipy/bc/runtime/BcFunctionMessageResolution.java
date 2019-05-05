package ch.snipy.bc.runtime;

import ch.snipy.bc.node.call.BcDispatchNode;
import ch.snipy.bc.node.call.BcDispatchNodeGen;
import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;

import java.util.Arrays;

@MessageResolution(receiverType = BcFunction.class)
public class BcFunctionMessageResolution extends Node {

    @Resolve(message = "EXECUTE")
    public abstract static class BcForeignFunctionExecuteNode extends Node {
        @Child private BcDispatchNode dispatchNode = BcDispatchNodeGen.create();

        public Object access(BcFunction function, Object[] args) {
            return dispatchNode.executeDispatch(
                    function,
                    Arrays.stream(args).map(BcContext::fromForeignValue).toArray()
            );
        }
    }

    @Resolve(message = "IS_EXECUTABLE")
    public abstract static class BcForeignFunctionIsExecutableNode extends Node {
        public Object access(Object o) {
            return o instanceof BcFunction;
        }
    }

    @CanResolve
    public abstract static class CheckFunction extends Node {
        protected static boolean test(TruffleObject object) {
            return object instanceof BcFunction;
        }
    }
}
