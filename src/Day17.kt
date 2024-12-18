package cz.tomasbublik

import readInput


fun main() {

    fun runProgramA(
        input: List<String>,
        initA: Int,
        initB: Int,
        initC: Int
    ): String {
        var A = initA
        var B = initB
        var C = initC

        val program = input.map { it.toInt() }

        fun comboValue(x: Int): Int {
            return when (x) {
                in 0..3 -> x
                4 -> A
                5 -> B
                6 -> C
                7 -> error("Invalid combo operand 7 encountered")
                else -> error("Invalid combo operand $x")
            }
        }

        val output = mutableListOf<Int>()

        var ip = 0

        while (ip < program.size) {
            val opcode = program[ip]
            val operand = if (ip + 1 < program.size) program[ip + 1] else 0

            when (opcode) {
                0 -> {
                    // adv: A = floor(A / 2^(comboValue(operand)))
                    val denom = 1 shl comboValue(operand)
                    A = A / denom
                    ip += 2
                }

                1 -> {
                    // bxl: B = B XOR operand (operand je literal)
                    B = B xor operand
                    ip += 2
                }

                2 -> {
                    // bst: B = comboValue(operand) % 8
                    B = comboValue(operand) % 8
                    ip += 2
                }

                3 -> {
                    // jnz: if A != 0 then ip = operand (operand je literal), else ip += 2
                    if (A != 0) {
                        ip = operand
                    } else {
                        ip += 2
                    }
                }

                4 -> {
                    // bxc: B = B XOR C (operand ignorován)
                    B = B xor C
                    ip += 2
                }

                5 -> {
                    // out: output comboValue(operand) % 8
                    output.add(comboValue(operand) % 8)
                    ip += 2
                }

                6 -> {
                    // bdv: B = floor(A / 2^(comboValue(operand)))
                    val denom = 1 shl comboValue(operand)
                    B = A / denom
                    ip += 2
                }

                7 -> {
                    // cdv: C = floor(A / 2^(comboValue(operand)))
                    val denom = 1 shl comboValue(operand)
                    C = A / denom
                    ip += 2
                }

                else -> {
                    // Neplatný opcode - konec
                    break
                }
            }
        }

        return output.joinToString(",")
    }

    fun part1(input: List<String>): Int {

//        val input = listOf("0","1","5","4","3","0")
        val input = listOf("2", "4", "1", "6", "7", "5", "4", "6", "1", "4", "5", "5", "0", "3", "3", "0")

        // Počáteční hodnoty registrů (ukázka, v praxi je lze načíst odkudkoliv)
        val A = 66171486
        val B = 0
        val C = 0

        val result = runProgramA(input, A, B, C)
        println(result) // Vytiskne výsledek programu, např. "4,6,3,5,6,3,5,2,1,0"
        return 0
    }

    class Debugger(var A: Int, var B: Int, var C: Int, val program: List<Int>) {
        var instructorPointer = 0
        val output = mutableListOf<Int>()

        fun getComboOperand(): Int {
            val value = program[instructorPointer]
            return when {
                value in 0..3 -> {
                    instructorPointer++
                    value
                }

                value == 4 -> {
                    instructorPointer++
                    A
                }

                value == 5 -> {
                    instructorPointer++
                    B
                }

                value == 6 -> {
                    instructorPointer++
                    C
                }

                else -> {
                    throw Exception("Invalid argument")
                }
            }
        }

        fun getLiteral(): Int {
            val value = program[instructorPointer]
            if (value < -1 || value > 7) throw Exception("Invalid argument")
            instructorPointer++
            return value
        }

        fun intDivide(): Int {
            val numerator = A
            val denominatorVal = getComboOperand()
            val denominator = 1 shl denominatorVal
            val result = numerator / denominator
            return result
        }

        fun adv() {
            val valRes = intDivide()
            A = valRes
        }

        fun bxl() {
            val currentB = B
            val literal = getLiteral()
            val result = currentB xor literal
            B = result
        }

        fun bst() {
            val combo = getComboOperand()
            val result = combo % 8
            B = result
        }

        fun jnz() {
            val jump = A
            val nextIndex = getLiteral()
            if (jump != 0) {
                instructorPointer = nextIndex
            }
        }

        fun bxc() {
            val result = B xor C
            getLiteral() // increments pointer for legacy reasons
            B = result
        }

        fun out() {
            val combo = getComboOperand()
            val result = combo % 8
            output.add(result)
        }

        fun bdv() {
            B = intDivide()
        }

        fun cdv() {
            C = intDivide()
        }

        fun doInstruction() {
            val instruction = getLiteral()
            when (instruction) {
                0 -> adv()
                1 -> bxl()
                2 -> bst()
                3 -> jnz()
                4 -> bxc()
                5 -> out()
                6 -> bdv()
                7 -> cdv()
            }
        }

        fun printout(): String {
            return output.joinToString(",")
        }

        fun halt(): Boolean {
            if (instructorPointer >= program.size) {
                return true
            }
            return false
        }
    }

    fun programStringToList(programStr: String): List<Int> {
        return programStr.split(",").map { it.trim().toInt() }
    }

    fun runDebugger(a: Int, b: Int, c: Int, programStr: String): List<Int> {
        val program = programStringToList(programStr)
        val debugger = Debugger(a, b, c, program)
        while (!debugger.halt()) {
            debugger.doInstruction()
        }
        return debugger.output
    }

    fun reverseDebugger(input: String) {
        val program = programStringToList(input)
        val programLength = program.size
        var valid = listOf(0)
        for (length in 1..programLength) {
            val oldvalid = valid
            val newvalid = mutableListOf<Int>()
            for (num in oldvalid) {
                for (offset in 0 until 8) {
                    val next = 8 * num + offset
                    val output = runDebugger(next, 0, 0, input)
                    val second = program.takeLast(length)
                    if (output == second) {
                        newvalid.add(next)
                    }
                }
            }
            valid = newvalid
        }
        val answer = valid.minOrNull() ?: throw Exception("No valid found")
        println(answer)
    }

    fun part2(input: List<String>): Int {
        println(runDebugger(729, 0, 0, "0,1,5,4,3,0"))
        reverseDebugger("2,4,1,6,7,5,4,6,1,4,5,5,0,3,3,0")

        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 0)
    check(part2(testInput) == 0)

    val input = readInput("Day17")
    println(part1(input))
    println(part2(input))
}

