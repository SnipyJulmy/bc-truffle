package ch.snipy.bc.node.expression;

import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.node.BcExpressionNode;
import ch.snipy.bc.runtime.BcContext;
import ch.snipy.bc.runtime.BcFunction;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.VirtualFrame;

import static com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

public final class BcFunctionLiteralNode extends BcExpressionNode {

    private final String identifier;

    @CompilationFinal
    private BcFunction cachedFunction;

    private final ContextReference<BcContext> reference;

    public BcFunctionLiteralNode(BcLanguage language, String identifier) {
        this.identifier = identifier;
        this.reference = language.getContextReference();
    }

    @Override
    public BcFunction executeGeneric(VirtualFrame frame) {
        if (cachedFunction == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            cachedFunction = reference.get().getFunctionRegistry().lookup(identifier, true);
        }
        return cachedFunction;
    }

    public String getIdentifier() {
        return identifier;
    }
}
