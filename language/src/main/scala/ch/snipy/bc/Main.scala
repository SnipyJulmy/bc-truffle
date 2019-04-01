package ch.snipy.bc

import org.graalvm.polyglot.{Context, Source}

object Main extends App {
  val program = """(8866128975287528 ^ 3) + (-8778405442862239 ^ 3) + (-2736111468807040 ^ 3)""".stripMargin

  val source: Source = Source.newBuilder("bc", program, "program")
    .build()

  val context = Context.newBuilder("bc")
    .in(System.in)
    .out(System.out)
    .build()

  val result = context.eval(source)
  println(result)
}
