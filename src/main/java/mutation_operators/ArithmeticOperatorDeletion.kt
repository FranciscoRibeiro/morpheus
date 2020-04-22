package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.*
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement
import utils.isArithmetic
import utils.isTypeString
import java.util.function.BinaryOperator

class ArithmeticOperatorDeletion(): MutationOperator<ArithmeticOperatorDeletion>() {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<ArithmeticOperatorDeletion>? {
        return when{
            op1 is DeleteOperation && op2 is MoveOperation -> checkDeleteAndMove(op1, op2)
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<ArithmeticOperatorDeletion>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if (delSrc is CtBinaryOperator<*> && insSrc is CtVariableRead<*>
                && !isTypeString(delSrc) && delSrc.parent == insOp.parent) {
            ArithmeticOperatorDeletion(delSrc, insSrc)
        } else null
    }

    private fun checkDeleteAndMove(delOp: DeleteOperation, movOp: MoveOperation): MutationOperator<ArithmeticOperatorDeletion>? {
        val (delSrc, movSrc) = Pair(delOp.srcNode, movOp.srcNode)
        return if (delSrc is CtBinaryOperator<*> && !isTypeString(delSrc) && isArithmetic(delSrc.kind)
                && (movSrc == delSrc.leftHandOperand || movSrc == delSrc.rightHandOperand)) {
            ArithmeticOperatorDeletion(delSrc, movSrc)
        } else null
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>, op3: Operation<Action>): MutationOperator<ArithmeticOperatorDeletion>? {
        return when{
            op1 is UpdateOperation && op2 is DeleteOperation && op3 is MoveOperation-> checkUpdateDeleteMove(op1, op2, op3)
            else -> null
        }
    }

    private fun checkUpdateDeleteMove(upOp: UpdateOperation, delOp: DeleteOperation, movOp: MoveOperation): MutationOperator<ArithmeticOperatorDeletion>? {
        val (upSrc, upDst) = Pair(upOp.srcNode, upOp.dstNode)
        val (delSrc, movSrc) = Pair(delOp.srcNode, movOp.srcNode)
        return if(upDst is CtBinaryOperator<*> && upSrc is CtBinaryOperator<*> && isArithmetic(upSrc.kind)
                && upSrc === movSrc && delSrc is CtBinaryOperator<*>){
            if(upDst.leftHandOperand == delSrc.leftHandOperand || upDst.rightHandOperand == delSrc.rightHandOperand){
                ArithmeticOperatorDeletion(delSrc, upDst)
            } else null
        } else null
    }

    override fun toString(): String {
        return "AOD(from=$fromElem,to=$toElem)"
    }
}