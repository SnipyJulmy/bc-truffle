package ch.snipy.bc.runtime;

import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.builtins.BcPrintBuiltin;
import ch.snipy.bc.builtins.BcPrintBuiltinFactory;
import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.BcRootNode;
import ch.snipy.bc.node.local.BcReadArgumentNode;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.FrameDescriptor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

public final class BCContext {
    private final BufferedReader input;
    private final PrintWriter output;
    private final Env env;
    private final TruffleLanguage language;
    // private final BcFunctionRegistry; TODO

    public BCContext(BcLanguage language,
                     Env env,
                     List<NodeFactory<? extends BcBuiltinNode>> builtins) { // TODO : builtins
        this.env = env;
        this.input = new BufferedReader(new InputStreamReader(env.in()));
        this.output = new PrintWriter(env.out(), true);
        this.language = language;
        installBuiltins();
    }

    private void installBuiltins() {
        installBuiltin(BcPrintBuiltinFactory.getInstance());
    }

    private void installBuiltin(NodeFactory<? extends BcBuiltinNode> factory) {
        int argsCount = factory.getExecutionSignature().size();
        BcExpressionNode[] args = new BcExpressionNode[argsCount];
        for (int i = 0; i < argsCount; i++) {
            args[i] = new BcReadArgumentNode(i);
        }
        BcBuiltinNode builtinNode = factory.createNode((Object) args);
        BcRootNode rootNode = new BcRootNode(language, new FrameDescriptor(),builtinNode);
        // todo register the builtin inside the registry
    }

    public BufferedReader getInput() {
        return input;
    }

    public PrintWriter getOutput() {
        return output;
    }
}
