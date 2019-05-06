package ch.snipy.bc

import org.graalvm.polyglot.{Context, Source}

object Main extends App {
  val ackermann =
    """
      |nanotime()
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
      |nanotime()
    """.stripMargin

  val bigNumber =
    """
       nanotime()
       y = 1
       for(i=1;i<100000;i++)
         y *= i
       nanotime()
       halt
    """.stripMargin

  val arrayTest2 =
    """
       for(i=0;i<10;i++)
         a[i] = i

       for(i=9;i>=0;i--)
         a[i]
    """.stripMargin

  val gcd =
    """
      |define g(m, n) {
      |	auto t
      |
      |	while (n != 0) {
      |		t = m
      |		m = n
      |		n = t % n
      |	}
      |	return (m)
      |}
      |
      |for(i=0;i<1000;i++)
      |  for(j=0;j<1000;j++)
      |    if(i != j)
      |      print "gcd(" + i + "," + j + ") = " + g(i,j)
      |
    """.stripMargin

  val linCongGen =
    """
      |randseed = 1
      |define rand() {
      |	randseed = (randseed * 1103515245 + 12345) % 2147483648
      |	return randseed
      |}
      |rand(); rand(); rand();
      |rand(); rand(); rand();
      |rand(); rand(); rand();
      |rand(); rand(); rand();
      |rand(); rand(); rand();
      |rand(); rand(); rand();
    """.stripMargin

  val primalityTest =
    """
      define p(n) {
          auto i

          if (n < 2) return(0)
          if (n == 2) return(1)
          if (n % 2 == 0) return(0)
          for (i = 3; i * i <= n; i += 2) {
              if (n % i == 0) return(0)
          }
          return(1)
      }
      for(j=1;j<100000;j++) {
        if(p(j)) a = j
      }
      halt
    """.stripMargin

  val lunhTest =
    """
       define l(n) {
           auto m, o, s, x

           o = scale
           scale = 0

           m = 1
           while (n > 0) {
               x = (n % 10) * m
               if (x > 9) x -= 9
               s += x
               m = 3 - m
               n /= 10
           }

           s %= 10
           scale = o
           if (s) return(0)
           return(1)
       }

       l(49927398716)
       l(49927398717)
       l(1234567812345678)
       l(1234567812345670)
    """

  val defTest =
    """
       define t(n) {
         auto m,n
         m = 2
         n = 4
         m += n
         return m
       }
       t(3)
    """.stripMargin

  val pi =
    """
      |scaleinc=20
      |
      |define zeropad (n) {
      |    auto m, scaleinc
      |    scaleinc = 20
      |    for ( m= scaleinc - 1; m > 0; --m ) {
      |        if ( n < 10^m ) {
      |            print "0"
      |        }
      |    }
      |    return ( n )
      |}
      |
      |wantscale= scaleinc - 2
      |scale= wantscale + 2
      |oldpi= 4*a(1)
      |scale= wantscale
      |oldpi= oldpi / 1
      |oldpi
      |while( 1 ) {
      |    wantscale= wantscale + scaleinc
      |    scale= wantscale + 2
      |    pi= 4*a(1)
      |    scale= 0
      |    digits= ((pi - oldpi) * 10^wantscale) / 1
      |    zeropad( digits)
      |    scale= wantscale
      |    oldpi= pi / 1
      |}
    """
      .stripMargin

  val printTest =
    """
       print "a"
       print "b"
       print "asd","\n"
       print "a"
    """


  val root =
    """
      |define r(a, n, d) {
      |    auto e, o, x, y, z
      |
      |    if (n == 0) return(1)
      |    if (a == 0) return(0)
      |
      |    o = scale
      |    scale = d
      |    e = 1 / 10 ^ d
      |
      |    if (n < 0) {
      |        n = -n
      |        a = 1 / a
      |    }
      |
      |    x = 1
      |    while (1) {
      |        y = ((n - 1) * x + a / x ^ (n - 1)) / n
      |        z = x - y
      |        if (z < 0) z = -z
      |        if (z < e) break
      |        x = y
      |    }
      |    scale = o
      |    return(y)
      |}
      |
      |r(9,2,10)
    """.stripMargin

  val dotProduct =
    """
      |define d(a[], b[], n) {
      |    auto d, i
      |
      |    for (i = 0; i < n; i++) {
      |        d += a[i] * b[i]
      |    }
      |    return(d)
      |}
      |
      |a[0] = 1
      |a[1] = 3
      |a[2] = -5
      |b[0] = 4
      |b[1] = -2
      |b[2] = -1
      |d(a[], b[], 3)
    """.stripMargin

  val arrayTest =
    """
       a[0] = 0
       a[1] = 1
       a[2] = 2
       a[3] = 3
       define get(b[],idx) {
         return b[idx]
       }
       a[0]
       a[1]
       a[2]
       a[3]
       get(a[],0)
       get(a[],1)
       get(a[],2)
       get(a[],3)
    """.stripMargin

  val source: Source = Source.newBuilder("bc", dotProduct, "scope")
    .build()

  val context = Context.newBuilder("bc")
    .in(System.in)
    .out(System.out)
    .build()

  val result = context.eval(source)
  println(result)
}
