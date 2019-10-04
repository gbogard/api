While you can follow this course entirely in your browser, you will need to
install a few things on your computer to develop real-life Scala applications.

To be productive, you need essentially three things:

- *sbt*, the most used build tool for Scala
- the *scala* command, a REPL for Scaa
- an IDE / text editor that support the Scala language

### Installing Sbt and the *scala* command through Sdkman (Unix based systems)

Sdkman is a tool for managing multiple versions of Software Development Kits that works
on most Unix systems (including MacOs). Sdkman makes it easy to install SDKs and switch between
multiple versions.

#### Installing Sdkman

Installing Sdkman is a one-command process: 

```shell
curl -s "https://get.sdkman.io" | bash
```

Then you should be able to type `sdk help` and see all the available commands.

#### Install Scala through Sdkman

One you have the `sdk` command available on your machine, you can use it to install Scala :

```shell
sdk install scala
```

will install the latest version of Scala on your machine. Once it is done, you should be able to type `scala --version`
and see something like this :

```shell
Scala code runner version 2.13.1 -- Copyright 2002-2019, LAMP/EPFL and Lightbend, Inc.
```

#### Install sbt through Sdkman

Once you have installed Scala, you need to install Sbt, the most widely-used build tool for the language.

Sbt will take care of several tasks for you, including :

- Define your project's metadata such as its name, its version ...
- Downloading the version of Scala you chose for your project
- Managing external dependencies for your project
- Build your Scala classes
- Run your tests 
- ...

To install sbt, you can do

```shell
sdk install sbt
```

If everything went well, you should be able to type `sbt sbtVersion` and see something like this

```shell
[info] Loading global plugins from /home/guillaume/.sbt/1.0/plugins
[info] Loading project definition from /home/guillaume/project
[info] Set current project to guillaume (in build file:/home/guillaume/)
[info] 1.2.8
```

### Using the Scala REPL

The Scala REPL is a tool for evaluating Scala expressions interactively. By using the `scala` command, you will
start a REPL session. REPL stands for **R**ead, **E**valuate, **P**rint, **L**oop. You can type any valid Scala expression,
and when you press Enter, the REPL will evaluate it for you and print the result. 
It's very useful to get a quick intuition of how the language works.

```shell
scala
```

```shell
Welcome to Scala 2.13.1 (OpenJDK 64-Bit Server VM, Java 1.8.0_222).
Type in expressions for evaluation. Or try :help.

scala> 56 + 2
```
```shell
res0: Int = 58

scala> 
```

To type multiline expressions, you can hold the *Shift* key along with *Enter*, and instead of submitting the
expression, it will span a new line.

Using the *Tab* key will get you autocompletion.

To exit the REPL, you can type `:quit`

### Creating a new Scala project through Sbt

To generate a new Scala project with Sbt, you can use the following command :

```shell
sbt new scala/scala-seed.g8
```

This will prompt for project details such as its name, and generate a new project structure inside a subfolder.
The generated project contains a basic build definition in the `build.sbt` file, a basic Main file, and examples
of how to write automated tests using Scalatest.

### Installing a Scala editor

The last thing you need to develop Scala applications on your machine is a good editor. Until recently, 
**Intellij IDEA**, a Java and Scala IDE from *Jetbrains*,
 was the de-facto choice for many Scala developers. But dedicated members of the Scala community created
**Metals**, an open-source language server for Scala that provides good Scala support to all the major text editors. 

Here are some tips on making your choice:

- If you're just getting started with Scala, and don't mind using something heavy and sometimes slow, **Intellij IDEA**
will usually make your life easier. It contains penty of features that make language easier to discover.
- Use **Metals** instead if you 
  - Want something lighter and more performant, to use it on slower hardware or save battery life
  - Don't care for advanced IDE features such as refactoring tools, but still want Autocomplete, automatic imports and
  inline errors.
  - Really want to stick to your favorite editor, whether it's Vim, VS Code, Emacs, Atom ...
  - Want to support Open Source software

Of course, choosing an editor is a matter of personal preference, and nothing prevents you from testing both options.

To install **Intellij IDEA**, you need to download it from [Jetbrain's website](https://www.jetbrains.com/idea/download).
Specific instructions will depend on your system.

To install **Metals**, instructions will depend on the actual editor you use, Metals is just a plug-in. 
The easiest editor to Use Metals with is probably [Visual Studio Code](https://code.visualstudio.com/), an open-source
text editor for all major platforms. 

Instructions to install Metals on your editor of choice can be found on 
[the project's website](https://scalameta.org/metals/docs/editors/overview.html).


****

#### Recap

This sums up all you need to install to write Scala applications 😀.

----
question:
  title: What is Sbt ?
  answer: A tool to manage dependencies, build and run your Scala project
  propositions:
    - A tool to evaluate Scala expressions on-the-fly
    - A plug-in that provides Scala autocompletion to your text editor
    - A Seriously Big Tarantula 🕷️
----

----
question:
  title: What does REPL stand for ?
  answer: Read, Evaluate, Print Loop
  propositions:
    - Return, Evaluate, Process Loop
    - Resistance against the Empire of Pokémon Lords
----