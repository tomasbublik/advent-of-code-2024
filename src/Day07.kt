package cz.tomasbublik

import readInput
import java.math.BigInteger

fun main() {

    // Parse the input
    data class Equation(val target: Long, val numbers: List<Long>)

    data class EquationBig(val target: BigInteger, val numbers: List<BigInteger>)

    // Possible operators
    val operators = listOf("+", "*", "||")

    // Function to evaluate a given combination of operators on the numbers
    fun evaluate(numbers: List<Long>, ops: List<Char>): Long {
        var result = numbers[0]
        for (i in ops.indices) {
            val nextNum = numbers[i + 1]
            when (ops[i]) {
                '+' -> result += nextNum
                '*' -> result *= nextNum
            }
        }
        return result
    }

    fun canSolve(equation: Equation): Boolean {
        val nums = equation.numbers
        if (nums.size == 1) {
            return nums[0] == equation.target
        }

        val numOps = nums.size - 1
        val totalCombinations = 1 shl numOps
        for (mask in 0 until totalCombinations) {
            val ops = mutableListOf<Char>()
            for (bit in 0 until numOps) {
                // Bit -> operator selection; '+' or '*'
                ops.add(if ((mask shr bit) and 1 == 1) '*' else '+')
            }

            val value = evaluate(nums, ops)
            if (value == equation.target) {
                return true
            }
        }
        return false
    }

    fun evaluateBig(numbers: List<BigInteger>, ops: List<String>): BigInteger {
        var result = numbers[0]
        for (i in ops.indices) {
            val nextNum = numbers[i + 1]
            result = when (ops[i]) {
                "+" -> result.add(nextNum)
                "*" -> result.multiply(nextNum)
                "||" -> (result.toString() + nextNum.toString()).toBigInteger()
                else -> throw IllegalStateException("Unknown operator: ${ops[i]}")
            }
        }
        return result
    }

    fun canSolveBig(equation: EquationBig): Boolean {
        val nums = equation.numbers
        val target = equation.target

        if (nums.size == 1) {
            return nums[0] == target
        }

        val numOps = nums.size - 1
        // Počet kombinací: 3^(numOps)
        val totalCombinations = BigInteger.valueOf(3L).pow(numOps)
        val combCount = totalCombinations.toLong() // Předpokládáme, že se vejde do long

        for (mask in 0 until combCount) {
            var temp = mask
            val ops = mutableListOf<String>()
            for (bit in 0 until numOps) {
                val opIndex = (temp % 3).toInt()
                ops.add(operators[opIndex])
                temp /= 3
            }

            val value = evaluateBig(nums, ops)
            if (value == target) {
                return true
            }
        }

        return false
    }
    fun part1(input: List<String>): Int {

        val equations = input.map { line ->
            val parts = line.split(":")
            val target = parts[0].trim().toLong()
            val nums = parts[1].trim().split(" ").map { it.toLong() }
            Equation(target, nums)
        }

        var totalSum = 0L

        for (eq in equations) {
            if (canSolve(eq)) {
                totalSum += eq.target
            }
        }

        println("Total sum: $totalSum") // Should print 3749 for the given example
        return totalSum.toInt()
    }

    fun part2(input: List<String>): Int {

        val equations = input.map { line ->
            val parts = line.split(":")
            val target = parts[0].trim().toBigInteger()
            val nums = parts[1].trim().split(" ").map { it.toBigInteger() }
            EquationBig(target, nums)
        }

        var totalSum = BigInteger.ZERO

        for (eq in equations) {
            if (canSolveBig(eq)) {
                totalSum += eq.target
            }
        }

        println("Total sum: $totalSum") // Should print 11387 for the given example

        return totalSum.toInt()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 3749)
    check(part2(testInput) == 11387)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
