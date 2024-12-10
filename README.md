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

## DSL
The syntax:
```scala
2.is[Even].and[NonZero].and[Positive]...
```

is just syntactic sugar for:
```scala
for
  even <- Even.validate(2)
  evenNonZero <- NonZero.validate(even)
  evenNonZeroPositive <- Positive.validate(evenNonZero)
yield(evenNonZeroPositive)
```

## Why it's useful

### Keeping validation outside of your domain logic
Now you're able to define functions that express validation requirements.
This means that you can define your domain logic without worrying about validation and related error handling.

```
        +-------------------------------------------+
        |              Validation Layer             |
        |                                           |
        |              +------------------------+   |
        |              |                        |   |
data ---|-> validate --|->    Domain Logic      |   |
        |      |       |                        |   |
error <-|------+       +------------------------+   |
        |                                           |
        +-------------------------------------------+
```

### Avoid redundant validation
Have you ever been in the situation of knowing that a specific property holds but in order to keep your code future-proof you just end up validating it again?

Let's make a stupid example:

```scala
private def heavyComputation(a: Int, b: Int): Int =
  require(b != 0)
  ???

def manyHeavyComputations(a: Int, seq: Seq[Int]): Seq[Int] =
  seq.map(b => heavyComputation(a, b))
```

It would be better to check that every single element of `seq` is not 0 BEFORE actually performing the operation. Because if the last element of `seq` was 0 we would have almost completed the computation just for failing at the last step.

So you decide to move the validation into `manyHeavyComputations`
```scala
private def heavyComputation2(a: Int, b: Int): Int =
  ???

def manyHeavyComputations2(a: Int, seq: Seq[Int]): Seq[Int] =
  seq.foreach(b => require(b != 0))
  seq.map(b => heavyComputation2(a, b))
```

This is more efficient, but you know that software changes, and someday it may happen that:
- other functions will use `heavyComputation` forgetting about input validation
- maybe changes to the implementation of `manyHeavyComputations` will get rid of the input check

So, to make your code future-proof, you end up validating the input again inside `heavyComputation`:

```scala
private def heavyComputation3(a: Int, b: Int): Int =
  require(b != 0) // redundant
  ???

def manyHeavyComputations3(a: Int, seq: Seq[Int]): Seq[Int] =
  seq.foreach(b => require(b != 0))
  seq.map(b => heavyComputation3(a, b))
```

This exact problem can be avoided by exploiting TCV:
```scala
private def heavyComputation4(a: Int, b: Valid[Int, NonZero]): Int =
  ???

def manyHeavyComputations4(a: Int, seq: Seq[Valid[Int, NonZero]]): Seq[Int] =
  seq.map(b => heavyComputation4(a, b))
```
