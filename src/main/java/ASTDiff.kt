import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.AstComparator
import gumtree.spoon.builder.SpoonGumTreeBuilder
import spoon.reflect.declaration.CtElement
import java.io.File

class ASTDiff(val original: File, val mutant: File) {
    val diff = AstComparator().compare(original, mutant)

    fun size(): Int {
        return diff.rootOperations.size
    }

    fun getChanges(): List<String> {
        return diff.rootOperations.map { it.javaClass.simpleName }
    }

    fun afterChange(action: Action): CtElement {
        return diff.mappingsComp.getDst(action.node.parent).getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT) as CtElement
    }
}
