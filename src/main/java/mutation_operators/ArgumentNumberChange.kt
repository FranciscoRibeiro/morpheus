package mutation_operators

import ASTDiff
import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.code.CtConstructorCall
import spoon.reflect.code.CtInvocation
import spoon.reflect.declaration.CtElement

class ArgumentNumberChange() : MutationOperator<ArgumentNumberChange>() {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement
    lateinit var astDiff: ASTDiff

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    constructor(astDiff: ASTDiff): this() {
        this.astDiff = astDiff
    }

    override fun check(op: Operation<Action>): MutationOperator<ArgumentNumberChange>? {
        return when(op){
            is UpdateOperation -> checkUpdate(op)
            is InsertOperation -> checkInsertion(op)
            is DeleteOperation -> checkDeletion(op)
            else -> null
        }
    }

    private fun checkUpdate(updateOp: UpdateOperation): MutationOperator<ArgumentNumberChange>? {
        val (src, dest) = Pair(updateOp.srcNode, updateOp.dstNode)
        return if(src is CtConstructorCall<*> && dest is CtConstructorCall<*>
                && src.executable.type.simpleName == dest.executable.type.simpleName
                && src.arguments.size != dest.arguments.size){
            ArgumentNumberChange(src, dest)
        } else null
    }

    private fun checkInsertion(insertOp: InsertOperation): MutationOperator<ArgumentNumberChange>? {
        val (srcParent, insOpParent) = Pair(insertOp.srcNode.parent, insertOp.parent)
        return if(srcParent is CtInvocation<*> && insOpParent is CtInvocation<*>
                && isArgumentToInvocation(insertOp.srcNode, srcParent)
                && srcParent.executable.simpleName == insOpParent.executable.simpleName
                && srcParent.arguments.size != insOpParent.arguments.size){
            ArgumentNumberChange(insOpParent, srcParent)
        } else null
    }

    private fun checkDeletion(deleteOp: DeleteOperation): MutationOperator<ArgumentNumberChange>? {
        val (src, srcParent) = Pair(deleteOp.srcNode, deleteOp.srcNode.parent)
        return if(srcParent is CtInvocation<*> && isArgumentToInvocation(src, srcParent)){
            val newInvocation = astDiff.afterChange(deleteOp.action)
            if(newInvocation is CtInvocation<*> && newInvocation.arguments.size != srcParent.arguments.size) {
                ArgumentNumberChange(srcParent, newInvocation)
            } else null
        } else null
    }

    private fun isArgumentToInvocation(elem: CtElement, invocation: CtInvocation<*>): Boolean {
        return invocation.arguments.contains(elem)
    }

    override fun toString(): String {
        return "ArgNrC(from=$fromElem,to=$toElem)"
    }
}
