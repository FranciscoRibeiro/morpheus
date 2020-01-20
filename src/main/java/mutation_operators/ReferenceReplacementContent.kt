package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.*
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtInvocation
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtElement
import utils.isArithmetic

class ReferenceReplacementContent() : MutationOperator<ReferenceReplacementContent> {
    lateinit var fromElem: CtElement
    lateinit var toElem: CtElement

    constructor(fromElem: CtElement, toElem: CtElement): this() {
        this.fromElem = fromElem
        this.toElem = toElem
    }

    override fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<ReferenceReplacementContent>? {
        return when{
            op1 is DeleteOperation && op2 is InsertOperation -> checkDeleteAndInsert(op1, op2)
            op1 is InsertOperation && op2 is MoveOperation -> checkInsertAndMove(op1, op2)
            else -> null
        }
    }

    private fun checkInsertAndMove(insOp: InsertOperation, movOp: MoveOperation): MutationOperator<ReferenceReplacementContent>? {
        val (insSrc, movSrc) = Pair(insOp.srcNode, movOp.srcNode)
        return if (insSrc is CtInvocation<*> && (movSrc is CtVariableRead<*> || movSrc is CtInvocation<*>)
                && insSrc.executable.simpleName == "clone" && insSrc == movOp.parent) {
            ReferenceReplacementContent(movSrc, insSrc)
        } else null
    }

    private fun checkDeleteAndInsert(delOp: DeleteOperation, insOp: InsertOperation): MutationOperator<ReferenceReplacementContent>? {
        val (delSrc, insSrc) = Pair(delOp.srcNode, insOp.srcNode)
        return if (delSrc is CtVariableRead<*> && insSrc is CtInvocation<*>
                && insSrc.executable.simpleName == "clone" && insSrc.target.toString() == delSrc.toString()) {
            ReferenceReplacementContent(delSrc, insSrc)
        } else null
    }

    override fun toString(): String {
        return "RRC(from=$fromElem,to=$toElem)"
    }
}
