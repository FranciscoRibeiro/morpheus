package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtLiteral
import spoon.reflect.code.CtThisAccess
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement

class ConsToVarReplacement() : MutationOperator<ConsToVarReplacement> {
    lateinit var fromCons: CtElement
    lateinit var toVar: CtElement

    constructor(fromCons: CtElement, toVar: CtElement): this() {
        this.fromCons = fromCons
        this.toVar = toVar
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<ConsToVarReplacement>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<ConsToVarReplacement>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if(delSrc.parent == insOp.parent && delSrc is CtLiteral<*>
                && (insSrc is CtVariableRead<*> || insSrc is CtThisAccess<*>)){
            ConsToVarReplacement(delSrc.parent, insSrc.parent)
        } else null
    }

    override fun toString(): String {
        return "CVR(from=$fromCons,to=$toVar)"
    }
}
