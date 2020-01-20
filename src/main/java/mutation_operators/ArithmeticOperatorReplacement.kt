package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.code.CtBinaryOperator
import utils.isArithmetic
import utils.isConditional

class ArithmeticOperatorReplacement() : MutationOperator<ArithmeticOperatorReplacement> {
    lateinit var fromOp: CtBinaryOperator<*>
    lateinit var toOp: CtBinaryOperator<*>

    constructor(fromOp: CtBinaryOperator<*>, toOp: CtBinaryOperator<*>): this() {
        this.fromOp = fromOp
        this.toOp = toOp
    }

    override fun check(op: Operation<Action>): MutationOperator<ArithmeticOperatorReplacement>? {
        return when(op){
            is UpdateOperation -> checkUpdate(op)
            else -> null
        }
    }

    private fun checkUpdate(updateOp: UpdateOperation): MutationOperator<ArithmeticOperatorReplacement>? {
        val (src, dest) = Pair(updateOp.srcNode, updateOp.dstNode)
        return if(src is CtBinaryOperator<*> && dest is CtBinaryOperator<*>
                && isArithmetic(src.kind) && isArithmetic(dest.kind)){
            ArithmeticOperatorReplacement(src, dest)
        } else null
    }

    override fun toString(): String {
        return "AOR(from=$fromOp,to=$toOp)"
    }
}
