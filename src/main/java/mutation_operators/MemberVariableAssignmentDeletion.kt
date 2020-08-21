package mutation_operators

import ASTDiff
import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.*
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtField
import utils.isDefaultValue
import utils.nullLiteral

class MemberVariableAssignmentDeletion(): MutationOperator<MemberVariableAssignmentDeletion>() {
    lateinit var delElem: CtElement
    lateinit var astDiff: ASTDiff

    constructor(delElem: CtElement): this() {
        this.delElem = delElem
    }

    constructor(astDiff: ASTDiff): this() {
        this.astDiff = astDiff
    }

    override fun check(op: Operation<Action>): MutationOperator<MemberVariableAssignmentDeletion>? {
        return when(op){
            is DeleteOperation -> checkDelete(op)
            else -> null
        }
    }

    private fun checkDelete(delOp: DeleteOperation): MutationOperator<MemberVariableAssignmentDeletion>? {
        val delSrc = delOp.srcNode
        return if(delSrc is CtExpression<*> && !nullLiteral(delSrc) && delSrc.parent is CtField<*>) {
            val newMemberAssignment = astDiff.afterChange(delOp.action) as? CtField<*> ?: return null
            if(newMemberAssignment.defaultExpression == null) {
                MemberVariableAssignmentDeletion(delSrc.parent)
            } else null
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
        return if (((delSrcParent is CtAssignment<*, *> && delSrcParent.getAssigned() is CtFieldWrite)
                    || delSrcParent is CtField<*>)
                && delSrcParent == insOp.parent
                && insSrc is CtLiteral<*> && (isDefaultValue(insSrc) || nullLiteral(insSrc))
        ) {
            MemberVariableAssignmentDeletion(delSrcParent)
        } else null
    }

    override fun toString(): String {
        return "MVAD(deleted_assignment=$delElem)"
    }
}