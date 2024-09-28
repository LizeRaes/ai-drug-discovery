# AI Drug Discovery Researcher

This is a demo app, aiming to show the latest LangChain4j features, 
while also giving a glimpse of the type of app that will be possible in the future.

You can see the logs next to the chatbot, and see which tools are called, which RAG segments are retrieved, and much more.

This project uses LangChain4j, Quarkus <https://quarkus.io/> and SQLite.

## Running the application in dev mode

First, you have to configure 3 env variables :

- OPENAI_API_KEY: Open AI Api Key
- (optional for audio) GEMINI_TOKEN: Google Gemini Api Key
- TAVILY_API_KEY: get a free key: https://app.tavily.com/sign-in
- (optional for antibody design simulation) ANTHROPIC_API_KEY: can be created here: https://console.anthropic.com/settings/keys

## Running the application
You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```
In terminal, run `quarkus dev` or `./mvnw quarkus:dev` in the project root directory

> Make sure that mvnw is executable after pulling the repo, if not run `chmod +x mvnw`

Then open the application at `localhost:8080/index_state_machine.html` in your browser.

Or at `localhost:8080/index_no_state_machine.html` for the version where the LLM decides on the state transitions.

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## A quick walkthrough of the app
This application is an AI Drug Discovery Researcher that will try to determine good new engineered antibodies for a given disease.
She will walk through the following steps with you:
1. Define target disease you want to solve **(tip: pick GBM for best results)**
2. Find antigen name and sequence (what makes the disease recognisable for anitbodies)
3. Search literature for known antibodies for that antigen + 
   their characteristics (binding affinity, specificity, stability, toxicity, immunogenicity)
4. Find CDRs for the known antibodies **(tip: pick 806 mAb and Cetuximab for best results)**
5. Find new candidate antibodies based on antigen sequence and findings from known antibodies,
6. Determine characteristics of new candidate antibody (binding affinity, specificity, stability, toxicity, immunogenicity) (user permission required to proceed with calling those tool)
7. Ask user if they want an article out of the findings for publishing in Nature

Tip: for this demo we gathered some data (scientific papers, protein sequences) related to Glioblastoma Multiforme (GBM), which is a type of brain tumor.
If you try the app with this disease, you'll find the best results with no hallucination. 

You can of course add more data, the idea is that in theory, this app would search all scientific literature and connect to a real protein database.
In that case, it can target any disease.

The tools to determine a new candidate antibody and it's characteristics are dummies, and would in a real-world application be replaced by calls to real specialized models, 
or even needed to be measured in the lab (how about steered by robotics, programmed by this app?).

## Aspects of LangChain4j demonstrated in this app
- Declarative AI services
- Tool support
- ChatMemory
- Advanced RAG (QueryCompressions, QueryRouting, EmbeddingStoreRetriever + WebSearchRetriever + SQLRetriever, RerankingAggregator)
- Audio
- Integration with diverse LLMs (OpenAI GPT-4, Anthropic Claude-3.5, Google Gemini)
- State Machine behavior (not specific to LangChain4j)
- GuardRails
- Observability (via Quarkus dev UI)

## Packaging and executables

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

