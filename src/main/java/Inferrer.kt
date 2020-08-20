import com.github.gumtreediff.actions.model.Action
import gumtree.spoon.diff.operations.Operation
import mutation_operators.*

class Inferrer(val astDiff: ASTDiff) {
    private val mutOperators = setOf(
            ConstructorCallReplacementNull(),
            ArithmeticOperatorInsertion(),
            ReferenceReplacementContent(),
            VoidMethodDeletion(),
            NonVoidMethodDeletion(),
            TrueReturn(),
            FalseReturn(),
            ReturnValue(),
            ConstantReplacement(),
            RelationalOperatorReplacement(),
            ConditionalOperatorReplacement(),
            VarToVarReplacement(),
            StatementDeletion(),
            VarToConsReplacement(),
            UnaryOperatorInsertion(),
            ConditionalOperatorDeletion(),
            ArithmeticOperatorReplacement(),
            MemberVariableAssignmentDeletion(),
            AccessorModifierChange(),
            RemoveConditional(),
            UnaryOperatorReplacement(),
            ArithmeticOperatorDeletion(),
            ConsToVarReplacement(),
            ConditionalOperatorInsertion(),
            UnaryOperatorDeletion(),
            StaticModifierDeletion(),
            AccessorMethodChange(),
            BitshiftOperatorReplacement(),
            StaticModifierInsertion(),
            ArgumentTypeChange(),
            ArgumentNumberChange(astDiff),
            BitshiftOperatorDeletion(),
            BitwiseOperatorReplacement(),
            Negation()
    )

    fun infer(): List<MutationOperator<*>> {
        return traverse(astDiff.diff.rootOperations)
    }

    private fun traverse(modifications: List<Operation<Action>>): List<MutationOperator<*>>{
        return subLists(modifications)
                //.mapNotNull { matchOperators(it) }
                .flatMap { matchOperators(it) }
    }

    private fun subLists(modifications: List<Operation<Action>>): List<List<Operation<Action>>> {
        val subLists = mutableListOf<List<Operation<Action>>>()
        for(i in modifications.indices){
            subLists.add(listOf(modifications[i]))
            if(i < modifications.size-1){
                for(j in i+1..modifications.size-1){
                    subLists.add(listOf(modifications[i], modifications[j]))
                    if(j < modifications.size-1){
                        for(k in j+1..modifications.size-1){
                            subLists.add(listOf(modifications[i], modifications[j], modifications[k]))
                        }
                    }
                }

            }
        }
        return subLists
    }

    /*private fun matchOperators(opsSubList: List<Operation<Action>>): MutationOperator<*>? {
        return mutOperators.asSequence()
                .map { it.matches(opsSubList) }
                .find { it != null }
    }*/

    private fun matchOperators(opsSubList: List<Operation<Action>>): List<MutationOperator<*>> {
        return mutOperators.mapNotNull { it.matches(opsSubList) }
    }
}
