package lambda.library

import lambda.domain.courses.Page.SimplePage
import lambda.domain.courses.Page.PageId
import lambda.domain.courses.Course
import lambda.domain.courses.Course.CourseId
import Utils._
import lambda.domain.Media
import cats.Monad
import cats.implicits._
import lambda.dsl.courses._

object ATourOfScala {

  private val title: String = "A tour of Scala"
  private val id: String = slug(title)

  val intro = (simplePage("What is Scala ?")
    withWidget mk(
      widgetId(id, 0),
      """Scala is a modern, multi-paradigm programming language designed to express common programming patterns in a concise, elegant, and type-safe way. It smoothly integrates features of object-oriented and functional languages.
      |
      |Scala was created by Martin Odersky at the [EPFL](https://www.epfl.ch/en/), in Switzerland. The first release of Scala is from 2003.
      |The language has since then successfully been used by major tech companies such as Twitter, and as the primary language of
      |widely-used open-source projects such as [Apache Spark](https://spark.apache.org/). Scala can be used for heavy 
      |data processing, machine learning, web services and more. It has been steadily gaining popularity in the IT industry, 
      |and is probably the most popular functional programming language today.
      |It's also worth noticing that while Scala jobs can still be arguably hard to find, Scala jobs are among the five
      |best paying programming languages worldwide, along with three other functional programming: F#, Clojure and Elixir.
      |[Source: Stackoverflow Survey 2019](https://insights.stackoverflow.com/survey/2019). It's clear that functional programming skills are sought for and valued.
      |
      |### Why should I learn Scala ?
      |
      |Here are a few reasons to consider taking on this Scala journey:
      |
      |- **You want to gain functional programming skills and be a better developer**. While you might not use Scala as part of your daily
      |job, you will be able to reuse some of the skills you will learn here. Functional programming is a way of thinking that can help you
      |as a developer, regardless of the technologies you use. It makes software easier to predict, test, and reason about.
      |- **You already work on the JVM** and you are frustrated with Java. Well, Scala might be what you are looking for. It's a well designed
      |language, often more expressive the Java, that also happens to interoperate very easily with all the Java libraries you already know.
      |Scala solves some of the most frustrating things about Java : null-pointer exceptions, verbose syntax, error handling, side-effects, not being able
      |to group values without creating classes ... And because it is fully compatible with Java, it can be gradually adopted without too much risk.
      |- **You want to build highly scalable, concurrent applications** : functional programming, combined with some of the best libraries the
      |rich Scala ecosystem provides, makes working with multiple threads easy. Scala enables you to process big amount of data in streams, build web
      |servers that can serve tens of thousands of requests per second, and parallelize computations in a safe manner.
      |""".stripMargin
    )
    withWidget multipleChoices(
      widgetId(id, 1),
      "What is Scala ?",
      "A general-purpose, functional and object oriented programming language with a strong type system",
      "The name of a very famous boys band from the 90s",
      "A framework to build microservices",
      "The new game of Peter Molyneux"
    )
    withWidget mk(
      widgetId(id, 2),
      """#### What's in this course ?
      |
      |This first course covers the basics of the language. It should be a first step for people that have some programming
      |experience and no experience in Scala.
      |
      |### Scala is expressive and statically typed
      |
      |In Scala, every value has a well-defined type. The correctness of Scala programs is
      |enforced at compile time. Scala's type system allows using powerful abstractions in safe and coherent manner. 
      |
      |Scala supports traits, generics, inner classes, type bounds and many other features that allow developers
      |to model their business model with precision and write complex programs with confidence. Many deadly
      |errors will be caught during the compilation, thus making runtime safer.
      |
      |Furthermore, we say the Scala is an *expression oriented* language. When writing Scala code, we strive
      |to focus on *what* should be achieved, instead of *how*. We do that by combining expressions rather than
      |imperatively control the flow of execution. In Scala, everything has a value, including conditionals such
      |as *if/else*. We don't make the distinction between expression and statement like most languages do.
      |
      |The powerful *pattern matching* feature allows to destructure and transform expressions in a concise way.
      |
      |Can you guess what this code does ?""".stripMargin
    )
    withWidget (
      scala(widgetId(id, 3))
        withBaseFile sourceFileResource("/a-tour-of-scala/1-intro/code-template-1.ssp")
        withDefaultValue (
          """case class User(name: String, age: Int)
        |
        |    def greetUser(user: User) = user match {
        |      case User("Jane", _) => println("Hi there, Jane")
        |      case User("Bob", _) => println("Nice to see you Bob!")
        |      case User(name, age) => println(s"Hey there $name, $age is a great age to learn Scala!")
        |    }
        |  
        |    greetUser(User("Jane", 28))""".stripMargin
        )
    )
    withWidget mk(
      widgetId(id, 4),
      """Try to modify the last line to get different results. Put "Bob" in place of Jane, or put your own
      |name and age!
      |
      |### Scala is functional
      |
      |Scala is a functional language in the sense that functions are first-class citizens. Scala's functions
      |can be passed around, nested, curried and partially applied. We'll get into the details of what these mean. 
      |Scala has a lightweight syntax for anonymous functions. Functions that aren't members of a class can be grouped
      |in singleton objects, similar to the concept of module in other languages. This means you don't even have to write
      |object-oriented code. Scala supports both purely functional programming, object-oriented patterns, or both depending 
      |on the problem at hand.
      |
      |Functional programming is a programming *paradigm*, a way of reasoning about software under a different angle than what
      |most programmers are used to. The goal of functional programming is to use **functions** as the primary building block of your
      |application, while minimizing **side-effects** (we'll get there). This makes software more reliable, easier to test and easier
      |to understand.
      |
      |### Scala is object oriented
      |
      |In Scala, every value is also an object. The language supports complex type hierarchies through classes and traits.
      |Traits support multiple-inheritance with the ability to mix interface declarations with behavior implementation.
      |Scala traits can act as *interfaces* and *mixins*, and they are the foundation for some of the most widely used patterns
      |in Scala.
      |
      |Imagine you have role-playing game where characters can combine abilities from different classes. Here's how
      |you can use traits to combine the abilities of Thief with those of a Wizard to create a new class of character.
      |""".stripMargin
    )
    withWidget (
      scala(widgetId(id, 5))
        withDefaultValue (
          """abstract class Character {
            |  val name: String
            |}
            |
            |trait Magic {
            |  def castSpell() = println("Woosh!")
            |}
            |
            |trait Stealth {
            |  def sneakBehind(target: Character) = println(s"You've stolen ${target.name}'s purse!")
            |}
            |
            |class Wizard(val name: String) extends Character with Magic
            |
            |// Wizard AND Thief, because why not ?
            |class WizardThief(val name: String) extends Character with Stealth with Magic
            |
            |val player = new WizardThief("Olugorim The Great")
            |val npc = new Wizard("Ezor The Fierce")
            |
            |player.castSpell()
            |player.sneakBehind(npc)""".stripMargin
        )
    )
    withWidget mk(
      widgetId(id, 6),
      """
      |### Scala interoperates
      |
      |Scala runs on the Java Virtual Machine, or *JVM* for short. It can also execute Java code, like your own Java classes or your favorite
      |Java library. Calling Java from Scala requires no syntax change, it just works. The goal of Scala is not to recreate what Java already
      |has, meaning if something exists in Java's standard library, you can just use it.
      |""".stripMargin
    ))

  def apply(): Course =
    (course(title)
      withDescription (
        """
        |Scala is a modern, expressive, statically typed programming language that is both functional and object oriented.
        |This course is aimed at people who already have some experience of programming and would like to learn the basics
        |of functional programming in Scala.
        |
        """.stripMargin.trim
      )
      withImage mediaResource("/public/a-tour-of-scala.png")
      withPage intro)

}
