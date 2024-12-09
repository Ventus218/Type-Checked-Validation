sealed trait Valid[T, +VM <: ValidationMarker[T, VM]]:
  val value: T

private case class ValidImpl[T, +VM <: ValidationMarker[T, VM]](value: T)
    extends Valid[T, VM]
