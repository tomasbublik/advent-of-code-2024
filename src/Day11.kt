import readInput
import java.math.BigInteger

fun main() {

    fun part1(input: List<String>): Int {
        val stones = input.firstOrNull()?.split(" ") ?: emptyList()

        val times = 25
        var current = stones

        repeat(times) {
            val newStones = mutableListOf<String>()
            for (s in current) {
                if (s == "0") {
// Rule 1
                    newStones.add("1")
                } else {
                    val length = s.length
                    if (length % 2 == 0) {
// Rule 2: even number of digits => split into two halves
                        val mid = length / 2
                        val leftPart = s.substring(0, mid).trimStart('0')
                        val rightPart = s.substring(mid).trimStart('0')
// If trimStart('0') empties the string, we must put "0"
                        newStones.add(if (leftPart.isEmpty()) "0" else leftPart)
                        newStones.add(if (rightPart.isEmpty()) "0" else rightPart)
                    } else {
// Rule 3: multiply by 2024
// s != "0" and has an odd number of digits
// Perform multiplication as BigInteger
                        val num = BigInteger(s)
                        val product = num.multiply(BigInteger("2024"))
                        newStones.add(product.toString())
                    }
                }
            }
            current = newStones
        }

        println(current.size)
        return current.size
    }

    fun isEvenDigitCount(num: String): Boolean {
        return num.length % 2 == 0
    }

    fun splitEven(num: String): Pair<String, String> {
        val half = num.length / 2
        val left = num.substring(0, half)
        var right = num.substring(half)

// Remove leading zeros from the right part
        right = right.trimStart('0')
        if (right.isEmpty()) {
            right = "0"
        }

        return Pair(left, right)
    }

    fun countStonesAfter(n: Int, stone: String, memo: MutableMap<Pair<String, Int>, Long>): Long {
        if (n == 0) return 1L

        val key = Pair(stone, n)
        memo[key]?.let { return it }

        val result = when {
            stone == "0" -> {
// Rule: 0 -> 1
                countStonesAfter(n - 1, "1", memo)
            }

            isEvenDigitCount(stone) -> {
// Even number of digits -> split into two halves
                val (leftPart, rightPart) = splitEven(stone)
                countStonesAfter(n - 1, leftPart, memo) + countStonesAfter(n - 1, rightPart, memo)
            }

            else -> {
                // Násobíme 2024
                val bigVal = BigInteger(stone)
                val multiplied = bigVal.multiply(BigInteger("2024")).toString()
                countStonesAfter(n - 1, multiplied, memo)
            }
        }

        memo[key] = result
        return result
    }

    fun part2(input: List<String>): Long {
        val initialStones = input.firstOrNull()?.split(" ") ?: emptyList()

        val memo = HashMap<Pair<String, Int>, Long>()
        val totalStones = initialStones.fold(0L) { acc, stone ->
            acc + countStonesAfter(75, stone, memo)
        }
        println(totalStones)
        return totalStones
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 55312)
//    check(part2(testInput) == BigInteger.valueOf(81))

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
