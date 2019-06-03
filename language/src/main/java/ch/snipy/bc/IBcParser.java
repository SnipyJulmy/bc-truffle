package ch.snipy.bc;

import ch.snipy.bc.node.BcRootNode;
import com.oracle.truffle.api.source.Source;

public interface IBcParser {
    BcRootNode parse(BcLanguage language, Source source);
}
