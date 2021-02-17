package mutation_operators

import ASTDiff
import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.DeleteOperation
import gumtree.spoon.diff.operations.InsertOperation
import gumtree.spoon.diff.operations.Operation
import gumtree.spoon.diff.operations.UpdateOperation
import spoon.reflect.cu.SourcePosition
import spoon.reflect.declaration.CtClass
import spoon.reflect.declaration.CtExecutable

abstract class MutationOperator<T: MutationOperator<T>> {
    var oldStartLine: Int = 0
    var oldEndLine: Int = 0
    var oldStartColumn = 0
    var oldEndColumn = 0
    var newStartLine: Int = 0
    var newEndLine: Int = 0
    var newStartColumn = 0
    var newEndColumn = 0
    var relativeOldStartLine: Int = 0
    var relativeOldEndLine: Int = 0
    var relativeNewStartLine: Int = 0
    var relativeNewEndLine: Int = 0
    var enclosingMethodOrConstructor: CtExecutable<*>? = null
    var enclosingClass: CtClass<*>? = null

    fun matches(opsSubList: List<Operation<Action>>, astDiff: ASTDiff): MutationOperator<T>? {
        val mutOp = when(opsSubList.size){
            1 -> check(opsSubList[0])
            2 -> check(opsSubList[0], opsSubList[1])
            3 -> check(opsSubList[0], opsSubList[1], opsSubList[2])
            else -> null
        }
        if(mutOp != null){
            val oldStartEndLineList = oldStartAndEndLine(opsSubList.first())
            val oldStartEndColumnList = oldStartAndEndColumn(opsSubList.first())
            val newStartEndLineList = newStartAndEndLine(opsSubList.last(), astDiff)
            val newStartEndColumnList = newStartAndEndColumn(opsSubList.last(), astDiff)
            mutOp.oldStartLine = oldStartEndLineList.min() ?: 0
            mutOp.oldEndLine = oldStartEndLineList.max() ?: 0
            mutOp.oldStartColumn = oldStartEndColumnList.min() ?: 0
            mutOp.oldEndColumn = oldStartEndColumnList.max() ?: 0
            mutOp.newStartLine = newStartEndLineList.min() ?: 0
            mutOp.newEndLine = newStartEndLineList.max() ?: 0
            mutOp.newStartColumn = newStartEndColumnList.min() ?: 0
            mutOp.newEndColumn = newStartEndColumnList.max() ?: 0
            mutOp.enclosingMethodOrConstructor = inferSrcEnclosingMethodOrConstructor(opsSubList.first())
            if (mutOp.enclosingMethodOrConstructor != null){
                mutOp.enclosingClass = inferEnclosingClass(mutOp.enclosingMethodOrConstructor!!)
                mutOp.calcRelatives(opsSubList)
            }
        }
        return mutOp
    }

    private fun inferEnclosingClass(executable: CtExecutable<*>): CtClass<*>? {
        return executable.getParent(CtClass::class.java)
    }

    private fun calcRelatives(opsSubList: List<Operation<Action>>) {
        if(enclosingMethodOrConstructor != null) {
            relativeOldStartLine = calcRelativeLine(enclosingMethodOrConstructor!!, oldStartLine)
            relativeOldEndLine = calcRelativeLine(enclosingMethodOrConstructor!!, oldEndLine)
            val newEnclosingMethodOrConstructor = inferDstEnclosingMethodOrConstructor(opsSubList.last()) ?: return
            relativeNewStartLine = calcRelativeLine(newEnclosingMethodOrConstructor, newStartLine)
            relativeNewEndLine = calcRelativeLine(newEnclosingMethodOrConstructor, newEndLine)
        } else return
    }

    private fun inferDstEnclosingMethodOrConstructor(op: Operation<Action>): CtExecutable<*>? {
        return if (op.dstNode != null) op.dstNode.getParent(CtExecutable::class.java)
        else op.srcNode.getParent(CtExecutable::class.java)
    }

    private fun inferSrcEnclosingMethodOrConstructor(op: Operation<Action>): CtExecutable<*>? {
        return if(op is InsertOperation) op.parent.getParent(CtExecutable::class.java)
        else op.srcNode.getParent(CtExecutable::class.java)
    }

    private fun newStartAndEndColumn(op: Operation<Action>, astDiff: ASTDiff): List<Int> {
        var position: SourcePosition
        return try {
            position = if(op is InsertOperation) op.srcNode.position else op.dstNode.position
            listOf(position.column, position.endColumn)
        } catch (e: UnsupportedOperationException){
            listOf(0,0)
        } catch (e: IllegalStateException){
            position = astDiff.afterChange(op.action).position
            if(position.isValidPosition) listOf(position.column, position.endColumn)
            else listOf(0,0)
        }
    }

    private fun oldStartAndEndColumn(op: Operation<Action>): List<Int> {
        return try {
            val position = if (op is InsertOperation) op.parent.position else op.srcNode.position
            listOf(position.column, position.endColumn)
        } catch (e: UnsupportedOperationException){
            listOf(0,0)
        } catch (e: IllegalStateException){
            listOf(0,0)
        }
    }

    private fun newStartAndEndLine(op: Operation<Action>, astDiff: ASTDiff): List<Int> {
        var position: SourcePosition
        return try {
            position = if(op is InsertOperation) op.srcNode.position else op.dstNode.position
            listOf(position.line, position.endLine)
        } catch (e: UnsupportedOperationException){
            listOf(0,0)
        } catch (e: IllegalStateException){
            position = astDiff.afterChange(op.action).position
            if(position.isValidPosition) listOf(position.line, position.endLine)
            else listOf(0,0)
        }
    }

    private fun oldStartAndEndLine(op: Operation<Action>): List<Int> {
        return try {
            val position = if (op is InsertOperation) op.parent.position else op.srcNode.position
            listOf(position.line, position.endLine)
        } catch (e: UnsupportedOperationException){
            listOf(0,0)
        } catch (e: IllegalStateException){
            listOf(0,0)
        }
    }

    open fun check(op: Operation<Action>): MutationOperator<T>? {
        return null
    }

    open fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<T>? {
        return null
    }

    open fun check(op1: Operation<Action>, op2: Operation<Action>, op3: Operation<Action>): MutationOperator<T>? {
        return null
    }
}

private fun calcRelativeLine(executable: CtExecutable<*>, nr: Int): Int {
    return if (nr == 0) 0
    else nr - executable.position.line
}
