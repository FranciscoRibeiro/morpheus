import java.io.File
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val mutantsPath = args[0]
    val originalsPath = args[1]
    val mutantFiles = File(mutantsPath).walk()
            .filter { it.toString().contains(".java") }

    var counter = 0
    var timeTaken = measureTimeMillis {
        mutantFiles.toList().parallelStream().forEach {
            var line = infer(originalsPath + it.name, it.absolutePath)
            File("inferred_mutants.csv").appendText("$line\n")
            counter++
            if(counter % 100 == 0){
                println(counter)
            }
        }
        /*for (mutant in mutantFiles){
            *//*println(originalsPath + mutant.name)
            println(mutant.absolutePath)
            println("---------------------------------")*//*
            var line = infer(originalsPath + mutant.name, mutant.absolutePath)
            File("inferred_mutants.csv").appendText("$line\n")
            counter++
            if(counter % 100 == 0){
                println(counter)
            }
        }*/
    }
    println(timeTaken.toFloat()/1000/60)
}
