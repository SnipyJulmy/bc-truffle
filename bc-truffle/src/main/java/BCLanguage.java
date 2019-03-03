import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.RootNode;
import runtime.BCContext;

public class BCLanguage extends TruffleLanguage<BCContext> {

    public static final String ID = "bc";
    public static final String MIME_TYPE = "application/x-bc";

    protected BCContext createContext(Env env) {
        return new BCContext();
    }

    protected boolean isObjectOfLanguage(Object object) {
        // TODO
        return false;
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        RootNode root = BCParser$.MODULE$.parse(
                this,
                request.getSource()
        );
        return Truffle.getRuntime().createCallTarget(root);
    }
}
