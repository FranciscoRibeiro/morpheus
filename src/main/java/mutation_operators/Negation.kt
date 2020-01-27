package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.MoveOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtUnaryOperator
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement
import utils.isArithmetic
import utils.isTypeString

class Negation() : MutationOperator<Negation>() {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<Negation>? {
        return when{
            op1 is InsertOperation && op2 is MoveOperation -> checkInsertAndMove(op1, op2)
            else -> null
        }
    }

    private fun checkInsertAndMove(insOp: InsertOperation, movOp: MoveOperation): MutationOperator<Negation>? {
        val (insSrc, movSrc) = Pair(insOp.srcNode, movOp.srcNode)
        return if (insSrc is CtUnaryOperator<*> && movSrc is CtVariableRead<*> && insSrc == movOp.parent) {
            Negation(movSrc, insSrc)
        } else null
    }

    override fun toString(): String {
        return "ABS(from=$fromElem,to=$toElem)"
    }
}
