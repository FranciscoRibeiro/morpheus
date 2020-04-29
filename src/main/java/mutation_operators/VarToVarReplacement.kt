package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.*
import spoon.reflect.code.CtThisAccess
import spoon.reflect.code.CtTypeAccess
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement

class VarToVarReplacement() : MutationOperator<VarToVarReplacement>() {
    lateinit var fromVar: CtElement
    lateinit var toVar: CtElement

    constructor(fromVar: CtElement, toVar: CtElement) : this() {
        this.fromVar = fromVar
        this.toVar = toVar
    }

    override fun check(op: Operation<Action>): MutationOperator<VarToVarReplacement>? {
        return when(op){
            is UpdateOperation -> checkUpdate(op)
            else -> null
        }
    }

    private fun checkUpdate(updateOp: UpdateOperation): MutationOperator<VarToVarReplacement>? {
        val (src, dest) = Pair(updateOp.srcNode, updateOp.dstNode)
        return if(src is CtVariableRead<*> && dest is CtVariableRead<*>){
            VarToVarReplacement(src, dest)
        } else null
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<VarToVarReplacement>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            op1 is InsertOperation && op2 is MoveOperation -> checkInsertAndMove(op1, op2)
            else -> null
        }
    }

    private fun checkInsertAndMove(insOp: InsertOperation, movOp: MoveOperation): MutationOperator<VarToVarReplacement>? {
        val (insSrc, movSrc) = Pair(insOp.srcNode, movOp.srcNode)
        return if((insSrc == movOp.parent || movSrc.parent == insOp.parent)
                && insSrc is CtVariableRead<*> && movSrc is CtVariableRead<*>){
            VarToVarReplacement(movSrc.parent, insSrc.parent)
        } else null
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<VarToVarReplacement>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if (delSrc.parent == insOp.parent
                && (delSrc is CtVariableRead<*> || delSrc is CtThisAccess<*> || delSrc is CtTypeAccess<*>)
                && (insSrc is CtVariableRead<*> || insSrc is CtThisAccess<*> || insSrc is CtTypeAccess<*>)) {
            VarToVarReplacement(delSrc.parent, insSrc.parent)
        } else null
    }

    override fun toString(): String {
        return "VVR(from=$fromVar,to=$toVar)"
    }
}
