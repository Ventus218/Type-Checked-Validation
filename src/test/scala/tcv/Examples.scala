package tcv

import tcv.Api.*
import Stubs.{*, given}

object Examples:
  @main
  def example1: Unit =
    val nonZeroEven = for
      even <- Even.validate(3)
      nonZeroEven <- NonZero.validate(even)
    yield (nonZeroEven)

  @main
  def example2: Unit =
    def doSomething(a: Valid[Int, NonZero & Even]): Double =
      5d / a.value

    (for
      even <- Even.validate(3)
      nonZeroEven <- NonZero.validate(even)
    yield (nonZeroEven)) match
      case None        => ()
      case Some(value) => doSomething(value)

  @main
  def example3: Unit =
    3.is[Even].and[NonZero].and[Positive] match
      case None        => ()
      case Some(value) => ()
