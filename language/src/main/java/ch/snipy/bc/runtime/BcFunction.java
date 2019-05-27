package ch.snipy.bc.runtime;

import ch.snipy.bc.BcLanguage;
import ch.snipy.bc.node.BcUndefinedFunctionRootNode;
import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.utilities.CyclicAssumption;

// serve as a data structure for storing a function
public final class BcFunction implements TruffleObject {

    private final String name;
    private final CyclicAssumption callTargetStable;

    private RootCallTarget callTarget;

    protected BcFunction(BcLanguage language, String name) {
        this.name = name;
        this.callTarget = Truffle.getRuntime().createCallTarget(new BcUndefinedFunctionRootNode(language, name));
        this.callTargetStable = new CyclicAssumption(name);
    }

    public String getName() {
        return name;
    }

    public RootCallTarget getCallTarget() {
        return callTarget;
    }

    public Assumption getCallTargetStable() {
        return callTargetStable.getAssumption();
    }

    protected void setCallTarget(RootCallTarget callTarget) {
        this.callTarget = callTarget;
        callTargetStable.invalidate();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public ForeignAccess getForeignAccess() {
        return BcFunctionMessageResolutionForeign.ACCESS;
    }
}
