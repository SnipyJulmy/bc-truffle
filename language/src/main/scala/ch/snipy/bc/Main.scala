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
      |    print("A(" + m + "," + n + ") = " + ack(m,n) + "\n")
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

  val primes = Array(21383267l, 21382135391l,2139857l,7139777l)

  val primalityTest =
    s"""
      define p(n) {
          auto i

          if (n < 2) return(0)
          if (n == 2) return(1)
          if (n % 2 == 0) return(0)
          for (i = 3; i <= n; i += 2) {
              if (n % i == 0) return(0)
          }
          return(1)
      }

      nanotime()
      while(1) {
          a = p(${primes(3)})
          nanotime()
      }

      for(j=1;j<1000;j++) {
        if(p(j)) j
      }
      halt
    """.stripMargin

  val millerRabin =
    s"""
      |seed = 1   /* seed of the random number generator */
      |scale = 0
      |
      |/* Random number from 0 to 32767. */
      |define rand() {
      |  /* Cheap formula (from POSIX) for random numbers of low quality. */
      |  seed = (seed * 1103515245 + 12345) % 4294967296
      |  return ((seed / 65536) % 32768)
      |}
      |
      |/* Random number in range [from, to]. */
      |define rangerand(from, to) {
      |  auto b, h, i, m, n, r
      |
      |  m = to - from + 1
      |  h = length(m) / 2 + 1  /* want h iterations of rand() % 100 */
      |  b = 100 ^ h % m        /* want n >= b */
      |  while (1) {
      |    n = 0                /* pick n in range [b, 100 ^ h) */
      |    for (i = h; i > 0; i--) {
      |      r = rand()
      |      while (r < 68) {
      |        r = rand();
      |      }
      |      n = (n * 100) + (r % 100)       /* append 2 digits to n */
      |    }
      |    if (n >= b) { break; }  /* break unless the modulo bias */
      |  }
      |  return (from + (n % m))
      |}
      |
      |/* n is probably prime? */
      |define millerrabintest(n, k) {
      |  auto d, r, a, x, s
      |  if (n <= 3) { return (1); }
      |  if ((n % 2) == 0) { return (0); }
      |
      |  /* find s and d so that d * 2^s = n - 1 */
      |  d = n - 1
      |  s = 0
      |  while((d % 2) == 0) {
      |     d /= 2
      |     s += 1
      |  }
      |
      |  while (k-- > 0) {
      |    a = rangerand(2, n - 2)
      |    x = (a ^ d) % n
      |    if (x != 1) {
      |      for (r = 0; r < s; r++) {
      |        if (x == (n - 1)) { break; }
      |        x = (x * x) % n
      |      }
      |      if (x != (n - 1)) {
      |        return (0)
      |      }
      |    }
      |  }
      |  return (1)
      |}
      |
      |nanotime()
      |while(1) {
      |  if(millerrabintest(102667,10)) a = 1
      |  nanotime()
      |}
      |
      |halt
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

  val commentTest =
    """
    /* Hello World ! */
    print "Hello World !\n"

    a = \
    3

    b \
    = \
    2
    b
  """.stripMargin

  val longTime =
    """
      |define longtimefunction(n) {
      |  auto a,res,max
      |  res = 0
      |  max = 10 ^ n
      |  for(a = 0; a < max; a++) res += a
      |  return res
      |}
      |longtimefunction(9)
      |halt
    """.stripMargin

  val scopeTest =
    """
       define f(a,b) {
         auto n
         n = 10
         return n
       }

       define g() {
         return n
       }

       f() == 10

       define f() {
         return n
       }

       f() == 0
       g() == 0
       n == 0
    """.stripMargin

  val langton =
    """
      |define o(w,h,a[]) {
      |    auto i, j
      |
      |    "P1 "
      |    w
      |    h
      |    for (j = 0; j < h; j++) {
      |        for (i = 0; i < w; i++) {
      |            print (a[j * w + i] + " ")
      |        }
      |        print "\n"
      |    }
      |}
      |
      |define l(w, h, x, y) {
      |    auto a[], d, i, x[], y[]
      |
      |    /* d represents one of the four possible directions:
      |     *             0
      |     *             ⇑
      |     *           3⇐ ⇒1
      |     *             ⇓
      |     *             2
      |     * The arrays x[] and y[] contain the changes to the x and y direction for
      |     * each value of d.
      |     */
      |    x[1] = 1
      |    x[3] = -1
      |    y[0] = -1
      |    y[2] = 1
      |
      |    while (1) {
      |        i = y * w + x
      |        if (a[i] == 0) d += 1   /* turn right if white */
      |        if (a[i] == 1) d -= 1   /* turn left if black */
      |        if (d < 0) d = 3
      |        if (d > 3) d = 0
      |        x += x[d]
      |        y += y[d]
      |        a[i] = 1 - a[i]         /* toggle cell colour */
      |        if (x < 0) break
      |        if (x == w) break
      |        if (y < 0) break
      |        if (y == h) break
      |    }
      |
      |    o(w,h,a[])
      |}
      |
      |l(100, 100, 50, 50)
    """.stripMargin

  val perfTest =
    """
      |l = 50000
      |seed = 3144421321
      |
      |define rand() {
      |  /* Cheap formula (from POSIX) for random numbers of low quality. */
      |  seed = (seed * 1103515245 + 12345) % 4294967296
      |  return ((seed / 65536) % 32768)
      |}
      |
      |define g(r) {
      |  if(r % 2 == 0) return r ^ 31
      |  if(r % 3 == 0) return r * 321213 - 3213 * 12 - 213 + 999317183
      |  if(r % 5 == 0) return r ^ 3 ^ 3 ^ 3
      |  if(r % 7 == 0) return -r * 123213 - 98776 + 231 ^ 45
      |  return r
      |}
      |
      |define void shellsort(t[],n) {
      |  auto j,k,v,idx,random
      |
      |  random = g(rand())
      |
      |  for(k=1;k<n;k=1+3*k){}
      |  for(k=k/3;k>0;k=k/3) {
      |    for(i=k;i<n;i++) {
      |      v = t[i]
      |      j = i
      |      while(j > k-1 && t[j-k] > v) {
      |        t[j] = t[j-k]
      |        j = j-k
      |      }
      |      t[j] = v
      |    }
      |  }
      |}
      |
      |nanotime()
      |while(1) {
      |  for(i=0;i<l;i++) {
      |    a[i] = rand()
      |  }
      |  shellsort(a[],l)
      |  nanotime()
      |}
      |
      |halt
    """.stripMargin

  val source: Source = Source.newBuilder("bc", langton, "scope")
    .build()

  val context = Context.newBuilder("bc")
    .in(System.in)
    .out(System.out)
    .build()

  val result = context.eval(source)
  println(result)
}
