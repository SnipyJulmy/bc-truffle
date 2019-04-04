import org.graalvm.polyglot.PolyglotException

class EvaluationTest extends BcTestSpec {

  val maxInt: String = s"${Integer.MAX_VALUE}"
  val minInt: String = s"${Integer.MIN_VALUE}"

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
    context.eval(mkSource("3 * 4 * 5 * 6 * 7")).asDouble() shouldBe (B(3) * B(4) * B(5) * B(6) * B(7))
    context.eval(mkSource("12.3 * -56.7")).asDouble() shouldBe B(12.3) * B(-56.7)
    context.eval(mkSource("3 + 4 * 6 + 1 * 45 * 3 * -9 + 1 - 123 * 21")).asDouble() shouldBe 3 + 4 * 6 + 1 * 45 * 3 * -9 + 1 - 123 * 21
  }

  it should "correctly parse and evaluate unary operator expression" in {
    context.eval(mkSource("-23")).asDouble() shouldBe -23.0
    context.eval(mkSource("+23")).asDouble() shouldBe 23.0
    context.eval(mkSource("+0")).asDouble() shouldBe 0.0
    context.eval(mkSource("-0")).asDouble() shouldBe 0.0
  }

  it should "correctly parse and evaluate power expression " in {
    context.eval(mkSource("2^4")).asDouble() shouldBe B(2).pow(4)
    context.eval(mkSource("2^5")).asDouble() shouldBe B(2).pow(5)
    context.eval(mkSource("2^6")).asDouble() shouldBe B(2).pow(6)
    context.eval(mkSource("3^4")).asDouble() shouldBe B(3).pow(4)
    context.eval(mkSource("2.2^4.9")).asDouble() shouldBe B(2.2).pow(4)
    context.eval(mkSource("821372187321321398731 ^ 3")).fitsInInt() shouldBe false
  }

  it should "correctly evaluate parenthesized expression" in {
    context.eval(mkSource("(2)")).asDouble() shouldBe 2
    context.eval(mkSource("(-2)")).asDouble() shouldBe -2
    context.eval(mkSource("(-2) + (-2)")).asDouble() shouldBe -4
    context.eval(mkSource("(-2) + (-2) + (2)")).asDouble() shouldBe -2
    context.eval(mkSource("(-4 * 3) + (3 * 4 - 1 + (3 ^ 0)) + (1 + (1 + (1)) - 3) + (0)")).asDouble() shouldBe 0
    context.eval(mkSource("(4 ^ 1) + (-2 ^ 1) + (-2 ^ 2)")).asDouble() shouldBe 6
    context.eval(mkSource("(4 ^ 1) + (-2 ^ 1) + (-2 ^ 3)")).asDouble() shouldBe -6
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3)")).asDouble() shouldBe 0
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + 1")).asDouble() shouldBe 1
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + (1)")).asDouble() shouldBe 1
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + (1 ^ 1)")).asDouble() shouldBe 1
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + (1 ^ 0)")).asDouble() shouldBe 1
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + (8 ^ 0)")).asDouble() shouldBe 1
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + (-8 ^ 0)")).asDouble() shouldBe 1
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + (-8 ^ 1)")).asDouble() shouldBe -8
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + (-8 ^ 2)")).asDouble() shouldBe 64
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + (-8 ^ 3)")).asDouble() shouldBe -512
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + (3 ^ 0)")).asDouble() shouldBe 1
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + (-3 ^ 0)")).asDouble() shouldBe 1
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + (-2 ^ 0)")).asDouble() shouldBe 1
    context.eval(mkSource("(8 ^ 3) + (-8 ^ 3) + (-2 ^ 3)")).asDouble() shouldBe -8
    // thank you, number theory
    context.eval(mkSource("(8866128975287528 ^ 3) + (-8778405442862239 ^ 3) + (-2736111468807040 ^ 3)")).asInt() shouldBe 33
    context.eval(mkSource("(8866128975287528 ^ 3) + (8778405442862239 ^ 3)")).toString shouldBe "1373418274408761713072539992376076290046598779871"
  }

  it should "correctly evaluate string concatenation and expression" in {
    context.eval(mkSource(""" "a" + "b" """)).asString() shouldBe "ab"
    context.eval(mkSource(""" "a" + 1 """)).asString() shouldBe "a1"
    context.eval(mkSource(""" 1 + "a" """)).asString() shouldBe "1a"
    context.eval(mkSource(""" 1 + 2 + 3 + "a" """)).asString() shouldBe "6a"
    context.eval(mkSource(""" (1 + "abc") + (1 ^ 9) """)).asString() shouldBe "1abc1"
    context.eval(mkSource(""" "a" + 1 + "b" """)).asString() shouldBe "a1b"
    context.eval(mkSource(""" "a" + "1" + "b" """)).asString() shouldBe "a1b"
    context.eval(mkSource(""" 1 + 2 + 3 + 4 + 5 + "6" + 7 + 8 + 9 """)).asString() shouldBe "156789"
  }

  it should "correctly evaluate simple or expression" in {
    context.eval(mkSource("0 || 0")).asInt() shouldBe 0
    context.eval(mkSource("0 || 1")).asInt() shouldBe 1
    context.eval(mkSource("1 || 0")).asInt() shouldBe 1
    context.eval(mkSource("1 || 1")).asInt() shouldBe 1
  }

  it should "correctly evaluate simple and expression" in {
    context.eval(mkSource("0 && 0")).asInt() shouldBe 0
    context.eval(mkSource("0 && 1")).asInt() shouldBe 0
    context.eval(mkSource("1 && 0")).asInt() shouldBe 0
    context.eval(mkSource("1 && 1")).asInt() shouldBe 1
  }

  it should "correctly evaluate simple negationed expression" in {
    context.eval(mkSource("!1")).asInt() shouldBe 0
    context.eval(mkSource("!0")).asInt() shouldBe 1
    context.eval(mkSource("!4")).asInt() shouldBe 0
    context.eval(mkSource("!213213")).asInt() shouldBe 0
    context.eval(mkSource("!2132132132138213621383871387163213213")).asInt() shouldBe 0
    context.eval(mkSource("!9999999999999999999999999999999999999999999999999999999999")).asInt() shouldBe 0
    context.eval(mkSource("!10")).asInt() shouldBe 0
  }

  it should "correctly evaluate simple comparison expression" in {
    val bigNumber = "999999999999999999999999999999999999999999"
    val veryBigNumber = s"$bigNumber ^ 2"

    context.eval(mkSource("1 == 1")).asInt() shouldBe 1
    context.eval(mkSource("0 == 1")).asInt() shouldBe 0
    context.eval(mkSource("13 == 23")).asInt() shouldBe 0
    context.eval(mkSource("321 == 123")).asInt() shouldBe 0
    context.eval(mkSource("99 == 99")).asInt() shouldBe 1
    context.eval(mkSource("132 > 131")).asInt() shouldBe 1
    context.eval(mkSource("132 >= 131")).asInt() shouldBe 1
    context.eval(mkSource("132 < 131")).asInt() shouldBe 0
    context.eval(mkSource("132 <= 131")).asInt() shouldBe 0
    context.eval(mkSource("132 <= 132")).asInt() shouldBe 1
    context.eval(mkSource("132 >= 132")).asInt() shouldBe 1

    context.eval(mkSource(s"$maxInt >= $minInt")).asInt() shouldBe 1
    context.eval(mkSource(s"$maxInt > $minInt")).asInt() shouldBe 1
    context.eval(mkSource(s"$maxInt >= 0")).asInt() shouldBe 1
    context.eval(mkSource(s"$maxInt > 0")).asInt() shouldBe 1
    context.eval(mkSource(s"$minInt <= 0")).asInt() shouldBe 1
    context.eval(mkSource(s"$minInt < 0")).asInt() shouldBe 1
    context.eval(mkSource(s"$minInt >= 0")).asInt() shouldBe 0
    context.eval(mkSource(s"$minInt > 0")).asInt() shouldBe 0
    context.eval(mkSource(s"$minInt > $maxInt")).asInt() shouldBe 0
    context.eval(mkSource(s"$minInt >= $maxInt")).asInt() shouldBe 0
    context.eval(mkSource(s"$maxInt < $minInt")).asInt() shouldBe 0
    context.eval(mkSource(s"$maxInt <= $minInt")).asInt() shouldBe 0
    context.eval(mkSource(s"$maxInt + 1 > $maxInt")).asInt() shouldBe 1
    context.eval(mkSource(s"$maxInt + 1 >= $maxInt")).asInt() shouldBe 1
    context.eval(mkSource(s"$minInt - 1 < $minInt")).asInt() shouldBe 1
    context.eval(mkSource(s"$maxInt - 1 <= $maxInt")).asInt() shouldBe 1

    context.eval(mkSource(s"$veryBigNumber >= $bigNumber")).asInt() shouldBe 1
    context.eval(mkSource(s"$veryBigNumber > $bigNumber")).asInt() shouldBe 1

    context.eval(mkSource(s"$maxInt == $maxInt")).asInt() shouldBe 1
    context.eval(mkSource(s"$minInt == $minInt")).asInt() shouldBe 1
    context.eval(mkSource(s"$bigNumber == $bigNumber")).asInt() shouldBe 1
    context.eval(mkSource(s"$veryBigNumber == $veryBigNumber")).asInt() shouldBe 1

    context.eval(mkSource(s"$maxInt != $maxInt")).asInt() shouldBe 0
    context.eval(mkSource(s"$minInt != $minInt")).asInt() shouldBe 0
    context.eval(mkSource(s"$bigNumber != $bigNumber")).asInt() shouldBe 0
    context.eval(mkSource(s"$veryBigNumber != $veryBigNumber")).asInt() shouldBe 0
  }

  it should "correctly evaluate simple negated expression" in {
    context.eval(mkSource(s"- 1")).asInt() shouldBe -1
    context.eval(mkSource(s"-(-1)")).asInt() shouldBe 1
    context.eval(mkSource(s"-(-(-(-(-(-((((-(-(-(-((-(-9)))))))))))))))")).asInt() shouldBe 9
  }

  it should "correctly parse and evaluate function call to print" in {
    context.eval(mkSource(s"print(3.2)")).asDouble() shouldBe 3.2
    context.eval(mkSource(s"print(3)")).asInt() shouldBe 3
  }

  it should "failed to parse incorrect expression" in {
    assertThrows[PolyglotException](context.eval(mkSource("1 - (2")))
    assertThrows[PolyglotException](context.eval(mkSource("1 - 2)")))
    assertThrows[PolyglotException](context.eval(mkSource("print(2")))
    assertThrows[PolyglotException](context.eval(mkSource("fib 2)")))
    assertThrows[PolyglotException](context.eval(mkSource("fib (2")))
  }
}
