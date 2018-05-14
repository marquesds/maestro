# maestro

Assign fittest job to correct nu-agents


![Orchestra](resources/images/orchestra.jpg)


## Project's Structure

.
├── README.md
├── maestro.iml
├── project.clj
├── resources
│   └── images
│       └── orchestra.jpg
├── src
│   └── maestro
│       ├── cli.clj
│       ├── dao.clj
│       └── orchestrator.clj
└── test
    └── maestro
        ├── cli_test.clj
        ├── dao_test.clj
        └── orchestrator_test.clj


 - src/maestro/cli.clj
    - User's entry point where the given json file is parsed and used as input to src/maestro/orchestrator.clj `orchestrate` function;
 - src/maestro/orchestrator.clj
    - Module responsible to assign jobs to nu-agents following a set of precedence rules;
 - src/maestro/dao.clj
    - Module responsible to create, retrive and delete entities;
 - test/maestro
    - Folder that contains all project's unit tests;


## Installation

    $ lein uberjar


## Usage

    $ java -jar target/uberjar/maestro-1.0.0-standalone.jar -i <JSON_FILEPATH>


## Running Unit Tests

    $ lein test


## License

Copyright © 2018 @marquesds

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
