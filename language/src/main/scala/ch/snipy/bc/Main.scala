package ch.snipy.bc

import org.graalvm.polyglot.{Context, Source}

object Main extends App {
  val program =
    """
       define id(x) {
         return x
       }
       id(1)
       define id(x) {
         return x * 2
       }
       id(1)
       define void id(x) {
         print(x*3)
       }
       id(2)
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
