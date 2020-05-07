package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.MoveOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtExpression
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement
import utils.isConditional
import utils.isPartOf

class ConditionalOperatorInsertion() : MutationOperator<ConditionalOperatorInsertion>() {
    lateinit var fromElem: CtElement
    lateinit var toOp: CtBinaryOperator<*>

    constructor(fromElem: CtElement, toOp: CtBinaryOperator<*>): this() {
        this.fromElem = fromElem
        this.toOp = toOp
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<ConditionalOperatorInsertion>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            op1 is InsertOperation && op2 is MoveOperation -> checkInsertAndMove(op1, op2)
            else -> null
        }
    }

    private fun checkInsertAndMove(insOp: InsertOperation, movOp: MoveOperation): MutationOperator<ConditionalOperatorInsertion>? {
        val (insSrc, movSrc) = Pair(insOp.srcNode, movOp.srcNode)
        val insOpParent = insOp.parent
        if (insOpParent !is CtExpression<*> || isPartOf(movOp.dstNode, insOpParent)) {
            return if (insSrc is CtBinaryOperator<*> && isConditional(insSrc.kind) && insSrc == movOp.parent) {
                ConditionalOperatorInsertion(movSrc, insSrc)
            } else null
        }
        return null
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<ConditionalOperatorInsertion>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if (delSrc is CtVariableRead<*> && insSrc is CtBinaryOperator<*> && isConditional(insSrc.kind)) {
            val (delVar, insLeftOp, insRightOp) = Triple(delSrc.toString(), insSrc.leftHandOperand, insSrc.rightHandOperand)
            if (delVar == insLeftOp.toString() || delVar == insRightOp.toString()) {
                ConditionalOperatorInsertion(delSrc, insSrc)
            } else null
        } else null
    }

    override fun toString(): String {
        return "COI(from=$fromElem,to=$toOp)"
    }
}
