import org.graalvm.polyglot.{Context, Source}
import org.scalatest.{FlatSpec, Matchers}

class BcExpressionTest extends FlatSpec with Matchers {

  private val context: Context = Context.newBuilder("bc")
    .in(System.in)
    .out(System.out)
    .build()

  private def mkSource(source: String, name: String = "???"): Source =
    Source.newBuilder("bc", source, name).build()

  private def assertDouble(source: String)(res: Double): org.scalatest.Assertion =
    context.eval(mkSource(source)).asDouble().shouldBe(res)

  behavior of "bc language"

  it should "correctly parse and evaluate double literals" in {
    context.eval(mkSource("1.0")).asDouble() shouldBe 1.0
    context.eval(mkSource("2.0")).asDouble() shouldBe 2.0
    context.eval(mkSource("3.0")).asDouble() shouldBe 3.0
    context.eval(mkSource("1.2")).asDouble() shouldBe 1.2
    context.eval(mkSource("100.232")).asDouble() shouldBe 100.232
    context.eval(mkSource("3.14")).asDouble() shouldBe 3.14
    context.eval(mkSource("0")).asDouble() shouldBe 0.0
    context.eval(mkSource("0.0")).asDouble() shouldBe 0.0
  }

  it should "correctly parse and evaluate term expression" in {
    context.eval(mkSource("1 + 1")).asDouble() shouldBe 2.0
    context.eval(mkSource("1 - 0")).asDouble() shouldBe 1.0
    context.eval(mkSource("0 - 1")).asDouble() shouldBe -1.0
    context.eval(mkSource("1 + 0")).asDouble() shouldBe 1.0
    context.eval(mkSource("0 + 1")).asDouble() shouldBe 1.0
    context.eval(mkSource("3 + 3 + 3 + 3")).asDouble() shouldBe 12.0
  }

  it should "correctly parse and evaluate factor expression" in {
    context.eval(mkSource("1 * 2")).asDouble() shouldBe 2.0
    context.eval(mkSource("3 * 2")).asDouble() shouldBe 6.0
    context.eval(mkSource("1 * -2")).asDouble() shouldBe -2.0
  }

  it should "correctly parse and evaluate unary operator expression" in {
    context.eval(mkSource("-23")).asDouble() shouldBe -23.0
    context.eval(mkSource("+23")).asDouble() shouldBe 23.0
    context.eval(mkSource("+0")).asDouble() shouldBe 0.0
    context.eval(mkSource("-0")).asDouble() shouldBe 0.0
  }

  it should "correctly parse and evaluate power expression " in {

  }

  it should "correctly parse and evaluate post and pre increment/decrement" in {

  }

}
