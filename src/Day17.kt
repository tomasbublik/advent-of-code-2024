import readInput
import kotlin.math.pow

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

    class Debugger(var A: Long, var B: Long, var C: Long, val program: List<Long>) {
        var instructorPointer = 0L
        val output = mutableListOf<Long>()

        private fun intDivide(): Long {
            val numerator = A
            var denominator = getComboOperand()
            val denominatorLong = 2.0.pow(denominator.toDouble()).toLong()
            if (denominatorLong == 0L) {
                return 0L
            }
            return numerator / denominatorLong // Dělení Long čísly
        }

        private fun getComboOperand(): Long {
            val value = program[instructorPointer.toInt()]
            instructorPointer++
            return when (value) {
                in 0L..3L -> value
                4L -> A
                5L -> B
                6L -> C
                else -> throw Exception("Invalid argument")
            }
        }

        private fun getLiteral(): Long {
            val value = program[instructorPointer.toInt()]
            if (value !in 0..7) throw Exception("Invalid argument")
            instructorPointer++
            return value
        }

        fun adv() {
            A = intDivide()
        }

        fun bxl() {
            val literal = getLiteral()
            B = B xor literal.toLong()
        }

        fun bst() {
            val combo = getComboOperand()
            B = (combo % 8).toLong()
        }

        fun jnz() {
            val jump = A
            val nextIndex = getLiteral()
            if (jump != 0L) {
                instructorPointer = nextIndex
            }
        }

        fun bxc() {
            getLiteral() // increments pointer for legacy reasons
            B = B xor C
        }

        fun out() {
            val combo = getComboOperand()
            output.add(combo % 8)
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
                0L -> adv()
                1L -> bxl()
                2L -> bst()
                3L -> jnz()
                4L -> bxc()
                5L -> out()
                6L -> bdv()
                7L -> cdv()
                else -> throw Exception("Invalid instruction")
            }
        }

        fun halt(): Boolean = instructorPointer >= program.size
    }

    fun programStringToList(programStr: String): List<Long> =
        programStr.split(",").map { it.toLong() }

    fun runDebugger(a: Long, b: Long, c: Long, programStr: String): List<Long> { // Změna typu parametrů
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
        var valid = listOf(0L) // Změna typu listu

        for (length in 1..programLength) {
            val oldValid = valid
            valid = mutableListOf()
            for (num in oldValid) {
                for (offset in 0..7) {
                    val next = 8 * num + offset
                    val output = runDebugger(next, 0, 0, input)
                    val second = program.subList(programLength - length, programLength)
                    if (output == second) {
                        valid.add(next)
                    }
                }
            }
        }

        val answer = valid.minOrNull() ?: 0L
        println(answer)
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

    fun part2(input: List<String>): Int {
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

