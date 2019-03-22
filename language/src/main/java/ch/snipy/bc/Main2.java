package ch.snipy.bc;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;

public class Main2 {
    public static void main(String[] args) {
        String programm = "1 + 2";
        try {
            Source source = Source.newBuilder("bc", programm, "program").build();
            Context context = Context.newBuilder("bc")
                    .in(System.in)
                    .out(System.out)
                    .build();
            Value result = context.eval(source);
            System.out.println(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
