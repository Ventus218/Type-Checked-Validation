package tcv

import tcv.Api.*

object Examples:
  import tcv.Api.*

  trait Even extends ValidationMarker[Int, Even]
  given Even: Even with
    def isValid(value: Int): Boolean = value % 2 == 0

  def iWantAnEvenNumber(evenNumber: Valid[Int, Even]) = ???

  2.is[Even] match
    case None       => println("The input must be an even number")
    case Some(even) => iWantAnEvenNumber(even)

  // Here's a new validation marker for integers that are not zero
  trait NonZero extends ValidationMarker[Int, NonZero]
  given NonZero: NonZero with
    def isValid(value: Int): Boolean = value != 0

  def iWantAnEvenNonZeroNumber(evenNumber: Valid[Int, Even & NonZero]) = ???

  val even = 2.is[Even]
  // iWantAnEvenNonZeroNumber(even.get) // This does not pass type checking as the function requires its argument to be both Even and NonZero

  val evenNonZero = 2.is[Even].and[NonZero]
  iWantAnEvenNonZeroNumber(evenNonZero.get) // While this correctly compiles

  // Note that i've unsafely unwrapped the Option result just to make the example clearer

  // **** DSL ****
  import Stubs.{Positive}

  2.is[Even].and[NonZero].and[Positive]

  for
    even <- Even.validate(2)
    evenNonZero <- NonZero.validate(even)
    evenNonZeroPositive <- Positive.validate(evenNonZero)
  yield (evenNonZeroPositive)

  // **** Avoid redundant validation ****
  private def heavyComputation(a: Int, b: Int): Int =
    require(b != 0)
    ???

  def manyHeavyComputations(a: Int, seq: Seq[Int]): Seq[Int] =
    seq.map(b => heavyComputation(a, b))

  private def heavyComputation2(a: Int, b: Int): Int =
    ???

  def manyHeavyComputations2(a: Int, seq: Seq[Int]): Seq[Int] =
    seq.foreach(b => require(b != 0))
    seq.map(b => heavyComputation2(a, b))

  private def heavyComputation3(a: Int, b: Int): Int =
    require(b != 0) // redundant
    ???

  def manyHeavyComputations3(a: Int, seq: Seq[Int]): Seq[Int] =
    seq.foreach(b => require(b != 0))
    seq.map(b => heavyComputation3(a, b))

  private def heavyComputation4(a: Int, b: Valid[Int, NonZero]): Int =
    ???

  def manyHeavyComputations4(a: Int, seq: Seq[Valid[Int, NonZero]]): Seq[Int] =
    seq.map(b => heavyComputation4(a, b))
