package tcv.util

import tcv.core.*
import scala.collection.IterableOps
import scala.annotation.targetName

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
        vm: ValidationMarker[T, VM]
    ): Option[Iter[Valid[T, VM]]] =
      val validated = iter.collect({ case vm(valid) => valid })
      if validated.size == iter.size then Some(validated)
      else None

  extension [T, Iter[X] <: IterableOps[
    X,
    Iter,
    Iter[X]
  ], VM <: ValidationMarker[T, VM]](iter: Iter[Valid[T, VM]])
    def and[U <: ValidationMarker[T, U]](using
        u: ValidationMarker[T, U]
    ): Option[Iter[Valid[T, VM & U]]] =
      val validated = iter.collect({ case u(valid) => valid })
      if validated.size == iter.size then Some(validated)
      else None

  extension [T, Iter[X] <: IterableOps[
    X,
    Iter,
    Iter[X]
  ], VM <: ValidationMarker[T, VM]](iterOpt: Option[Iter[Valid[T, VM]]])
    @targetName("andAre")
    def and[U <: ValidationMarker[T, U]](using
        u: ValidationMarker[T, U]
    ): Option[Iter[Valid[T, VM & U]]] =
      for
        a <- iterOpt
        b <- a.and[U]
      yield (b)
