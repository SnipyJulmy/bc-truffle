package node;

import com.oracle.truffle.api.dsl.TypeSystem;

@TypeSystem({
        double.class,  // bc only work with multi-precision value
        String.class,  // support for string
        Object[].class // only used to pass arguments for function call
        // TODO : consider BigDouble, BigInteger, Long (when scale is 0 !)
})
public abstract class BcTypes {

}
