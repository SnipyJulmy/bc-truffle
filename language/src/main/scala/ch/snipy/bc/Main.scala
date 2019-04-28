package ch.snipy.bc

import org.graalvm.polyglot.{Context, Source}

object Main extends App {
  val program =
    """
      |define ack(m, n) {
      |   if ( m == 0 ) return (n+1);
      |   if ( n == 0 ) return (ack(m-1, 1));
      |   return (ack(m-1, ack(m, n-1)));
      |}
      |
      |for (n=0; n<9; n++) {
      |  for (m=0; m<4; m++) {
      |    print("A(" + m + "," + n + ") = " + ack(m,n))
      |  }
      |}
    """.stripMargin

  val pi =
    """
      |
      |scaleinc= 20
      |
      |define zeropad (n) {
      |    auto m
      |    for ( m= scaleinc - 1; m > 0; --m ) {
      |        if ( n < 10^m ) {
      |            print "0"
      |        }
      |    }
      |    return ( n )
      |}
      |
      |zeropad(1)
      |
      |wantscale = scaleinc - 2
      |scale = wantscale + 2
      |oldpi = 4*a(1)
      |scale = wantscale
      |oldpi = oldpi / 1
      |oldpi
      |while( 1 ) {
      |    wantscale = wantscale + scaleinc
      |    scale = wantscale + 2
      |    pi= 4*a(1)
      |    scale= 0
      |    digits= ((pi - oldpi) * 10^wantscale) / 1
      |    zeropad( digits )
      |    scale= wantscale
      |    oldpi= pi / 1
      |}
    """.stripMargin

  val printEx =
    """
       define id(n) {
         return (n)
       }
       id(2)
       id(3)
    """.stripMargin

  val source: Source = Source.newBuilder("bc", pi, "pi")
    .build()

  val context = Context.newBuilder("bc")
    .in(System.in)
    .out(System.out)
    .build()

  val result = context.eval(source)
  println(result)
}
