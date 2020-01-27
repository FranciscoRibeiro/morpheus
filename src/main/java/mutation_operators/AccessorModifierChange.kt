package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.builder.CtVirtualElement
import gumtree.spoon.builder.CtWrapper
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtField
import spoon.reflect.declaration.CtNamedElement
import spoon.reflect.declaration.ModifierKind
import utils.isAccessor
import utils.hasOneAccessor

class AccessorModifierChange() : MutationOperator<AccessorModifierChange>() {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op: Operation<Action>): MutationOperator<AccessorModifierChange>? {
        return when(op){
            is UpdateOperation -> checkUpdate(op)
            is InsertOperation -> checkInsert(op)
            else -> null
        }
    }

    private fun checkInsert(insOp: InsertOperation): MutationOperator<AccessorModifierChange>? {
        val (insSrc, insSrcParent) = Pair(insOp.srcNode, insOp.srcNode.parent)
        return if(insSrc is CtVirtualElement && insSrcParent is CtField<*> && hasOneAccessor(insSrcParent)){
            AccessorModifierChange(insOp.parent, insSrcParent)
        } else null
    }

    private fun checkUpdate(updateOp: UpdateOperation): MutationOperator<AccessorModifierChange>? {
        val (src, dest) = Pair(updateOp.srcNode, updateOp.dstNode)
        return if (src is CtWrapper<*> && dest is CtWrapper<*>) {
            val (srcParent, destParent) = Pair(src.parent, dest.parent)
            if (srcParent is CtNamedElement && destParent is CtNamedElement
                    && srcParent.simpleName == destParent.simpleName
                    && isAccessor(src.value as ModifierKind) && isAccessor(dest.value as ModifierKind)) {
                AccessorModifierChange(src, dest)
            } else null
        } else null
    }

    override fun toString(): String {
        return "AModifierC(from=$fromElem,to=$toElem)"
    }
}
