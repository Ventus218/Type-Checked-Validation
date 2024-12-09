package tcv

import org.scalatest._
import flatspec._
import matchers._
import tcv.Api.*
import Stubs.{*, given}

class CoreTests extends AnyFlatSpec with should.Matchers:

  "validate" should "produce a defined option if the object satisfies the property" in:
    Even.validate(2) shouldBe defined

  it should "produce an empty option if the object doesn't satisfy the property" in:
    Even.validate(1) should not be defined

  it should "produce an intersection of 2 validation markers" in:
    def doSomething(a: Valid[Int, Even & NonZero]): Unit = ()
    (for
      even <- Even.validate(2)
      nonZeroEven <- NonZero.validate(even)
    yield (nonZeroEven)) match
      case None        => ()
      case Some(valid) => doSomething(valid)

  it should "produce an intersection of n validation markers" in:
    def doSomething(a: Valid[Int, Even & NonZero & Positive]): Unit = ()
    (for
      even <- Even.validate(2)
      nonZeroEven <- NonZero.validate(even)
      nonZeroEvenPositive <- Positive.validate(nonZeroEven)
    yield (nonZeroEvenPositive)) match
      case None        => ()
      case Some(valid) => doSomething(valid)

  "function calls with non-validated arguments" should "not pass type checking" in:
    def doSomething(a: Valid[Int, Even]): Unit = ()
    "doSomething(3)" shouldNot typeCheck

  "function calls with wrongly validated arguments" should "not pass type checking" in:
    def doSomething(a: Valid[Int, Even]): Unit = ()
    NonZero.validate(3) match
      case None        => ()
      case Some(valid) => "doSomething(valid)" shouldNot typeCheck

  "function calls with non-fully validated arguments" should "not pass type checking" in:
    def doSomething(a: Valid[Int, Even & NonZero]): Unit = ()
    NonZero.validate(2) match
      case None        => ()
      case Some(valid) => "doSomething(valid)" shouldNot typeCheck