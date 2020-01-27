package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.builder.CtVirtualElement
import gumtree.spoon.builder.CtWrapper
import gumtree.spoon.diff.operations.*
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtIf
import spoon.reflect.code.CtLiteral
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtField
import spoon.reflect.declaration.CtNamedElement
import spoon.reflect.declaration.ModifierKind
import utils.isAccessor
import utils.hasOneAccessor

class RemoveConditional() : MutationOperator<RemoveConditional>() {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<RemoveConditional>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            else -> null
        }
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>, op3: Operation<Action>): MutationOperator<RemoveConditional>? {
        return when{
            op1 is UpdateOperation && op2 is DeleteOperation && op3 is MoveOperation -> checkUpdateAndDeleteAndMove(op1, op2, op3)
            else -> null
        }
    }

    private fun checkUpdateAndDeleteAndMove(upOp: UpdateOperation, delOp: DeleteOperation, movOp: MoveOperation): MutationOperator<RemoveConditional>? {
        val (upSrc, upDst) = Pair(upOp.srcNode, upOp.dstNode)
        val (delSrc, movSrc) = Pair(delOp.srcNode, movOp.srcNode)
        return if (upSrc is CtLiteral<*> && upDst is CtLiteral<*>
                && delSrc is CtBinaryOperator<*> && movSrc is CtLiteral<*>
                && upDst.value is Boolean
                && (upSrc == delSrc.leftHandOperand || upSrc == delSrc.rightHandOperand)) {
            RemoveConditional(delSrc, upDst)
        } else null
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<RemoveConditional>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if(delSrc.parent is CtIf && insSrc is CtLiteral<*>
                && insSrc.value is Boolean && insOp.parent == delSrc.parent) {
            RemoveConditional(delSrc.parent as CtIf, insSrc.parent as CtIf)
        } else null
    }

    override fun toString(): String {
        return "RC(from=$fromElem,to=$toElem)"
    }
}
