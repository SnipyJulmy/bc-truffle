package ch.snipy.bc.runtime;

import ch.snipy.bc.BcLanguage;
import com.oracle.truffle.api.RootCallTarget;

import java.util.*;

// map to store the functions during the AST generation and execution
public final class BcFunctionRegistry {

    private final BcLanguage language;
    private final FunctionsObject functionsObject = new FunctionsObject();

    public BcFunctionRegistry(BcLanguage language) {
        this.language = language;
    }

    public BcFunction lookup(String name, boolean createIfNotPresent) {
        BcFunction result = functionsObject.functions.get(name);
        if (result == null && createIfNotPresent) {
            result = new BcFunction(language, name);
            functionsObject.functions.put(name, result);
        }
        return result;
    }

    public boolean contains(String name) {
        return functionsObject.functions.containsKey(name);
    }

    public BcFunction register(String name, RootCallTarget callTarget) {
        BcFunction function = lookup(name, true);
        function.setCallTarget(callTarget);
        return function;
    }

    public void register(Map<String, RootCallTarget> functions) {
        functions.forEach(this::register);
    }

    public BcFunction lookup(String name) {
        return functionsObject.functions.get(name);
    }

    // sorted list of all functions
    public List<BcFunction> functions() {
        ArrayList<BcFunction> res = new ArrayList<>(functionsObject.functions.values());
        Collections.sort(res, new Comparator<BcFunction>() {
            @Override
            public int compare(BcFunction o1, BcFunction o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return res;
    }

    public FunctionsObject getFunctionsObject() {
        return functionsObject;
    }
}

