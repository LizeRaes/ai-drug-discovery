# AI Drug Discovery Researcher

This is a demo app, aiming to show the latest LangChain4j features, while also giving a glimpse of the type of app that will be possible in the future.


This project uses LangChain4j, Quarkus <https://quarkus.io/> and SQLite.

## Running the application in dev mode

First, you have to configure 3 env variables :

- OPENAI_API_KEY: Open AI Api Key
- (optional for audio) GEMINI_TOKEN: Google Gemini Api Key
- TAVILY_API_KEY: get a free key: https://app.tavily.com/sign-in
- (optional for antibody design simulation) ANTHROPIC_API_KEY: can be created here: https://console.anthropic.com/settings/keys


You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```
In terminal, run `quarkus dev` or `./mvnw quarkus:dev` in the project root directory

> Make sure that mvnw is executable after pulling the repo, if not run `chmod +x mvnw`


Then open the application at `localhost:8080/index_state_machine.html` in your browser.

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/devoxx-ma-demo-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
