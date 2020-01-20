package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.builder.CtVirtualElement
import gumtree.spoon.builder.CtWrapper
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtFieldRead
import spoon.reflect.code.CtTypeAccess
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtField
import spoon.reflect.declaration.ModifierKind
import utils.hasOneStatic

class StaticModifierInsertion(): MutationOperator<StaticModifierInsertion> {
    lateinit var elem: CtElement

    constructor(fromElem: CtElement): this() {
        this.elem = fromElem
    }

    override fun check(op: Operation<Action>): MutationOperator<StaticModifierInsertion>? {
        return when(op){
            is InsertOperation -> checkInsert(op)
            else -> null
        }
    }

    private fun checkInsert(insOp: InsertOperation): MutationOperator<StaticModifierInsertion>? {
        val insSrc = insOp.srcNode
        return if((insSrc is CtWrapper<*> && insSrc.value == ModifierKind.STATIC)
                || (insSrc is CtVirtualElement && insSrc.parent is CtField<*> && hasOneStatic(insSrc.parent as CtField<*>))){
            StaticModifierInsertion(insSrc.parent)
        } else null
    }

    override fun toString(): String {
        return "SMI(inserted=$elem)"
    }
}