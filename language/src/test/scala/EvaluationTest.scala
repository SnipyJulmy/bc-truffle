class EvaluationTest extends BcTestSpec {

  behavior of "bc parser and evaluator"

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
    context.eval(mkSource("56 * 72 + 1")).asDouble() shouldBe (56 * 72 + 1).toDouble
    context.eval(mkSource("3 * 4 * 5 * 6 * 7")).asDouble() shouldBe (3 * 4 * 5 * 6 * 7).toDouble
    context.eval(mkSource("12.3 * -56.7")).asDouble() shouldBe (12.3 * -56.7)
    context.eval(mkSource("3 + 4 * 6 + 1 * 45 * 3 * -9 + 1 - 123 * 21")).asDouble() shouldBe 3 + 4 * 6 + 1 * 45 * 3 * -9 + 1 - 123 * 21
  }

  it should "correctly parse and evaluate unary operator expression" in {
    context.eval(mkSource("-23")).asDouble() shouldBe -23.0
    context.eval(mkSource("+23")).asDouble() shouldBe 23.0
    context.eval(mkSource("+0")).asDouble() shouldBe 0.0
    context.eval(mkSource("-0")).asDouble() shouldBe 0.0
  }

  it should "correctly parse and evaluate power expression " in {
    context.eval(mkSource("2^4")).asDouble() shouldBe Math.pow(2.0, 4.0)
    context.eval(mkSource("2^5")).asDouble() shouldBe Math.pow(2.0, 5.0)
    context.eval(mkSource("2^6")).asDouble() shouldBe Math.pow(2.0, 6.0)
    context.eval(mkSource("3^4")).asDouble() shouldBe Math.pow(3.0, 4.0)
    context.eval(mkSource("2.2^4.9")).asDouble() shouldBe Math.pow(2.2, 4.9)
  }
}