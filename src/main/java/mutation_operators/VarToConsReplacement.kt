package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.code.*
import spoon.reflect.declaration.CtConstructor
import spoon.reflect.declaration.CtElement
import utils.isPlusOrMinus
import utils.isSignedConstant

class VarToConsReplacement() : MutationOperator<VarToConsReplacement>() {
    lateinit var fromVar: CtElement
    lateinit var toCons: CtElement

    constructor(fromVar: CtElement, toCons: CtElement): this() {
        this.fromVar = fromVar
        this.toCons = toCons
    }

    override fun check(op: Operation<Action>): MutationOperator<VarToConsReplacement>? {
        return when{
            op is UpdateOperation -> checkUpdate(op)
            else -> null
        }
    }

    private fun checkUpdate(upOp: UpdateOperation): MutationOperator<VarToConsReplacement>? {
        val (upSrc, upDst) = Pair(upOp.srcNode, upOp.dstNode)
        if(upSrc is CtConstructorCall<*> && upDst is CtConstructorCall<*>
                && upSrc.arguments.size == upDst.arguments.size){
            for(i in upSrc.arguments.indices){
                if(upSrc.arguments[i] is CtVariableRead<*> && upDst.arguments[i] is CtLiteral<*>){
                    return VarToConsReplacement(upSrc, upDst)
                }
            }
        }
        return null
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<VarToConsReplacement>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<VarToConsReplacement>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if(delSrc.parent == insOp.parent
                && (delSrc is CtVariableRead<*> || delSrc is CtThisAccess<*>)
                && (insSrc is CtLiteral<*> || (insSrc is CtUnaryOperator<*> && isSignedConstant(insSrc)))){
            VarToConsReplacement(delSrc.parent, insSrc.parent)
        } else null
    }

    override fun toString(): String {
        return "VCR(from=$fromVar,to=$toCons)"
    }
}
