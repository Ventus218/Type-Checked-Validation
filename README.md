# Type-Checked Validation

The aim of Type-Checked Validation (TCV) is to let you mark the object that you've just validated so that the fact that it was validated can be known at compile time!

## Example usage

First of all you must define a `ValidationMarker` trait that will hold the logic about the property you want to validate:
```scala
import tcv.Api.*

trait Even extends ValidationMarker[Int, Even]
given Even: Even with
  def isValid(value: Int): Boolean = value % 2 == 0
```

Then you can define functions that require validated arguments:
```scala
def iWantAnEvenNumber(evenNumber: Valid[Int, Even]) = ???

2.is[Even] match
  case None       => println("The input must be an even number")
  case Some(even) => iWantAnEvenNumber(even)
```

Let's break down what you've seen here:
1. The function requires a `Valid[Int, Even]` which is a wrapper of an `Int` that is known to be `Even`.
2. `2.is[Even]` returns an `Option[Valid[Int, Even]]` to let you handle the case in which the number is not even.
3. The function is now 100% sure that the input she was given is actually and even number.

The power of this library comes from composing validation requirements, let's see a more complex example:
```scala
// Here's a new validation marker for integers that are not zero
trait NonZero extends ValidationMarker[Int, NonZero]
given NonZero: NonZero with
  def isValid(value: Int): Boolean = value != 0
```

```scala
def iWantAnEvenNonZeroNumber(evenNumber: Valid[Int, Even & NonZero]) = ???

val even = 2.is[Even] 
iWantAnEvenNonZeroNumber(even.get) // This does not pass type checking as the function requires its argument to be both Even and NonZero

val evenNonZero = 2.is[Even].and[NonZero]
iWantAnEvenNonZeroNumber(evenNonZero.get) // While this correctly compiles

// Note that i've unsafely unwrapped the Option result just to make the example clearer
```

## Dsl
<!-- TODO -->

## Why it's useful
<!-- TODO -->
