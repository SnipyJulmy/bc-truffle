package ch.snipy.bc.node;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;

@NodeInfo(language = "bc", description = "root node for all BC execution tree")
public class BcRootNode extends RootNode {

    // TODO : for the root node, a list of statement for bc ???
    // the statement to execute, body is either :
    // - a list block statement, which is a list of statement
    // - a function body which is also a list of statement
    @Child private BcStatementNode bodyNode;

    private final String name;

    public BcRootNode(TruffleLanguage<?> language, FrameDescriptor frameDescriptor, BcStatementNode bodyNode, String name) {
        super(language, frameDescriptor);
        this.bodyNode = bodyNode;
        this.name = name;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        bodyNode.executeVoid(frame);
        return true; // fixme what to return when we just have statement ???
    }

}
