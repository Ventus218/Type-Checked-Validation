package tcv

import org.scalatest._
import flatspec._
import matchers._
import tcv.Api.*
import Stubs.{*, given}

class DslTests extends AnyFlatSpec with should.Matchers:
  "extension method \"is\"" should "behave as validate method" in:
    val values = Seq(0, 1, 2)
    values.foreach: value =>
      value.is[Even] shouldBe Even.validate(value)

  "Option extension method \"and\"" should "behave as a for comprehension" in:
    val values = Seq(-2, -1, 0, 1, 2)
    values.foreach: value =>
      val expected = for
        even <- Even.validate(value)
        nonZeroEven <- NonZero.validate(even)
        nonZeroEvenPositive <- Positive.validate(nonZeroEven)
      yield (nonZeroEvenPositive)
      value.is[Even].and[NonZero].and[Positive] shouldBe expected
