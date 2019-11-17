So far we've talked expressions, functions and methods. There's still a lot to cover. Let's start
with complex expressions !

### Complex expressions and lexical scoping

All the epxressions we've seen so far took only one line, but if we need more space ?

In Scala, you can use *blocks*, pairs of curly braces `{}` to delimitate expressions that can span multiple lines.
The last value of the expression will also be the value of the overall expression.

```scala
val myNumber: Int = {
  val a = 15
  val c = 89
  a + c
}

// => 104
```

How would someone write a method that spans multiple lines ? Well, anywhere you can put a simple expression,
you can put a block too! The syntax for multiple-lines methods the same as seen before


### Conditionals

### The substitution model

We've said earlier that expressions are reduced to a value through a process of *evaluation*. Scala's evaluation model is based on the principle of the *substitution model*.
The basic idea is that all evaluation does is reduce an expression to a value, which in term does not need further evaluation, meaning we can rewrite or *substitute* an expression
for its underlying value, and leave our program unchanged.

However, the way Scala evaluates an expression can vary depending on its nature.

#### How values are evaluated

Let's say we have some values

```scala
val x = 10
val y = 5
val result = 2 * y + 14 + x
```

The `result` will be rewritten to 34, following the mathematical order of operations :

- 2 * 5 + 14 + x
- 10 + 14 + 10
- 34

This series of operations will be executed immediately when execution scope reaches the value declaration.
 The value will be rewritten to its final form, meaning the computation won't be evaluated again, 
 no matter how many time you access the value.

This is usually a good thing. However, sometimes you will want to re-evaluate an expression each time it is accessed, rather 
than only upon declaration.

#### How functions are evaluated
