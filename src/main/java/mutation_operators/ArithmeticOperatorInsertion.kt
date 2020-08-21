package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.*
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtExpression
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement
import utils.isArithmetic
import utils.isTypeString
import utils.simpleExpr

class ArithmeticOperatorInsertion() : MutationOperator<ArithmeticOperatorInsertion>() {
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
        return if (delSrc is CtExpression<*> && insSrc is CtBinaryOperator<*> && isArithmetic(insSrc.kind)) {
            val (insLeftOp, insRightOp) = Pair(insSrc.leftHandOperand, insSrc.rightHandOperand)
            if (isTypeString(insLeftOp) || isTypeString(insRightOp)) null
            else if (delSrc == insLeftOp || delSrc == insRightOp) ArithmeticOperatorInsertion(delSrc, insSrc)
            else if (simpleExpr(delSrc) == simpleExpr(insLeftOp) || simpleExpr(delSrc) == simpleExpr(insRightOp)) {
                ArithmeticOperatorInsertion(delSrc, insSrc)
            } else null
        } else null
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>, op3: Operation<Action>): MutationOperator<ArithmeticOperatorInsertion>? {
        return when{
            op1 is UpdateOperation && op2 is InsertOperation && op3 is MoveOperation-> checkUpdateInsertMove(op1, op2, op3)
            else -> null
        }
    }

    private fun checkUpdateInsertMove(upOp: UpdateOperation, insOp: InsertOperation, movOp: MoveOperation): MutationOperator<ArithmeticOperatorInsertion>? {
        val (upSrc, upDst) = Pair(upOp.srcNode, upOp.dstNode)
        val (insSrc, movSrc) = Pair(insOp.srcNode, movOp.srcNode)
        return if(upDst is CtBinaryOperator<*> && upSrc is CtBinaryOperator<*> && isArithmetic(upDst.kind)
                && upSrc === movSrc && insSrc === movOp.parent){
            val movOpParent = movOp.parent as? CtBinaryOperator<*> ?: return null
            val (upSrcLeft, upSrcRight) = Pair(upSrc.leftHandOperand, upSrc.rightHandOperand)
            if(upSrcLeft == movOpParent.leftHandOperand || upSrcRight == movOpParent.rightHandOperand){
                ArithmeticOperatorInsertion(upSrc, insSrc)
            } else null
        } else null
    }

    override fun toString(): String {
        return "AOI(from=$fromElem,to=$toElem)"
    }
}
