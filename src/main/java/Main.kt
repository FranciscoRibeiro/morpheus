import java.io.File

fun infer(originalFile: String, mutantFile: String): String {
    val astDiff = ASTDiff(File(originalFile), File(mutantFile))
    val inferredMutOps = Inferrer(astDiff).infer()
    return "${originalFile};" +
            "${mutantFile};" +
            "${astDiff.fullOriginalName()};" +
            "${astDiff.fullMutantName()};" +
            "${astDiff.size()};" +
            "${astDiff.getChanges()};" +
            "\"${inferredMutOps.map { it.toString().replace("\"", "\\\"") }}\";" +
            "${inferredMutOps.map { it.javaClass.simpleName }};" +
            "${inferredMutOps.map { it.enclosingClass?.simpleName + "#" + it.enclosingMethodOrConstructor?.signature }};" +
            "${inferredMutOps.map { "${it.oldStartLine}-${it.oldEndLine}" }};" +
            "${inferredMutOps.map { "${it.oldStartColumn}-${it.oldEndColumn}" }};" +
            "${inferredMutOps.map { "${it.newStartLine}-${it.newEndLine}" }};" +
            "${inferredMutOps.map { "${it.newStartColumn}-${it.newEndColumn}" }};" +
            "${inferredMutOps.map { "${it.relativeOldStartLine}-${it.relativeOldEndLine}" }};" +
            "${inferredMutOps.map { "${it.relativeNewStartLine}-${it.relativeNewEndLine}" }}"
}

fun main(args: Array<String>) {
    println(infer(args[0], args[1]))
}
