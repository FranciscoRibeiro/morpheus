package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.*
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

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<ConditionalOperatorReplacement>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<ConditionalOperatorReplacement>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if(delSrc is CtBinaryOperator<*> && isConditional(delSrc.kind)
                && insSrc is CtBinaryOperator<*> && isConditional(insSrc.kind)
                && insOp.parent.parent === delSrc){
            ConditionalOperatorReplacement(delSrc, insSrc)
        } else null
    }

    override fun toString(): String {
        return "COR(from=$fromOp,to=$toOp)"
    }
}
