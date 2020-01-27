package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.code.CtBinaryOperator
import utils.isConditional

class ConditionalOperatorReplacement() : MutationOperator<ConditionalOperatorReplacement>() {
    lateinit var fromOp: CtBinaryOperator<*>
    lateinit var toOp: CtBinaryOperator<*>

    constructor(fromOp: CtBinaryOperator<*>, toOp: CtBinaryOperator<*>): this() {
        this.fromOp = fromOp
        this.toOp = toOp
    }

    override fun check(op: Operation<Action>): MutationOperator<ConditionalOperatorReplacement>? {
        return when(op){
            is UpdateOperation -> checkUpdate(op)
            else -> null
        }
    }

    private fun checkUpdate(updateOp: UpdateOperation): MutationOperator<ConditionalOperatorReplacement>? {
        val (src, dest) = Pair(updateOp.srcNode, updateOp.dstNode)
        return if(src is CtBinaryOperator<*> && dest is CtBinaryOperator<*>
                && isConditional(src.kind) && isConditional(dest.kind)){
            ConditionalOperatorReplacement(src, dest)
        } else null
    }

    override fun toString(): String {
        return "COR(from=$fromOp,to=$toOp)"
    }
}
