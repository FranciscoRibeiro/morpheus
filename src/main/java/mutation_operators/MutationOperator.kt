package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.Operation

abstract class MutationOperator<T: MutationOperator<T>> {
    var startLine: Int = 0
    var endLine: Int = 0
    var startColumn = 0
    var endColumn = 0

    fun matches(opsSubList: List<Operation<Action>>): MutationOperator<T>?{
        val mutOp = when(opsSubList.size){
            1 -> check(opsSubList[0])
            2 -> check(opsSubList[0], opsSubList[1])
            3 -> check(opsSubList[0], opsSubList[1], opsSubList[2])
            else -> null
        }
        if(mutOp != null){
            val startEndLineList = opsSubList.flatMap { startAndEndLine(it) }
            val startEndColumnList = opsSubList.flatMap { startAndEndColumn(it) }
            mutOp.startLine = startEndLineList.min() ?: 0
            mutOp.endLine = startEndLineList.max() ?: 0
            mutOp.startColumn = startEndColumnList.min() ?: 0
            mutOp.endColumn = startEndColumnList.max() ?: 0
        }
        return mutOp
    }

    private fun startAndEndColumn(op: Operation<Action>): List<Int> {
        val position = op.srcNode.position
        return try {
            listOf(position.column, position.endColumn)
        }catch (e: UnsupportedOperationException){
            listOf(0,0)
        }
    }

    private fun startAndEndLine(op: Operation<Action>): List<Int> {
        val position = op.srcNode.position
        return try {
            listOf(position.line, position.endLine)
        }catch (e: UnsupportedOperationException){
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
