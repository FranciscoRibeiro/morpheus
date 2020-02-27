package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtConstructorCall
import spoon.reflect.code.CtInvocation
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtParameter
import spoon.reflect.reference.CtTypeReference
import utils.isBitshift

class ArgumentNumberChange() : MutationOperator<ArgumentNumberChange>() {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op: Operation<Action>): MutationOperator<ArgumentNumberChange>? {
        return when(op){
            is UpdateOperation -> checkUpdate(op)
            is InsertOperation -> checkInsertion(op)
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

    private fun isArgumentToInvocation(elem: CtElement, invocation: CtInvocation<*>): Boolean {
        return invocation.arguments.contains(elem)
    }

    override fun toString(): String {
        return "ArgNrC(from=$fromElem,to=$toElem)"
    }
}
