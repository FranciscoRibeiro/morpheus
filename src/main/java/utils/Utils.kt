package utils

import spoon.reflect.code.*
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtField
import spoon.reflect.declaration.ModifierKind
import java.lang.NumberFormatException

val defaultValues = mapOf(Pair("boolean", false),
        Pair("int", 0),
        Pair("byte", 0),
        Pair("short", 0),
        Pair("long", 0),
        Pair("float", 0.0),
        Pair("double", 0.0),
        Pair("char", '\u0000'))

fun retrieveValue(elem: CtElement): Any? {
    val elemType = retrieveType(elem) ?: return null
    val elemString = elem.toString().replace("(", "").replace(")", "")
    return try {
        when (elemType) {
            "int" -> elemString.toInt()
            "byte" -> elemString.toByte()
            "short" -> elemString.toShort()
            "long" -> elemString.toLong()
            "float" -> elemString.toFloat()
            "double" -> elemString.toDouble()
            "boolean" -> elemString
            else -> elem.toString()
        }
    } catch (nfe: NumberFormatException) {
        //println("invalid number format: $elemString")
        elemString
    }
}

fun retrieveType(elem: CtElement): String? {
    return when (elem) {
        is CtLiteral<*> -> elem.type.simpleName
        is CtUnaryOperator<*> -> elem.type.simpleName
        is CtBinaryOperator<*> -> elem.type.simpleName
        is CtVariableRead<*> -> elem.variable.type.simpleName
        is CtInvocation<*> -> elem.executable.type.simpleName
        else -> null
    }
}

fun isRelational(kind: BinaryOperatorKind): Boolean {
    return kind.name == "EQ" || kind.name == "NE"
            || kind.name == "LT" || kind.name == "GT"
            || kind.name == "LE" || kind.name == "GE"
}

fun isArithmetic(kind: BinaryOperatorKind): Boolean {
    return kind.name == "PLUS" || kind.name == "MINUS" || kind.name == "MUL"
            || kind.name == "DIV" || kind.name == "MOD"
}

fun isConditional(kind: BinaryOperatorKind): Boolean {
    return kind.name == "OR" || kind.name == "AND"
}

fun isBitshift(kind: BinaryOperatorKind): Boolean {
    return kind.name == "SL" || kind.name == "SR" || kind.name == "USR"
}

fun isBitwise(kind: BinaryOperatorKind): Boolean {
    return kind.name == "BITOR" || kind.name == "BITXOR" || kind.name == "BITAND"
}

fun isTypeString(elem: CtExpression<*>): Boolean {
    return elem.type.simpleName == "String"
}

fun isDefaultValue(insSrc: CtLiteral<*>): Boolean {
    val insSrcParent = insSrc.parent
    return (insSrcParent is CtAssignment<*,*>
            && insSrc
            .value
            == defaultValues[
            insSrcParent
                    .getType()
                    .toString()])
}

fun isAccessor(kind: ModifierKind): Boolean {
    return kind == ModifierKind.PRIVATE || kind == ModifierKind.PROTECTED || kind == ModifierKind.PUBLIC
}

fun hasOneAccessor(elem: CtField<*>): Boolean {
    return elem.modifiers.size == 1 && isAccessor(elem.modifiers.first())
}

fun hasOneStatic(elem: CtField<*>): Boolean {
    return elem.modifiers.size == 1 && elem.modifiers.first() == ModifierKind.STATIC
}

fun areOpposite(kind1: UnaryOperatorKind, kind2: UnaryOperatorKind): Boolean {
    return when {
        (kind1 == UnaryOperatorKind.POSTDEC && kind2 == UnaryOperatorKind.POSTINC)
                || (kind1 == UnaryOperatorKind.POSTINC && kind2 == UnaryOperatorKind.POSTDEC)
                || (kind1 == UnaryOperatorKind.PREDEC && kind2 == UnaryOperatorKind.PREINC)
                || (kind1 == UnaryOperatorKind.PREINC && kind2 == UnaryOperatorKind.PREDEC) -> true
        else -> false
    }
}

fun isPlusOrMinus(kind: UnaryOperatorKind): Boolean {
    return kind == UnaryOperatorKind.NEG || kind == UnaryOperatorKind.POS
}

fun isSignedConstant(unaryOp: CtUnaryOperator<*>): Boolean {
    return isPlusOrMinus(unaryOp.kind) && unaryOp.operand is CtLiteral<*>
}

fun isPartOf(node: CtElement, container: CtExpression<*>): Boolean {
    if (node === container) return true
    else if (container is CtBinaryOperator<*>){
        return isPartOf(node, container.leftHandOperand) || isPartOf(node, container.rightHandOperand)
    }
    else return false
}
