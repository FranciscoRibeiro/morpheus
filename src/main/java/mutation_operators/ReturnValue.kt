package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtReturn
import spoon.reflect.declaration.CtElement
import utils.retrieveValue

class ReturnValue() : MutationOperator<ReturnValue> {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement
    var insideCriteria: Boolean = true

    constructor(fromElem: CtElement, toElem: CtElement, insideCriteria: Boolean = true): this() {
        this.fromElem = fromElem
        this.toElem = toElem
        this.insideCriteria = insideCriteria
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<ReturnValue>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<ReturnValue>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if(delSrc.parent is CtReturn<*> && insOp.parent is CtReturn<*> && delSrc.parent == insOp.parent){
            val (delValue, insValue) = Pair(retrieveValue(delSrc), retrieveValue(insSrc))
            if(delValue != null && insValue != null){
                when{
                    delValue == "true" || delValue == "false" -> ReturnValue(delSrc.parent, insSrc.parent, isInsideCriteria(delValue.toString().toBoolean(), insValue))
                    delValue is Number && insValue is Number -> ReturnValue(delSrc.parent, insSrc.parent, isInsideCriteria(delValue, insValue))
                    insValue == "null" -> ReturnValue(delSrc.parent, insSrc.parent)
                    insValue != "null" -> ReturnValue(delSrc.parent, insSrc.parent, false)
                    else -> null
                }
            } else null
        } else null
    }

    private fun isInsideCriteria(from: Any, to: Any): Boolean {
        return when{
            from is Int && to is Int -> if(from == 0) to == 1 else to == 0
            from is Byte && to is Byte -> if(from == 0) to == 1 else to == 0
            from is Short && to is Byte -> if(from == 0) to == 1 else to == 0
            from is Long && to is Long -> to == from+1
            from is Float && to is Float -> if(from.isNaN()) to == 0 else to == -(from+1.0)
            from is Double && to is Double -> if(from.isNaN()) to == 0 else to == -(from+1.0)
            from is Boolean -> if(to == "true" || to == "false") from != to.toString().toBoolean() else false
            else -> false
        }
    }

    override fun toString(): String {
        return "RV${if(insideCriteria) "" else "_outside_criteria"}" +
                "(from=$fromElem,to=$toElem)"
    }
}
