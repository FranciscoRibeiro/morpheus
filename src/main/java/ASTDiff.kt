import com.github.gumtreediff.actions.model.Action
import com.github.gumtreediff.actions.model.Insert
import gumtree.spoon.AstComparator
import gumtree.spoon.builder.SpoonGumTreeBuilder
import gumtree.spoon.diff.Diff
import spoon.Launcher
import spoon.reflect.declaration.CtElement
import java.io.File

class ASTDiff(val original: File, val mutant: File) {
    val diff = buildDiff(original, mutant)
    val originalFullPackageName = extractFullPackageName(original)
    val originalSimpleClassName = extractSimpleClassName(original)
    val mutantFullPackageName = extractFullPackageName(mutant)
    val mutantSimpleClassName = extractSimpleClassName(mutant)

    private fun extractSimpleClassName(original: File): String {
        return original.nameWithoutExtension
    }

    private fun extractFullPackageName(original: File): String {
        val launcher = Launcher()
        launcher.addInputResource(original.path)
        launcher.buildModel()
        return launcher.model.allPackages.last().qualifiedName //assume the last package has the longest path
    }

    private fun buildDiff(original: File, mutant: File): Diff {
        return AstComparator().compare(original, mutant)
    }

    fun size(): Int {
        return diff.rootOperations.size
    }

    fun getChanges(): List<String> {
        return diff.rootOperations.map { it.javaClass.simpleName }
    }

    fun afterChange(action: Action): CtElement {
        var dstTree = diff.mappingsComp.getDst(action.node.parent)
        if(dstTree == null){
            dstTree = diff.mappingsComp.getDst((action as Insert).parent)
        }
        return dstTree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT) as CtElement
    }

    fun fullOriginalName(): String {
        return "$originalFullPackageName$$originalSimpleClassName"
    }

    fun fullMutantName(): String {
        return "$mutantFullPackageName$$mutantSimpleClassName"
    }
}
