# Infer mutation operators
Obtain the list of mutations made to a program by providing the **original version** and the **mutated version**.

Example:

```bash
java -jar mut_op_infer.jar <path_to_original> <path_to_mutated>
```

The output is a line with several fields, each separated by a _semicolon (;)_, with the following structure:

```
<path_to_original>;<path_to_mutated>;NR_AST_TRANSFORMATIONS;[AST_TRANSFORMATIONS];[SMALL_OVERVIEW_MUTATION_OPERATORS];[MUTATION_OPERATORS]
```

* First two fields are the provided input paths;
* ***NR_AST_TRANSFORMATIONS:*** Number of transformations made to the original AST in order to obtain the mutated AST;
* ***[AST_TRANSFORMATIONS]:*** List of the transformations made to the original AST. These can be:

    * Delete
    * Insert
    * Move
    * Update

* ***[SMALL_OVERVIEW_MUTATION_OPERATORS]:*** List of the **inferred** mutation operators. Each one has a small overview of the part of the code which was modified;

* ***[MUTATION_OPERATORS]:*** List with the full names of all the **inferred** mutation operators.

##Building the executable

This project uses Maven. In order to build a _jar file_ which can be executed similarly to the example in the beginning, simply run:

```bash
mvn package
```

The generated _jar_ will be in the `target` directory.
The project is configured so that the dependencies are also packaged with the _jar_.
