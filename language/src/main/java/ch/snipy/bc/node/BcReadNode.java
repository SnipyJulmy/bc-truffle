package ch.snipy.bc.node;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(
        shortName = "readnode",
        description = "abstract node to extends when need to access the global scope"
)
@NodeField(name = "globalFrame", type = MaterializedFrame.class)
public abstract class BcReadNode extends BcExpressionNode {
    protected abstract MaterializedFrame getGlobalFrame();
}
