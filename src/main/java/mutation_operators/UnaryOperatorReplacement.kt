package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtUnaryOperator
import utils.areOpposite
import utils.isArithmetic
import utils.isConditional

class UnaryOperatorReplacement() : MutationOperator<UnaryOperatorReplacement> {
    lateinit var fromOp: CtUnaryOperator<*>
    lateinit var toOp: CtUnaryOperator<*>
    var insideCriteria: Boolean = true

    constructor(fromOp: CtUnaryOperator<*>, toOp: CtUnaryOperator<*>, insideCriteria: Boolean = true): this() {
        this.fromOp = fromOp
        this.toOp = toOp
        this.insideCriteria = insideCriteria
    }

    override fun check(op: Operation<Action>): MutationOperator<UnaryOperatorReplacement>? {
        return when(op){
            is UpdateOperation -> checkUpdate(op)
            else -> null
        }
    }

    private fun checkUpdate(updateOp: UpdateOperation): MutationOperator<UnaryOperatorReplacement>? {
        val (src, dest) = Pair(updateOp.srcNode, updateOp.dstNode)
        return if(src is CtUnaryOperator<*> && dest is CtUnaryOperator<*>){
            UnaryOperatorReplacement(src, dest, areOpposite(src.kind, dest.kind))
        } else null
    }

    override fun toString(): String {
        return "UOR${if(insideCriteria) "" else "_outside_criteria"}" +
                "(from=$fromOp,to=$toOp)"
    }
}
