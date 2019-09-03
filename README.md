# Lambdacademy

## Local development

Install :

```
sh ./install-deps.sh
```

```
yarn
```

Run :

```
sbt infrastructure/run
```

```
yarn dev
```

## Run back-end inside Docker

Build : 

`docker build -t lambda-back -f deploy/back.Dockerfile .`

Run :

`docker run -v /mnt/tmp:/usr/app/tmp -v scala-utils:/usr/app/build/scala-utils -v shared-files:/usr/app/build/shared-files -v /var/run/docker.sock:/var/run/docker.sock  -p 8080:8080 lambda-back`

## Scala Code Runner and templating

By default, user input will be put in the "userInput" variable. You need to print somewhere in a template
and set the template as "baseFile".

### Base templates

Base templates are located in `infratructure`

- `templates/scala/UserInput.ssp` : writes the user input to a file, nothing more
- `templates/scala/WrapInMain.ssp` : wraps the user input in an called Main that serves as entry point