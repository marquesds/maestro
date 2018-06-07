# maestro

Assign fittest job to correct nu-agents

![Orchestra](resources/images/orchestra.jpg)

## Project's Structure

 - src/maestro/views/api_v1.clj
    - API responsible to create/retrieve jobs, nu-agents, job-requests and to assign fittest job to correct nu-agent;
 - src/maestro/orchestrator.clj
    - Module responsible to assign jobs to nu-agents following a set of precedence rules;
 - src/maestro/dao.clj
    - Module responsible to create, retrive and delete entities;
 - src/maestro/schema.clj
    - Module that contains API's json schemas;
 - test/maestro
    - Folder that contains all project's unit tests;

## APIs Overview

  - [Nu Agents](docs/nu-agents.md)
  - [Jobs](docs/jobs.md)
  - [Job Requests](docs/job-requests.md)
  - [Jobs Queue](docs/jobs-queue.md)

## Installation

    $ lein uberjar

## Running

    $ java -jar target/uberjar/maestro-2.0.0-standalone.jar

Note: service will run on port 8080 by default.

## Running on Docker

	$ docker build -t maestro:v2 .
	$ docker run -p 8080:8080 -it maestro:v2

## Running Unit Tests

    $ lein test

## License

Copyright © 2018 @marquesds

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
