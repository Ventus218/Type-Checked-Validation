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

  "Iterable extendsion method \"are\"" should "validate every single item" in:
    val values = Seq(-2, -1, 1, 2)
    val validated = values.are[NonZero]
    validated shouldBe defined
    validated.get.map(_.value) should contain theSameElementsInOrderAs values

    def doSomething(iterable: Iterable[Valid[Int, NonZero]]): Unit = ()
    doSomething(validated.get)

  it should "produce an empty option if one or more items do not satisfy the validation property" in:
    val values = Seq(-2, -1, 0, 1, 2)
    val nonZeroValues = values.are[NonZero]
    nonZeroValues should not be defined

  "Iterable extendsion method \"and\"" should "behave as a for comprehension" in:
    val values = Seq(-2, -1, 0, 1, 2)
    val expected = for
      even <- values.are[Even]
      nonZeroEven <- even.and[NonZero]
      nonZeroEvenPositive <- nonZeroEven.and[Positive]
    yield (nonZeroEvenPositive)
    values.are[Even].and[NonZero].and[Positive] shouldBe expected
