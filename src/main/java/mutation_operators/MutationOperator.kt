package mutation_operators

import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.Operation

interface MutationOperator<T: MutationOperator<T>> {
    fun matches(opsSubList: List<Operation<Action>>): MutationOperator<T>?{
        return when(opsSubList.size){
            1 -> check(opsSubList[0])
            2 -> check(opsSubList[0], opsSubList[1])
            3 -> check(opsSubList[0], opsSubList[1], opsSubList[2])
            else -> null
        }
    }

    fun check(op: Operation<Action>): MutationOperator<T>? {
        return null
    }

    fun check(op1: Operation<Action>, op2: Operation<Action>): MutationOperator<T>? {
        return null
    }

    fun check(op1: Operation<Action>, op2: Operation<Action>, op3: Operation<Action>): MutationOperator<T>? {
        return null
    }
}
