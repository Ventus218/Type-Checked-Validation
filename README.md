# Type-Checked Validation

It can happen that an object property is validated (for example an integer to be even) and then that object is passed through many other functions.
Each one of these function will not be able to be 100% sure that the object still mainain it's property, and so it may need to validate it again.

The aim of Type-Checked Validation (TCV) is to let you mark the validated object so that at it can be known at compile time that the validated property still holds!
