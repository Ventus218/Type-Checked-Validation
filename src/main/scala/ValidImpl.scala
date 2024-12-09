private case class ValidImpl[T, +VM <: ValidationMarker[T, VM]](value: T)
    extends Valid[T, VM]
