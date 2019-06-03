package ch.snipy.bc;

import ch.snipy.bc.node.BcRootNode;
import ch.snipy.bc.runtime.BcBigNumber;
import ch.snipy.bc.runtime.BcContext;
import ch.snipy.bc.runtime.BcNull;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.source.Source;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

@TruffleLanguage.Registration(
        id = "bc",
        name = "bc",
        version = "0.1",
        characterMimeTypes = "application/x-bc",
        contextPolicy = TruffleLanguage.ContextPolicy.SHARED
)
public class BcLanguage extends TruffleLanguage<BcContext> {

    public static IBcParser parser;

    public static BcContext getCurrentContext() {
        return getCurrentContext(BcLanguage.class);
    }

    @Override
    protected BcContext createContext(Env env) {
        // if there exists any external builtin, we need to add them here
        return new BcContext(this, env, Collections.synchronizedList(new ArrayList<>()));
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        if (!(object instanceof TruffleObject))
            return false;
        return object instanceof BcBigNumber || object instanceof BcNull;
    }

    @Override
    protected CallTarget parse(ParsingRequest request) {
        Source source = request.getSource();

        try {
            Class initClass = Class.forName("ch.snipy.bc.Init");
            Method initMethod = initClass.getMethod("init");
            initMethod.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        BcRootNode root = parser.parse(this, source);
        return Truffle.getRuntime().createCallTarget(root);
    }
}
