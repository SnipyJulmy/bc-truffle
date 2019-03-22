package ch.snipy.bc

import org.graalvm.polyglot.{Context, Source}

object Main extends App {
  val program =
    """
       a + b
    """.stripMargin


  val source: Source = Source.newBuilder(BcLanguage.ID, program, "program")
    .build()
  val context = Context.newBuilder(BcLanguage.ID)
    .in(System.in)
    .out(System.out)
    .build()

  val result = context.eval(source)
  println(result)
}
