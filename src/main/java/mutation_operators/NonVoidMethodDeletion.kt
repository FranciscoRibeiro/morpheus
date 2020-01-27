package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.*
import spoon.reflect.code.CtInvocation
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement

class NonVoidMethodDeletion() : MutationOperator<NonVoidMethodDeletion>() {
    lateinit var delInvoc: CtInvocation<*>

    constructor(delInvoc: CtInvocation<*>): this() {
        this.delInvoc = delInvoc
    }

    override fun check(op: Operation<Action>): MutationOperator<NonVoidMethodDeletion>? {
        return when(op){
            is DeleteOperation -> checkDelete(op)
            else -> null
        }
    }

    private fun checkDelete(delOp: DeleteOperation): MutationOperator<NonVoidMethodDeletion>? {
        val delSrc = delOp.srcNode
        return if (delSrc is CtInvocation<*> && delSrc.executable.type.simpleName != "void") {
            NonVoidMethodDeletion(delSrc)
        } else null
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<NonVoidMethodDeletion>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            op1 is DeleteOperation && op2 is MoveOperation -> checkDeleteAndMove(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndMove(delOp: DeleteOperation, movOp: MoveOperation): MutationOperator<NonVoidMethodDeletion>? {
        val (delSrc, movSrc) = Pair(delOp.srcNode, movOp.srcNode)
        return if (delSrc is CtInvocation<*> && delSrc.target == movSrc && delSrc.executable.type.simpleName != "void") {
                NonVoidMethodDeletion(delSrc)
        } else null
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<NonVoidMethodDeletion>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if (delSrc is CtInvocation<*> && insSrc is CtVariableRead<*> && delSrc.target.toString() == insSrc.toString()
                && delSrc.executable.type.simpleName != "void") {
            NonVoidMethodDeletion(delSrc)
        } else null
    }

    override fun toString(): String {
        return "NVMD(deleted call to=${delInvoc.executable})"
    }
}
