Scala is a modern multi-paradigm programming language designed to express common programming patterns in a concise, elegant, and type-safe way. It smoothly integrates features of object-oriented and functional languages.

----
question:
  title: What is Scala ?
  answer: A general-purpose, functional and object oriented programming language with a strong type system
  propositions:
    - The name of a very famous boys band
    - A micro-services framework
    - The new game of Peter Molyneux
----

### Scala is expressive and statically typed

In Scala, every value has a well-defined type. The correctness of Scala programs is
enforced at compile time. Scala's type system allows using powerful abstractions in safe and coherent manner. 

Scala supports traits, generics, inner classes, type bounds and many other features that allow developers
to model their business model with precision and write complex programs with confidence. Many deadly
errors will be caught during the compilation, thus making runtime safer.

Furthermore, we say the Scala is an *expression oriented* language. When writing Scala code, we strive
to focus on *what* should be achieved, instead of *how*. We do that by combining expressions rather than
imperatively control the flow of execution. In Scala, everything has a value, including conditionals such
as *if/else*. We don't make the distinction between expression and statement like most languages do.

The powerful *pattern matching* feature allows to destructure and transform expressions in a concise way.

Can you guess what this code does ?

----
scala:
  defaultValue: |
    case class User(name: String, age: Int)

    def greetUser(user: User) = user match {
      case User("Jane", _) => println("Hi there, Jane")
      case User("Bob", _) => println("Nice to see you Bob!")
      case User(name, age) => println(s"Hey there $name, $age is a great age to learn Scala!")
    }
  
    greetUser(User("Jane", 28))
----

Try to modify the last line to get different results. Put "Bob" in place of Jane, or put your own
name and age!

### Scala is functional

Scala is a functional language in the sense that functions are first-class citizens. Scala's functions
can be passed around, nested, curried and partially applied. We'll get into the details of what these mean. 
Scala has a lightweight syntax for anonymous functions. Functions that aren't members of a class can be grouped
in singleton objects, similar to the concept of module in other languages. This means you don't even have to write
object-oriented code. Scala supports both purely functional programming, object-oriented patterns, or both depending 
on the problem at hand.

### Scala is object oriented

### Scala interoperates

### Scala is fun