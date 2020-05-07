import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

fun notIn(prog: File, list: List<String>): Boolean {
    return !list.any { prog.absolutePath.contains(it) }
}

fun main(args: Array<String>) {
    val mutantsPath = args[0]
    val originalsPath = args[1]
    val noASTChanges = Files.readAllLines(Paths.get("reports/total_no_ast_changes.txt"))
    val noCompile = Files.readAllLines(Paths.get("reports/total_no_compile.txt"))
    val mutantFiles = Files.walk(Paths.get(mutantsPath)).parallel()
            //.filter<File> { it.toString().contains(".java") }
            /*.filter { it.extension == "java" }
            .filter { notIn(it, noCompile) && notIn(it, noASTChanges) }*/

    /*val mutantFiles = File(mutantsPath).walk()
            //.filter<File> { it.toString().contains(".java") }
            .filter { it.extension == "java" }
            .filter { notIn(it, noCompile) && notIn(it, noASTChanges) }*/

//    var temp = mutantFiles.toList()

    var counter = 0
    var timeTaken = measureTimeMillis {
        mutantFiles
                .filter { it.toString().endsWith(".java") }
                .map { it.toFile() }
                .filter { notIn(it, noCompile) && notIn(it, noASTChanges) }
                .forEach {
                    var line = infer(originalsPath + it.name, it.absolutePath)
                    File("inferred_mutants.csv").appendText("$line\n")
                    counter++
                    if (counter % 100 == 0) {
                        println(counter)
                    }
                }
    }
    println(timeTaken.toFloat()/1000/60)
}
