In this section, we will cover expressions and how they are evaluated. This includes values, functions and variables.
Don't hesitate to play around with the interactive examples. If you wish to go back to the initial code, click the reset
button.

### What are expressions ?

Expressions are a combination of *terms* that can be reduced to a *value*. For instance :

```
1 + 3
```

is an expression, that can be reduced to the value `4`. We call this reduction *evaluation*. You can print
expressions using `println`.

----
scala:
  defaultValue: |
    println(3) 
    println(1 + 5)
    println("Hello, world!")
----

Some languages make a distinction between *expressions* and *statements*. Statements are combinations of terms terms that
*do things* (write to a file, print to the console) but can't be reduced to a value. Scala doest not make such distinction.
In Scala, every combination of term can be evaluated to a value, and every value has a type.

### Named Values

It is possible to bind a value to a name using the `val` keyword. 
Referencing such named value does not re-compute it.

----
scala:
  defaultValue: |
    val a = 45 * 2
    println(a)
----

Values in Scala are said to be immutable. They cannot be reassigned.

```scala
val a = "Hello"
a = "Goodbye" // This does not compile
```

This immutability makes your programs safer and easier to reason about : since there is no risk that
sone other part of your program mutates this value in place, you can focus what the code at hand does without
knowing about the rest of the system. We'll cover immutability in more depth, for now just remember that values
cannot be re-assigned.

#### Typing

Recall that every value in Scala has a type. A type is a particular attribute of the value that tells
how tells the Scala compiler how this value it is intended to be used. In a strongly-typed language like Scala,
not all values are created equal. For instance, it does not make sense to multiply a number with a piece of text.

When declaring a value, you can specify its type explicitly using
a colon after the name of the value.

```scala
val name: String = "Alex"
val age: Int = 21
```

However you don't have to do that. Instead, you can let the Scala compiler figure out the types for you.
We call it *type inference*.

```scala
val name = "Alex" // This is automatically inferred to `String`
val age = 21 // This is automatially inferred to `Int`
```

So far we've encountered two basic types in Scala :

- `Int`: a 32-bit integer, ranging from -2^15 to 2^15-1 (inclusive)
- `String`: a sequence of Unicode character (`Char`)

### Variables


----
scala:
  defaultValue: |
    var a = "John"
    println(a)
    a = "James"
    println(a)
----

### Functions

Functions are expressions that take other expressions as parameters. They are defined using a *fat arrow* : `=>`.
Here's an example of an anonymous function:

```scala
(name: String) => "Hello " + name + "!"
```
The left-hand side of the funfction is a parameter list, where each parameter has an explicit type. The right-hand side
is an expression involving the parameters.

You can name functions using `val`, just like any expression.

----
scala:
  defaultValue: |
    val greeting = (name: String) => "Hello " + name + "!"
    val myName = "Max"
    println(greeting(myName))
----

Functions may take several parameters:

```scala
val multiply = (a: Int, b: Int): Int => a * b
```

Notice how you can explicitly set the return type of the function after the parameter list if you wish, using
the colon `:` syntax we've seen before.

Functions may also take no parameter at all. This is useful to delay the execution of some side-effect, like
printing to the console. We'll get into the details of what side-effects are.

----
scala:
  defaultValue: |
    val saySomething = () => println(
      """
      |Before he created Scala, Martin Odersky developed Pizza, a superset of Java
      |with generics and functional programming features.
      """.stripMargin
    )

    saySomething()
----

### Methods

Methods look and behave almost like functions. They are declared with the `def` keyword.

----
scala:
  defaultValue: |
    def add(a: Int, b: Int): Int = a + b

    println(add(20, 89))
----

Notice how the syntax changes : the fat arrow is now gone, and the parameter list is seperated from the right-hand side of the method by an equal `=` sign.
There are some key differences between methods and functions which we will cover later. For now, just remmeber that the method syntax is used in the majority of cases.

### The substitution model

We've said earlier that expressions are reduced to a value through a process of *evaluation*. Scala's evaluation model is based on the principle of the *substitution model*.
The basic idea is that all evaluation does is reduce an expression to a value, which in term does not need further evaluation, meaning we can rewrite or *substitute* an expression
for its underlying value, and leave our program unchanged.

However, the way Scala evaluates an expression can vary depending on its nature.

#### How values are evaluated

Let's say we have sone values

```scala
val x = 10
val y = 5
val result = 2 * y + 14 + x
```

The `result` will be rewritten to 34, following the mathematical order of operations :

- 2 * 5 + 14 + x
- 10 + 14 + 10
- 34

This series of operations will be executed immediately when execution scope reaches the value declaration. The value will be rewritten to its final form, meaning the
computation is won't be re-evaluated, no matter how many time you access the value.

This is usually a good thing. However, sometimes you will want to re-evaluate an expression when it is accessed, rather than when it is declared.

#### How functions are evaluated
