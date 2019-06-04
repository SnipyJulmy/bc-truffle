package ch.snipy.bc

import org.graalvm.polyglot.{Context, Source}

object Main extends App {
  val perfTest =
    """
      |l = 5000
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

  val source: Source = Source.newBuilder("bc", perfTest, "scope")
    .build()

  val context = Context.newBuilder("bc")
    .in(System.in)
    .out(System.out)
    .build()

  val result = context.eval(source)
  println(result)
}
