package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.MoveOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.declaration.CtElement
import utils.isConditional

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
        return if (delSrc is CtBinaryOperator<*> && isConditional(delSrc.kind)
                && (movSrc == delSrc.leftHandOperand || movSrc == delSrc.rightHandOperand)) {
            ConditionalOperatorDeletion(delSrc, movSrc)
        } else null
    }

    override fun toString(): String {
        return "COD(from=$fromElem,to=$toElem)"
    }
}