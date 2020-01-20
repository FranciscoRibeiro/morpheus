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
import utils.isTypeString

class ArithmeticOperatorInsertion() : MutationOperator<ArithmeticOperatorInsertion> {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<ArithmeticOperatorInsertion>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            op1 is InsertOperation && op2 is MoveOperation -> checkInsertAndMove(op1, op2)
            else -> null
        }
    }

    private fun checkInsertAndMove(insOp: InsertOperation, movOp: MoveOperation): MutationOperator<ArithmeticOperatorInsertion>? {
        val (insSrc, movSrc) = Pair(insOp.srcNode, movOp.srcNode)
        return if (insSrc is CtBinaryOperator<*> && isArithmetic(insSrc.kind) && insSrc == movOp.parent) {
            val (insLeftOp, insRightOp) = Pair(insSrc.leftHandOperand, insSrc.rightHandOperand)
            if(isTypeString(insLeftOp) || isTypeString(insRightOp)) null
            else ArithmeticOperatorInsertion(movSrc, insSrc)
        } else null
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<ArithmeticOperatorInsertion>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if (delSrc is CtVariableRead<*> && insSrc is CtBinaryOperator<*> && isArithmetic(insSrc.kind)) {
            val (delVar, insLeftOp, insRightOp) = Triple(delSrc.variable, insSrc.leftHandOperand, insSrc.rightHandOperand)
            if(isTypeString(insLeftOp) || isTypeString(insRightOp)) null
            else if (delVar.toString() == insLeftOp.toString() || delVar.toString() == insRightOp.toString()) {
                ArithmeticOperatorInsertion(delSrc, insSrc)
            } else null
        } else null
    }

    override fun toString(): String {
        return "AOI(from=$fromElem,to=$toElem)"
    }
}
