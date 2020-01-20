package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.builder.CtWrapper
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtFieldRead
import spoon.reflect.code.CtTypeAccess
import spoon.reflect.code.CtUnaryOperator
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtField
import spoon.reflect.declaration.ModifierKind

class StaticModifierDeletion(): MutationOperator<StaticModifierDeletion> {
    lateinit var delElem: CtElement

    constructor(fromElem: CtElement): this() {
        this.delElem = fromElem
    }

    override fun check(op: Operation<Action>): MutationOperator<StaticModifierDeletion>? {
        return when(op){
            is DeleteOperation -> checkDelete(op)
            else -> null
        }
    }

    private fun checkDelete(delOp: DeleteOperation): MutationOperator<StaticModifierDeletion>? {
        val delSrc = delOp.srcNode
        return if(delSrc is CtWrapper<*> && delSrc.value == ModifierKind.STATIC){
            StaticModifierDeletion(delSrc.parent)
        } else null
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<StaticModifierDeletion>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<StaticModifierDeletion>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        val insOpParent = insOp.parent
        return if(delSrc is CtWrapper<*> && delSrc.value == ModifierKind.STATIC && insOpParent is CtFieldRead<*>){
            val delSrcParent = delSrc.parent
            if(delSrcParent is CtField<*> && insSrc is CtTypeAccess<*>
                    && delSrcParent.simpleName == insOpParent.variable.simpleName){
                StaticModifierDeletion(delSrcParent)
            } else null
        } else null
    }

    override fun toString(): String {
        return "SMD(deleted=$delElem)"
    }
}