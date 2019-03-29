package ch.snipy.bc.node;

import com.oracle.truffle.api.dsl.TypeSystem;

import java.math.BigDecimal;

@TypeSystem({
        BigDecimal.class,   // standard posix bc number
        String.class,       // support for string
        Object[].class      // only used to pass arguments for function call
})
public abstract class BcTypes {

}
