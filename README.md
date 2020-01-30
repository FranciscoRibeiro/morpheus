# Morpheus
Morpheus is a tool that infers mutation operators based on the modifications made to a program.

Frequently, when working on our projects, we unintentionally introduce errors while trying to add new features or performing maintenance tasks.
To track down and fix these issues, we sometimes need more information than just the lines which got modified. Understanding the semantics behind the changes we made can be very useful in order to comprehend why the behaviour also changed. This way, we can think about it clearly and fix it more easily.

Obtain the list of mutations made to a program by providing the **original version** and the **mutated version**.

Example:

```bash
java -jar mut_op_infer.jar <path_to_original> <path_to_mutated>
```

The output is a line with several fields, each separated by a _semicolon (;)_, with the following structure:

```
<path_to_original>;<path_to_mutated>;NR_AST_TRANSFORMATIONS;[AST_TRANSFORMATIONS];[SMALL_OVERVIEW_MUTATION_OPERATORS];[MUTATION_OPERATORS];[START_END_LINES];[START_END_COLUMNS]
```

* First two fields are the provided input paths;
* ***NR_AST_TRANSFORMATIONS:*** Number of transformations made to the original AST in order to obtain the mutated AST;
* ***[AST_TRANSFORMATIONS]:*** List of the transformations made to the original AST. These can be:

    * Delete
    * Insert
    * Move
    * Update

* ***[SMALL_OVERVIEW_MUTATION_OPERATORS]:*** List of the **inferred** mutation operators. Each one has a small overview of the part of the code which was modified;

* ***[MUTATION_OPERATORS]:*** List with the full names of all the **inferred** mutation operators;

* ***[START_END_LINES]:*** List containing the start and
end lines of the places in the source where the corresponding inferred mutation operator was detected (same index in inferred mutation operator list);

* ***[START_END_COLUMNS]:*** Similar to previous field but for column numbers.

## Building the executable

This project uses Maven. In order to build a _jar file_ which can be executed similarly to the example in the beginning, simply run:

```bash
mvn package
```

The generated _jar_ will be in the `target` directory.
The project is configured so that the dependencies are also packaged with the _jar_.

## Extending Morpheus

Morpheus contains a considerable set of mutation operators that it tries to infer.
Nonetheless, new mutation operators can be added so that Morpheus also checks for their presence.

Doing it is a matter of:
* Creating a class for the intended mutation operator and extending the `MutationOperator` class;
* Providing it to the `Inferrer` class so that the inference algorithm verifies the encoded rules of the added mutation operator.
