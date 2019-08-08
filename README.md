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

## Scala Code Runner and templating

By default, user input will be put in the "userInput" variable. You need to print somewhere in a template
and set the template as "baseFile".

### Base templates

Base templates are located in `infratructure`

- `templates/scala/UserInput.ssp` : writes the user input to a file, nothing more
- `templates/scala/WrapInMain.ssp` : wraps the user input in an called Main that serves as entry point