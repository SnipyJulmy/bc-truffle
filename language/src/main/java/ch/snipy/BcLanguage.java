package ch.snipy;

import ch.snipy.runtime.BCContext;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

@TruffleLanguage.Registration(
        id = BcLanguage.ID,
        name = BcLanguage.NAME,
        version = BcLanguage.VERSION,
        mimeType = BcLanguage.MIME_TYPE
)
public class BcLanguage extends TruffleLanguage<BCContext> {

    public static final String ID = "bc";
    public static final String NAME = "bc";
    public static final String VERSION = "0.1";
    public static final String MIME_TYPE = "application/x-bc";

    @Override
    protected BCContext createContext(Env env) {
        return new BCContext();
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        Source source = request.getSource();
        RootNode root = BCParser$.MODULE$.parse(this, source);
        return Truffle.getRuntime().createCallTarget(root);
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        // TODO
        return false;
    }
}
