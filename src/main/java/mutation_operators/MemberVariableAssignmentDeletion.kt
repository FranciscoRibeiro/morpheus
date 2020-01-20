package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.code.CtAssignment
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtFieldWrite
import spoon.reflect.code.CtLiteral
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtField
import utils.isConditional
import utils.isDefaultValue

class MemberVariableAssignmentDeletion(): MutationOperator<MemberVariableAssignmentDeletion> {
    lateinit var delElem: CtElement

    constructor(delElem: CtElement): this() {
        this.delElem = delElem
    }

    override fun check(op: Operation<Action>): MutationOperator<MemberVariableAssignmentDeletion>? {
        return when(op){
            is DeleteOperation -> checkDelete(op)
            else -> null
        }
    }

    private fun checkDelete(delOp: DeleteOperation): MutationOperator<MemberVariableAssignmentDeletion>? {
        return if(delOp.srcNode is CtLiteral<*> && delOp.srcNode.parent is CtField<*>) {
            MemberVariableAssignmentDeletion(delOp.srcNode.parent)
        } else null
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<MemberVariableAssignmentDeletion>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<MemberVariableAssignmentDeletion>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        val delSrcParent = delSrc.parent
        return if (delSrcParent is CtAssignment<*, *> && insSrc is CtLiteral<*>
                && delSrcParent == insOp.parent && delSrcParent.getAssigned() is CtFieldWrite
                && isDefaultValue(insSrc)) {
            MemberVariableAssignmentDeletion(delSrcParent)
        } else null
    }

    override fun toString(): String {
        return "MVAD(deleted_assignment=$delElem)"
    }
}