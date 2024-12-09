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

  extension [T](t: T)
    def is[VM <: ValidationMarker[T, VM]](using
        vm: ValidationMarker[T, VM]
    ): Option[Valid[T, VM]] =
      vm.validate(t)

private case class ValidImpl[T, +VM <: ValidationMarker[T, VM]](value: T)
    extends Valid[T, VM]
