# Lambdacademy

A friendly, interactive platform for learning functional programming online.

![GitHub last commit](https://img.shields.io/github/last-commit/gbogard/lambdacademy)

## Project goals

- Enable people to learn functional programming by following interactive courses where they
can read tutorials, do exercises, code in their browser, and complete real projects.
- Be fun : the long-term goal of the platform is to have gamification features that make learning more motivating and more sociable. The courses themselves should be playful.
- Be inclusive : the goal here is to share the love of functional programming and empower developers of all backgrounds to write beautiful code

... and of course

- be functional : besides the courses' content, the platform itself is developed with functional
programming in mind, by using a combination of Scala and ReasonML.

## Launching the API

```
sbt infrastructure/run
```

You will need an instance of the Scala Runner to execute Scala code.

### Launching the runner with sbt

Pull the `lambdacademy-dev/scala-runner` project from Git, and then launch it using

```shell script
sbt server/run
```

### Launching the runner with Docker

You can pull the required images from the repository

```shell script
docker pull docker.pkg.github.com/lambdacademy-dev/scala-runner/scala-runner-runtime:LATEST
docker pull docker.pkg.github.com/lambdacademy-dev/scala-runner/scala-runner-server:LATEST
```

And then run it using

```shell script
docker run -p 2003:2003 -v /var/run/docker.sock:/var/run/docker.sock -v "$PWD/tmp:/app/tmp" -e "TMP_ROOT_HOST_PATH=$PWD/tmp" -e TMP_ROOT_CONTAINER_PATH=/app/tmp  docker.pkg.github.com/lambdacademy-dev/scala-runner/scala-runner-server:LATEST
```

More details in the README of the `lambdacademy-dev/scala-runner` project.

## Participation

This project follows the [Scala Code of Conduct](https://www.scala-lang.org/conduct/). All participants are expected to be kind and courteous. 