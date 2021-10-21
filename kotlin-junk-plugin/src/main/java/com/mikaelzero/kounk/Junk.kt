package com.mikaelzero.kounk

import com.squareup.kotlinpoet.*
import java.util.*
import kotlin.reflect.KClass

/**
 * @Author:         MikaelZero
 * @CreateDate:     2021/10/21 9:43 上午
 * @Description:
 */


enum class RandomType(val type: Int) {
    INT(0), STRING(1), DOUBLE(2), BOOLEAN(3)
}


fun TypeSpec.Builder.generateField(): TypeSpec.Builder {
    for (x in 0 until Random().nextInt(20)) {
        generateFieldSingle()
    }
    return generateFieldSingle()
}


private fun TypeSpec.Builder.generateFieldSingle(): TypeSpec.Builder {
    val type = Random().nextInt(RandomType.values().size)
    val android = PropertySpec.builder(getRandomString(), getRandomType(type))
        .addModifiers(KModifier.PRIVATE)
        .initializer(getRandomFormat(type), getRandomValue(type))
        .build()
    return addProperty(android)
}


fun getRandomFormat(type: Int): String {
    return when (type) {
        RandomType.INT.type, RandomType.DOUBLE.type, RandomType.BOOLEAN.type -> "%L"
        RandomType.STRING.type -> "%S"
        else -> "%S"
    }
}

fun getRandomType(type: Int): KClass<*> {
    return when (type) {
        RandomType.INT.type -> Int::class
        RandomType.DOUBLE.type -> Double::class
        RandomType.STRING.type -> String::class
        RandomType.BOOLEAN.type -> Boolean::class
        else -> String::class
    }
}

fun getRandomValue(type: Int): Any {
    return when (type) {
        RandomType.INT.type -> Random().nextInt(10 * 100)
        RandomType.DOUBLE.type -> Random().nextDouble()
        RandomType.STRING.type -> getRandomString()
        RandomType.BOOLEAN.type -> Random().nextInt(2) == 0
        else -> getRandomString()
    }
}

fun getRandomString(): String {
    val str = "abcdefghijklmnopqrstuvwxyz"
    val random = Random()
    val sb = StringBuffer()
    for (i in 0 until 15) {
        val number = random.nextInt(str.length)
        sb.append(str[number])
    }
    return sb.toString()
}


fun TypeSpec.Builder.generateFunction(): TypeSpec.Builder {
    for (x in 0 until Random().nextInt(30)) {
        generateFunctionSingle()
    }
    return generateFunctionSingle()
}

private fun TypeSpec.Builder.generateFunctionSingle(): TypeSpec.Builder {
    val type = Random().nextInt(5)
    return addFunction(
        when (type) {
            0 -> {
                FunSpec.builder(getRandomString())
                    .receiver(Int::class)
                    .returns(Int::class)
                    .addStatement("return if (this < 0) -this else this")
                    .build()
            }
            1 -> {
                val bPara = ParameterSpec.builder("b", Int::class)
                    .defaultValue("0")
                    .build()

                val print = "a + b = " + "$" + "{ a + b }"
                FunSpec.builder(getRandomString())
                    .addParameter("a", Int::class)
                    .addParameter(bPara)
                    .addStatement("print(%P)", print)
                    .build()
            }
            else -> {
                FunSpec.builder(getRandomString())
                    .returns(String::class)
                    .addStatement("return %P", getRandomString())
                    .build()
            }
        }
    )
}