package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.code.CtAssignment
import spoon.reflect.code.CtReturn
import spoon.reflect.declaration.CtElement

class StatementDeletion() : MutationOperator<StatementDeletion> {
    lateinit var delElem: CtElement

    constructor(delElem: CtElement): this(){
        this.delElem = delElem
    }

    override fun check(op: Operation<Action>): MutationOperator<StatementDeletion>? {
        return when(op){
            is DeleteOperation -> checkDelete(op)
            else -> null
        }
    }

    private fun checkDelete(delOp: DeleteOperation): MutationOperator<StatementDeletion>? {
        val delSrc = delOp.srcNode
        return if(delSrc is CtAssignment<*, *> || delSrc is CtReturn<*>) {
            StatementDeletion(delSrc)
        } else null
    }

    override fun toString(): String {
        return "SD(deleted=$delElem)"
    }
}
