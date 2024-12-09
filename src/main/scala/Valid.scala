sealed trait Valid[T, +VM <: ValidationMarker[T, VM]]:
  val value: T

object Valid:
  extension [T, VM <: ValidationMarker[T, VM]](o: Option[Valid[T, VM]])
    def and[U <: ValidationMarker[T, U]](using
        u: ValidationMarker[T, U]
    ): Option[Valid[T, VM & U]] =
      for
        a <- o
        b <- u.validate(a)
      yield (b)

private case class ValidImpl[T, +VM <: ValidationMarker[T, VM]](value: T)
    extends Valid[T, VM]
