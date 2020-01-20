package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.code.CtLiteral
import spoon.reflect.code.CtUnaryOperator
import spoon.reflect.declaration.CtElement
import utils.retrieveValue

class ConstantReplacement(): MutationOperator<ConstantReplacement> {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement
    var insideCriteria: Boolean = true

    constructor(fromElem: CtElement, toElem: CtElement, insideCriteria: Boolean) : this() {
        this.fromElem = fromElem
        this.toElem = toElem
        this.insideCriteria = insideCriteria
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<ConstantReplacement>? {
        return when {
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<ConstantReplacement>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return when {
            delSrc is CtLiteral<*> && insSrc is CtUnaryOperator<*> -> {
                val insSrcOperand = insSrc.operand
                if (insSrcOperand is CtLiteral<*> && delSrc.parent == insOp.parent) {
                    ConstantReplacement(delSrc.parent, insSrc.parent, isInsideCriteria(delSrc, insSrc))
                } else null
            }
            delSrc is CtUnaryOperator<*> && insSrc is CtLiteral<*> -> {
                val delSrcOperand = delSrc.operand
                if (delSrcOperand is CtLiteral<*> && delSrc.parent == insOp.parent) {
                    ConstantReplacement(delSrc.parent, insSrc.parent, isInsideCriteria(delSrc, insSrc))
                } else null
            }
            else -> null
        }
    }

    override fun check(operation: Operation<Action>): MutationOperator<ConstantReplacement>? {
        return when(operation){
            is UpdateOperation -> checkUpdate(operation)
            else -> null
        }
    }

    private fun checkUpdate(updateOp: UpdateOperation): MutationOperator<ConstantReplacement>? {
        val (src, dest) = Pair(updateOp.srcNode, updateOp.dstNode)
        return if(src is CtLiteral<*> && dest is CtLiteral<*>){
            ConstantReplacement(src.parent, dest.parent, isInsideCriteria(src, dest))
        } else null
    }

    private fun isInsideCriteria(fromNode: CtElement, toNode: CtElement): Boolean {
        val from = retrieveValue(fromNode)
        val to = retrieveValue(toNode)
        return when {
            from is Int && to is Int -> to == 1 || to == 0 || to == -1 || to == -from || to == from+1 || to == from-1
            else -> false
        }
    }

    override fun toString(): String {
        return "CR${if(insideCriteria) "" else "_outside_criteria"}" +
                "(from=$fromElem,to=$toElem)"
    }
}
