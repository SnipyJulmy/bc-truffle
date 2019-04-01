import org.graalvm.polyglot.{Context, Source}
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.scalacheck.Checkers

trait BcTestSpec extends FlatSpec with Checkers with Matchers {

  protected val context: Context = Context.newBuilder("bc")
    .in(System.in)
    .out(System.out)
    .build()

  protected def mkSource(source: String, name: String = "???"): Source =
    Source.newBuilder("bc", source, name).build()

  protected def B(value : Double) : BigDecimal = BigDecimal(value)
  protected def B(value : Int) : BigDecimal = BigDecimal(value)
}
