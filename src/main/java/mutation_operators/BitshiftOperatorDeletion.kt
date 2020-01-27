package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.MoveOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement
import utils.isArithmetic
import utils.isBitshift
import utils.isConditional
import utils.isTypeString

class BitshiftOperatorDeletion(): MutationOperator<BitshiftOperatorDeletion>() {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<BitshiftOperatorDeletion>? {
        return when{
            op1 is DeleteOperation && op2 is MoveOperation -> checkDeleteAndMove(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndMove(delOp: DeleteOperation, movOp: MoveOperation): MutationOperator<BitshiftOperatorDeletion>? {
        val (delSrc, movSrc) = Pair(delOp.srcNode, movOp.srcNode)
        return if (delSrc is CtBinaryOperator<*> && isBitshift(delSrc.kind)
                && (movSrc == delSrc.leftHandOperand || movSrc == delSrc.rightHandOperand)) {
            BitshiftOperatorDeletion(delSrc, movSrc)
        } else null
    }

    override fun toString(): String {
        return "BSOD(from=$fromElem,to=$toElem)"
    }
}