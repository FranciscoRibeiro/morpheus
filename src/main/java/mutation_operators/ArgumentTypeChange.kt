package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtParameter
import spoon.reflect.reference.CtTypeReference
import utils.isBitshift

class ArgumentTypeChange() : MutationOperator<ArgumentTypeChange>() {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op: Operation<Action>): MutationOperator<ArgumentTypeChange>? {
        return when(op){
            is UpdateOperation -> checkUpdate(op)
            else -> null
        }
    }

    private fun checkUpdate(updateOp: UpdateOperation): MutationOperator<ArgumentTypeChange>? {
        val (src, dest) = Pair(updateOp.srcNode, updateOp.dstNode)
        return if(src is CtTypeReference<*> && dest is CtTypeReference<*>
                && src.parent is CtParameter<*> && dest.parent is CtParameter<*>){
            ArgumentTypeChange(src.parent, dest.parent)
        } else null
    }

    override fun toString(): String {
        return "ArgTypeC(from=$fromElem,to=$toElem)"
    }
}
