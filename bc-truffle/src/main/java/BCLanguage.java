import com.oracle.truffle.api.TruffleLanguage;
import runtime.BCContext;

public class BCLanguage extends TruffleLanguage<BCContext> {

    public static final String ID = "bc";
    public static final String MIME_TYPE = "application/x-bc";

    protected BCContext createContext(Env env) {
        // TODO
        return null;
    }

    protected boolean isObjectOfLanguage(Object object) {
        // TODO
        return false;
    }
}
