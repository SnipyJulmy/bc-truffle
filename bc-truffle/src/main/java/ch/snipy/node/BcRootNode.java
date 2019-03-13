package ch.snipy.node;


import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;

@NodeInfo(language = "bc", description = "root node for all BC execution tree")
public class BcRootNode extends RootNode {

    // the statement to execute, body is either :
    // - a list block statement, which is a list of statement
    // - a function body which is also a list of statement
    @Child private BcStatementNode body;

    protected BcRootNode(TruffleLanguage<?> language) {
        super(language);
    }

    protected BcRootNode(TruffleLanguage<?> language, FrameDescriptor frameDescriptor) {
        super(language, frameDescriptor);
    }

    public Object execute(VirtualFrame frame) {
        return null;
    }
}
