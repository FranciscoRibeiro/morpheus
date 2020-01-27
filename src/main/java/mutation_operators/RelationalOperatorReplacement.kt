package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.declaration.CtElement
import utils.isRelational

class RelationalOperatorReplacement() : MutationOperator<RelationalOperatorReplacement>() {
    lateinit var fromOp: CtElement
    lateinit var toOp: CtElement

    constructor(fromOp: CtElement, toOp: CtElement) : this() {
        this.fromOp = fromOp
        this.toOp = toOp
    }

    override fun check(op: Operation<Action>): MutationOperator<RelationalOperatorReplacement>? {
        return when(op){
            is UpdateOperation -> checkUpdate(op)
            else -> null
        }
    }

    private fun checkUpdate(updateOp: UpdateOperation): MutationOperator<RelationalOperatorReplacement>? {
        val (src, dest) = Pair(updateOp.srcNode, updateOp.dstNode)
        return if(src is CtBinaryOperator<*> && dest is CtBinaryOperator<*>
                && isRelational(src.kind) && isRelational(dest.kind)){
            RelationalOperatorReplacement(src, dest)
        } else null
    }

    override fun toString(): String {
        return "ROR(from=$fromOp,to=$toOp)"
    }
}
