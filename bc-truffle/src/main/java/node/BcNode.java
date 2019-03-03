package node;


import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public class BcNode extends RootNode {

    protected BcNode(TruffleLanguage<?> language) {
        super(language);
    }

    protected BcNode(TruffleLanguage<?> language, FrameDescriptor frameDescriptor) {
        super(language, frameDescriptor);
    }

    public Object execute(VirtualFrame frame) {
        return null;
    }
}
