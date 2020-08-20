import java.io.File

fun infer(originalFile: String, mutantFile: String): String {
    val astDiff = ASTDiff(File(originalFile), File(mutantFile))
    val inferredMutOps = Inferrer(astDiff).infer()
    return "${originalFile};" +
            "${mutantFile};" +
            "${astDiff.size()};" +
            "${astDiff.getChanges()};" +
            "\"${inferredMutOps?.map { it.toString().replace("\"", "\"\"") }}\";" +
            "${inferredMutOps?.map { it.javaClass.simpleName }};" +
            "${inferredMutOps?.map { "${it.startLine}-${it.endLine}" }};" +
            "${inferredMutOps?.map { "${it.startColumn}-${it.endColumn}" }}"
}

fun main(args: Array<String>) {
    println(infer(args[0], args[1]))
}
