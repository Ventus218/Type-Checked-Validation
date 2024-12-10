package tcv.util

import tcv.core.*
import scala.collection.IterableOps
import scala.collection.BuildFrom

object Dsl:
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

  extension [T, Iter[X] <: IterableOps[X, Iter, Iter[X]]](iter: Iter[T])
    def are[VM <: ValidationMarker[T, VM]](using
        vm: ValidationMarker[T, VM],
        bf: BuildFrom[Iter[T], Valid[T, VM], Iter[Valid[T, VM]]]
    ): Option[Iter[Valid[T, VM]]] =
      val validated = iter.map(vm.validate)
      if validated.forall(_.isDefined) then
        Some(bf.fromSpecific(iter)(validated.flatten))
      else None
