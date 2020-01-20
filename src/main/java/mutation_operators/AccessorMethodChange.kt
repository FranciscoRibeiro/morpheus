package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.builder.CtVirtualElement
import gumtree.spoon.builder.CtWrapper
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.code.CtInvocation
import spoon.reflect.declaration.*
import utils.isAccessor
import utils.hasOneAccessor

class AccessorMethodChange() : MutationOperator<AccessorMethodChange> {
    lateinit var fromInvoc: CtInvocation<*>
    lateinit var toInvoc: CtInvocation<*>

    constructor(fromInvoc: CtInvocation<*>, toInvoc: CtInvocation<*>): this() {
        this.fromInvoc = fromInvoc
        this.toInvoc = toInvoc
    }

    override fun check(op: Operation<Action>): MutationOperator<AccessorMethodChange>? {
        return when(op){
            is UpdateOperation -> checkUpdate(op)
            else -> null
        }
    }

    private fun checkUpdate(updateOp: UpdateOperation): MutationOperator<AccessorMethodChange>? {
        val (src, dest) = Pair(updateOp.srcNode, updateOp.dstNode)
        return if(src is CtInvocation<*> && dest is CtInvocation<*>
                && src.executable.simpleName.startsWith("get")
                && dest.executable.simpleName.startsWith("get")){
            AccessorMethodChange(src, dest)
        } else null
    }

    override fun toString(): String {
        return "AMethodC(from=$fromInvoc,to=$toInvoc)"
    }
}
