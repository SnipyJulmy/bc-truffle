package ch.snipy.bc

import org.graalvm.polyglot.{Context, Source}

object Main extends App {
  val program =
    """
      | 33 ^ 3
      | 123
      | print(4)
      | 45 * 43 == (44 ^ 2) - (1 ^ 2)
      | 8 * 8 * 8 == 8 ^ 3
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
