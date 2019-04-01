package ch.snipy.bc.node;

import ch.snipy.bc.runtime.BcBigNumber;
import com.oracle.truffle.api.dsl.TypeSystem;

import java.math.BigDecimal;

@TypeSystem({
        BcBigNumber.class,   // standard posix bc number
        String.class,       // support for string
        Object[].class      // only used to pass arguments for function call
})
public abstract class BcTypes {

}
