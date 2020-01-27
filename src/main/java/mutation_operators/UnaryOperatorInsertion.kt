package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtUnaryOperator
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement

class UnaryOperatorInsertion() : MutationOperator<UnaryOperatorInsertion>() {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<UnaryOperatorInsertion>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<UnaryOperatorInsertion>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if (delSrc is CtVariableRead<*> && insSrc is CtUnaryOperator<*>
                && delSrc.toString() == insSrc.operand.toString()) {
            UnaryOperatorInsertion(delSrc, insSrc)
        } else null
    }

    override fun toString(): String {
        return "UOI(from=$fromElem,to=$toElem)"
    }
}
