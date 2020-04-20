package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.MoveOperation
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.code.CtInvocation
import spoon.reflect.code.CtVariableRead

class VoidMethodDeletion() : MutationOperator<VoidMethodDeletion>() {
    lateinit var delInvoc: CtInvocation<*>

    constructor(delInvoc: CtInvocation<*>): this() {
        this.delInvoc = delInvoc
    }

    override fun check(op: Operation<Action>): MutationOperator<VoidMethodDeletion>? {
        return when(op){
            is DeleteOperation -> checkDelete(op)
            else -> null
        }
    }

    private fun checkDelete(delOp: DeleteOperation): MutationOperator<VoidMethodDeletion>? {
        val delSrc = delOp.srcNode
        return if (delSrc is CtInvocation<*>
                && delSrc.executable.type != null
                && delSrc.executable.type.simpleName == "void") {
            VoidMethodDeletion(delSrc)
        } else null
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<VoidMethodDeletion>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            op1 is DeleteOperation && op2 is MoveOperation -> checkDeleteAndMove(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndMove(delOp: DeleteOperation, movOp: MoveOperation): MutationOperator<VoidMethodDeletion>? {
        val (delSrc, movSrc) = Pair(delOp.srcNode, movOp.srcNode)
        return if (delSrc is CtInvocation<*> && delSrc.target == movSrc
                && delSrc.executable.type != null && delSrc.executable.type.simpleName == "void") {
            VoidMethodDeletion(delSrc)
        } else null
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<VoidMethodDeletion>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if (delSrc is CtInvocation<*> && insSrc is CtVariableRead<*> && delSrc.target.toString() == insSrc.toString()
                && delSrc.executable.type != null && delSrc.executable.type.simpleName == "void") {
            VoidMethodDeletion(delSrc)
        } else null
    }

    override fun toString(): String {
        return "VMD(deleted call to=${delInvoc.executable})"
    }
}
