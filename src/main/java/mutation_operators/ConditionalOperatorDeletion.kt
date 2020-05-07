package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.MoveOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtExpression
import spoon.reflect.declaration.CtElement
import utils.isConditional
import utils.isPartOf

class ConditionalOperatorDeletion(): MutationOperator<ConditionalOperatorDeletion>() {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<ConditionalOperatorDeletion>? {
        return when{
            op1 is DeleteOperation && op2 is MoveOperation -> checkDeleteAndMove(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndMove(delOp: DeleteOperation, movOp: MoveOperation): MutationOperator<ConditionalOperatorDeletion>? {
        val (delSrc, movSrc) = Pair(delOp.srcNode, movOp.srcNode)
        val movOpParent = movOp.parent
        if (movOpParent !is CtExpression<*> || !isPartOf(movOp.dstNode, movOpParent)) {
            return if (delSrc is CtBinaryOperator<*> && isConditional(delSrc.kind)
                    && movSrc == movOp.dstNode && (movSrc == delSrc.leftHandOperand || movSrc == delSrc.rightHandOperand)) {
                ConditionalOperatorDeletion(delSrc, movSrc)
            } else null
        }
        return null
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>, op3: Operation<Action>): MutationOperator<ConditionalOperatorDeletion>? {
        return when{
            op1 is DeleteOperation && op2 is MoveOperation && op3 is MoveOperation -> checkDeleteMoveAndMove(op1, op2, op3)
            else -> null
        }
    }

    private fun checkDeleteMoveAndMove(delOp: DeleteOperation, movOp1: MoveOperation, movOp2: MoveOperation): MutationOperator<ConditionalOperatorDeletion>? {
        val (delSrc, movSrc1, movSrc2) = Triple(delOp.srcNode, movOp1.srcNode, movOp2.srcNode)
        val (movOpParent1, movOpParent2) = Pair(movOp1.parent, movOp2.parent)
        if ((movOpParent1 !is CtExpression<*> || !isPartOf(movOp1.dstNode, movOpParent1))
                && (movOpParent2 !is CtExpression<*> || !isPartOf(movOp2.dstNode, movOpParent2))) {
            return if (delSrc is CtBinaryOperator<*> && isConditional(delSrc.kind)
                    && movOp1.parent === movOp2.parent
                    && isPartOf(movSrc1, delSrc) && isPartOf(movSrc2, delSrc)) {
                ConditionalOperatorDeletion(delSrc, movOp1.parent)
            } else null
        }
        return null
    }

    override fun toString(): String {
        return "COD(from=$fromElem,to=$toElem)"
    }
}