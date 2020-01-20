package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.MoveOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtConstructorCall
import spoon.reflect.code.CtLiteral
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement
import utils.isArithmetic
import utils.isConditional
import utils.isTypeString

class ConstructorCallReplacementNull(): MutationOperator<ConstructorCallReplacementNull> {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<ConstructorCallReplacementNull>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<ConstructorCallReplacementNull>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if (delSrc is CtConstructorCall<*> && insSrc is CtLiteral<*> && insSrc.value == null
                && delSrc.parent == insOp.parent) {
            ConstructorCallReplacementNull(delSrc, insSrc)
        } else null
    }

    override fun toString(): String {
        return "CCRN(from=$fromElem,to=$toElem)"
    }
}