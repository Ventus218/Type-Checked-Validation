package tcv.core

trait ValidationMarker[T, +Self <: ValidationMarker[T, Self]]:
  // this self type makes it impossible to compile things like: trait Even extends ValidationMarker[Int, NonZero]
  self: Self =>
  def isValid(value: T): Boolean

  def validate(value: T): Option[Valid[T, Self]] =
    if isValid(value) then Some(ValidImpl(value)) else None

  def validate[VM <: ValidationMarker[T, VM]](
      otherValid: Valid[T, VM]
  ): Option[Valid[T, VM & Self]] =
    validate(otherValid.value) match
      case Some(value) => Some(ValidImpl(value.value))
      case None        => None

  def unapply(value: T): Option[Valid[T, Self]] = validate(value)

  def unapply[VM <: ValidationMarker[T, VM]](
      value: Valid[T, VM]
  ): Option[Valid[T, VM & Self]] = validate(value)
