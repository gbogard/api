### Complex expressions

### Conditionals

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
