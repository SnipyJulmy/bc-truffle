package ch.snipy.bc.runtime;

import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.builtins.BcBuiltinNode;
import ch.snipy.bc.builtins.BcNanoBuiltinFactory;
import ch.snipy.bc.builtins.BcPrintlnBuiltinFactory;
import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.node.BcRootNode;
import ch.snipy.bc.node.local.BcReadArgumentNode;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.util.List;

public final class BCContext {
    private static final int DEFAULT_SCALE = 0;

    private final BufferedReader input;
    private final PrintWriter output;
    private final Env env;
    private final TruffleLanguage language;
    private final BcFunctionRegistry functionRegistry;

    private int scale = DEFAULT_SCALE;
    private RoundingMode roundingMode = RoundingMode.FLOOR;

    public BCContext(BcLanguage language,
                     Env env,
                     List<NodeFactory<? extends BcBuiltinNode>> builtins) { // TODO : builtins
        this.env = env;
        this.input = new BufferedReader(new InputStreamReader(env.in()));
        this.output = new PrintWriter(env.out(), true);
        this.language = language;
        this.functionRegistry = new BcFunctionRegistry(language);
        installBuiltins();
    }

    public static Object fromForeignValue(Object o) {
        if (o instanceof BcBigNumber || o instanceof String) return o;
        else if (o instanceof Character) return String.valueOf(o);
        else if (o instanceof Number) return fromForeignNumber(o);
        else if (o instanceof TruffleObject) return o;
        else if (o instanceof BCContext) return o;
        CompilerDirectives.transferToInterpreter();
        throw new IllegalStateException(o + " is not a Truffle value");
    }

    @TruffleBoundary
    private static BcBigNumber fromForeignNumber(Object o) {
        return new BcBigNumber(
                ((Number) o).doubleValue()
        );
    }

    public static NodeInfo lookupNodeInfo(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        NodeInfo info = clazz.getAnnotation(NodeInfo.class);
        if (info != null) {
            return info;
        } else {
            return lookupNodeInfo(clazz.getSuperclass());
        }
    }

    private void installBuiltins() {
        installBuiltin(BcPrintlnBuiltinFactory.getInstance());
        installBuiltin(BcNanoBuiltinFactory.getInstance());
    }

    private void installBuiltin(NodeFactory<? extends BcBuiltinNode> factory) {
        int argsCount = factory.getExecutionSignature().size();
        BcExpressionNode[] args = new BcExpressionNode[argsCount];
        for (int i = 0; i < argsCount; i++) {
            args[i] = new BcReadArgumentNode(i);
        }
        BcBuiltinNode builtinNode = factory.createNode((Object) args);
        String name = lookupNodeInfo(builtinNode.getClass()).shortName();
        BcRootNode rootNode = new BcRootNode(language, new FrameDescriptor(), builtinNode, name);
        functionRegistry.register(name, Truffle.getRuntime().createCallTarget(rootNode));
    }

    public BufferedReader getInput() {
        return input;
    }

    public PrintWriter getOutput() {
        return output;
    }

    public BcFunctionRegistry getFunctionRegistry() {
        return functionRegistry;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public RoundingMode getRoundingMode() {
        return this.roundingMode;
    }
}
