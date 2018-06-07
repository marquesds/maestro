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

### Get nu-agent

Get nu-agent by its uuid

**URL**: `/api/v1/nu-agents/{uuid}`

**Method**: `GET`

#### Success Response

**Code**: `200 OK`

**Output Example**:

```json
{
    "id": "8ab86c18-3fae-4804-bfd9-c3d6e8f66260",
    "name": "BoJack Horseman",
    "primary_skillset": [
        "bills-questions"
    ],
    "secondary_skillset": [],
    "jobs_performed": {
        "bills-questions": 1
    }
}
```

#### Not Found Response

**Code**: `404 Not found`

**Output Example**:

```json
{}
```

### Get nu-agents

Get all nu-agents

**URL**: `/api/v1/nu-agents`

**Method**: `GET`

#### Success Response

**Code**: `200 OK`

**Output Example**:

```json
[
    {
        "id": "8ab86c18-3fae-4804-bfd9-c3d6e8f66260",
        "name": "BoJack Horseman",
        "primary_skillset": [
            "bills-questions"
        ],
        "secondary_skillset": [],
        "jobs_performed": {
            "bills-questions": 1
        }
    },
    {
        "id": "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88",
        "name": "Mr. Peanut Butter",
        "primary_skillset": [
            "rewards-question"
        ],
        "secondary_skillset": [
        	"bills-questions"
        ],
        "jobs_performed": {
        	"rewards-question": 4,
            "bills-questions": 1
        }
    }
]
```

#### Not Found Response

**Code**: `404 Not found`

**Output Example**:

```json
[]
```

### Create nu-agent

Create a new nu-agent

**URL**: `/api/v1/nu-agents`

**Method**: `POST`

**Body Example**:
```json
{
  "id": "8ab86c18-3fae-4804-bfd9-c3d6e8f66260",
  "name": "BoJack Horseman",
  "primary_skillset": ["bills-questions"],
  "secondary_skillset": []
}
```

#### Success Response

**Code**: `201 Created`

**Output Example**:

```json
{}
```

#### Invalid Body Response

**Code**: `400 Bad request`

**Output Example**:

```json
{
    "error": "Value does not match schema: {\"name\" (not (instance? java.lang.String 1))}"
}
```

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
