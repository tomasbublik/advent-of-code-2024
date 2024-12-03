import java.util.regex.Pattern

fun main() {

    fun part1(input: List<String>): Int {
        var totalSum = 0

        val regex = "mul\\((\\d{1,3}),(\\d{1,3})\\)"
        val pattern = Pattern.compile(regex)

        for (inputLine in input) {
            val matcher = pattern.matcher(inputLine)

            while (matcher.find()) {
                val x = matcher.group(1).toInt()
                val y = matcher.group(2).toInt()
                val product = x * y
                totalSum += product
                println("Found mul($x,$y) in \"$inputLine\": $x * $y = $product")
            }
        }

        println("Total sum: $totalSum")

        return totalSum
    }

    fun part2(input: List<String>): Int {
        // Combine the patterns
        val combinedRegex = "(mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\))"
        val pattern = Pattern.compile(combinedRegex)


        var totalSum = 0
        var mulEnabled = true // mul instructions are enabled at the start

        for (inputLine in input) {
            val matcher = pattern.matcher(inputLine)
            while (matcher.find()) {
                val match = matcher.group()
                when {
                    match == "do()" -> {
                        mulEnabled = true
                        println("Found do(); mul instructions are now ENABLED")
                    }

                    match == "don't()" -> {
                        mulEnabled = false
                        println("Found don't(); mul instructions are now DISABLED")
                    }

                    match.startsWith("mul") && mulEnabled -> {
                        val x = matcher.group(2).toInt()
                        val y = matcher.group(3).toInt()
                        val product = x * y
                        totalSum += product
                        println("Found mul($x,$y): $x * $y = $product (ENABLED)")
                    }

                    match.startsWith("mul") && !mulEnabled -> {
                        val x = matcher.group(2)
                        val y = matcher.group(3)
                        println("Found mul($x,$y): Skipped (DISABLED)")
                    }
                }
            }
        }

        println("Total sum: $totalSum")
        return totalSum
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 161)
    // Part 1 test and part 2 test are different this time?
    check(part2(listOf("xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))")) == 48)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
