package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.MoveOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtLiteral
import spoon.reflect.code.CtThisAccess
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement

class VarToConsReplacement() : MutationOperator<VarToConsReplacement>() {
    lateinit var fromVar: CtElement
    lateinit var toCons: CtElement

    constructor(fromVar: CtElement, toCons: CtElement): this() {
        this.fromVar = fromVar
        this.toCons = toCons
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<VarToConsReplacement>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<VarToConsReplacement>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if(delSrc.parent == insOp.parent
                && (delSrc is CtVariableRead<*> || delSrc is CtThisAccess<*>)
                && insSrc is CtLiteral<*>){
            VarToConsReplacement(delSrc.parent, insSrc.parent)
        } else null
    }

    override fun toString(): String {
        return "VCR(from=$fromVar,to=$toCons)"
    }
}
