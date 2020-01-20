package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtReturn
import spoon.reflect.declaration.CtElement
import utils.retrieveValue

class FalseReturn() : MutationOperator<FalseReturn> {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<FalseReturn>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<FalseReturn>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if(delSrc.parent is CtReturn<*> && insOp.parent is CtReturn<*> && delSrc.parent == insOp.parent
                && retrieveValue(delSrc) != null && retrieveValue(insSrc) == "false"){
            FalseReturn(delSrc.parent, insSrc.parent)
        } else null
    }

    override fun toString(): String {
        return "FR(from=$fromElem,to=$toElem)"
    }
}
