package ch.snipy.bc;

import ch.snipy.bc.runtime.BCContext;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

@TruffleLanguage.Registration(
        id = "bc",
        name = "bc",
        version = "0.1",
        mimeType = "application/x-bc"
)
public class BcLanguage extends TruffleLanguage<BCContext> {

    @Override
    protected BCContext createContext(Env env) {
        return new BCContext();
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return false;
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        Source source = request.getSource();
        RootNode root = BCParser$.MODULE$.parseAdd(this,source);
        return Truffle.getRuntime().createCallTarget(root);
    }
}
