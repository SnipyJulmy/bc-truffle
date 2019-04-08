package ch.snipy.bc

import org.graalvm.polyglot.{Context, Source}

object Main extends App {
  val program =
    """
       a = 20 ; while(--a > 0) print(a)
       a = 20 ; while(a-- > 0) print(a)
    """.stripMargin

  val source: Source = Source.newBuilder("bc", program, "program")
    .build()

  val context = Context.newBuilder("bc")
    .in(System.in)
    .out(System.out)
    .build()

  val result = context.eval(source)
  println(result)
}
