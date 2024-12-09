package tcv

import tcv.api.*

object Stubs:
  trait Even extends ValidationMarker[Int, Even]
  given Even: Even with
    def isValid(value: Int): Boolean = value % 2 == 0

  trait NonZero extends ValidationMarker[Int, NonZero]
  given NonZero: NonZero with
    def isValid(value: Int): Boolean = value != 0

  trait Positive extends ValidationMarker[Int, Positive]
  given Positive: Positive with
    def isValid(value: Int): Boolean = value >= 0
