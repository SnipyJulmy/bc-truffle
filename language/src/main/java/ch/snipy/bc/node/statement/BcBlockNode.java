package ch.snipy.bc.node.statement;

import ch.snipy.bc.node.BcStatementNode;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Node representing a list of statement
 */
@NodeInfo(shortName = "block", description = "node implementing a block, i.e. a list of statements")
public final class BcBlockNode extends BcStatementNode {
    @Children private final BcStatementNode[] nodes;

    public BcBlockNode(BcStatementNode[] nodes) {
        this.nodes = nodes;
    }

    @Override
    @ExplodeLoop
    public void executeVoid(VirtualFrame frame) {
        CompilerAsserts.compilationConstant(nodes.length);
        for (BcStatementNode statement : nodes) {
            statement.executeVoid(frame);
        }
    }

    // return immutable list of statement
    public List<BcStatementNode> statements() {
        return Collections.unmodifiableList(Arrays.asList(nodes));
    }
}
