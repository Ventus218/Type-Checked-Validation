sealed trait Valid[T, +VM <: ValidationMarker[T, VM]]:
  val value: T
