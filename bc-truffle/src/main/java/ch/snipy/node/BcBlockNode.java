package ch.snipy.node;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NodeInfo(shortName = "block", description = "sequence of statement")
public abstract class BcBlockNode extends BcStatementNode {

    @Children private final BcStatementNode[] nodes;

    public BcBlockNode(BcStatementNode[] nodes) {
        this.nodes = nodes;
    }

    @Override
    @ExplodeLoop
    public void executeVoid(VirtualFrame frame) {
        for (BcStatementNode node : nodes) {
            node.executeVoid(frame);
        }
    }

    public List<BcStatementNode> statements() {
        // return immutable list
        return Collections.unmodifiableList(Arrays.asList(nodes));
    }
}
