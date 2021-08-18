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
<path_to_original>;<path_to_mutated>;ORIGINAL_FULL_NAME;MUTATED_FULL_NAME;NR_AST_TRANSFORMATIONS;[AST_TRANSFORMATION];[SMALL_OVERVIEW_MUTATION_OPERATOR];[MUTATION_OPERATOR];[CALLABLE];[OLD_START_END_LINES];[OLD_START_END_COLUMNS];[NEW_START_END_LINES];[NEW_START_END_COLUMNS];[RELATIVE_OLD_START_END_LINES];[RELATIVE_NEW_START_END_LINES]
```

* 1st and 2nd fields are the provided input paths;
* 3rd and 4th fields are the full class names of each provided file;
* ***NR_AST_TRANSFORMATIONS:*** Number of transformations made to the original AST in order to obtain the mutated AST;
* ***[AST_TRANSFORMATION]:*** Lists the transformations made to the original AST. These can be:

    * Delete
    * Insert
    * Move
    * Update

* ***[SMALL_OVERVIEW_MUTATION_OPERATOR]:*** Lists the **inferred** mutation operators. Each one has a small overview of the modified code;

* ***[MUTATION_OPERATOR]:*** Lists the full names of all the **inferred** mutation operators;

* ***[CALLABLE]:*** Lists the method/constructor names where mutations were inferred;

* ***[OLD_START_END_LINES]:*** Lists the start and end lines in the **original** file where mutations were detected;

* ***[OLD_START_END_COLUMNS]:*** Similar to previous field but for column numbers;

* ***[NEW_START_END_LINES]:*** Lists the start and end lines in the **mutated** file where mutations were detected;

* ***[NEW_START_END_COLUMNS]:*** Similar to previous field but for column numbers;

* ***[RELATIVE_OLD_START_END_LINES]:*** Lists the start and end lines inside the corresponding callable's body in the **original** file where mutations were detected;

* ***[RELATIVE_NEW_START_END_LINES]:*** Similar to previous field but for the **mutated** file.

## Building the executable

This project uses Maven. In order to build a _jar file_ which can be executed similarly to the example in the beginning, simply run:

```bash
./build.sh
```

`build.sh` is a simple script that sets necessary environment variables for the build process, like allowing Maven to use 6Gb of heap space.

This is mainly due to the fact that this stage needs a considerable amount of memory. Furthermore, as this is a self-contained _jar_, although it is easier to utilize as we do not need to specify any dependencies, it negatively impact the memory footprint of the build process.

The generated _jar_ will be in the `target` directory.

## Extending Morpheus

Morpheus contains a considerable set of mutation operators that it tries to infer.
Nonetheless, new mutation operators can be added so that Morpheus also checks for their presence.

Doing it is a matter of:
* Creating a class for the intended mutation operator and extending the `MutationOperator` class;
* Providing it to the `Inferrer` class so that the inference algorithm verifies the encoded rules of the added mutation operator.
