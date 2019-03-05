package node;

import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(language = "BC", description = "abstract base node for any bc statement")
@ReportPolymorphism
public abstract class BcStatementNode extends Node {

}
