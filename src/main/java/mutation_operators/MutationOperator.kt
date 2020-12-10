package mutation_operators

import ASTDiff
import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.Operation
import spoon.reflect.cu.SourcePosition

abstract class MutationOperator<T: MutationOperator<T>> {
    var oldStartLine: Int = 0
    var oldEndLine: Int = 0
    var oldStartColumn = 0
    var oldEndColumn = 0
    var newStartLine: Int = 0
    var newEndLine: Int = 0
    var newStartColumn = 0
    var newEndColumn = 0

    fun matches(opsSubList: List<Operation<Action>>, astDiff: ASTDiff): MutationOperator<T>? {
        val mutOp = when(opsSubList.size){
            1 -> check(opsSubList[0])
            2 -> check(opsSubList[0], opsSubList[1])
            3 -> check(opsSubList[0], opsSubList[1], opsSubList[2])
            else -> null
        }
        if(mutOp != null){
            val oldStartEndLineList = opsSubList.flatMap { oldStartAndEndLine(it) }
            val oldStartEndColumnList = opsSubList.flatMap { oldStartAndEndColumn(it) }
            val newStartEndLineList = opsSubList.flatMap { newStartAndEndLine(it, astDiff) }
            val newStartEndColumnList = opsSubList.flatMap { newStartAndEndColumn(it, astDiff) }
            mutOp.oldStartLine = oldStartEndLineList.min() ?: 0
            mutOp.oldEndLine = oldStartEndLineList.max() ?: 0
            mutOp.oldStartColumn = oldStartEndColumnList.min() ?: 0
            mutOp.oldEndColumn = oldStartEndColumnList.max() ?: 0
            mutOp.newStartLine = newStartEndLineList.min() ?: 0
            mutOp.newEndLine = newStartEndLineList.max() ?: 0
            mutOp.newStartColumn = newStartEndColumnList.min() ?: 0
            mutOp.newEndColumn = newStartEndColumnList.max() ?: 0
        }
        return mutOp
    }

    private fun newStartAndEndColumn(op: Operation<Action>, astDiff: ASTDiff): List<Int> {
        var position: SourcePosition
        return try {
            position = op.dstNode.position
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
            val position = op.srcNode.position
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
            position = op.dstNode.position
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
            val position = op.srcNode.position
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
