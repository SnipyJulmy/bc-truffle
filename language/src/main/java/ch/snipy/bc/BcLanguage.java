package ch.snipy.bc;

import ch.snipy.bc.runtime.BCContext;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

import java.util.ArrayList;
import java.util.Collections;

@TruffleLanguage.Registration(
        id = "bc",
        name = "bc",
        version = "0.1",
        characterMimeTypes = "application/x-bc"
)
public class BcLanguage extends TruffleLanguage<BCContext> {

    public static BCContext getCurrentContext() {
        return getCurrentContext(BcLanguage.class);
    }

    @Override
    protected BCContext createContext(Env env) {
        return new BCContext(this, env, Collections.synchronizedList(new ArrayList<>()));
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return false;
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        Source source = request.getSource();
        RootNode root = BCParser$.MODULE$.parseExpr(this, source);
        return Truffle.getRuntime().createCallTarget(root);
    }
}
