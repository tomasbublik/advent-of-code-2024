fun main() {
    fun listPair(input: List<String>): Pair<MutableList<Int>, MutableList<Int>> {
        val leftList = mutableListOf<Int>()
        val rightList = mutableListOf<Int>()

        // Parse each line and extract the two numbers
        for (line in input) {
            val parts = line.trim().split("\\s+".toRegex())
            if (parts.size == 2) {
                val leftNum = parts[0].toInt()
                val rightNum = parts[1].toInt()
                leftList.add(leftNum)
                rightList.add(rightNum)
            }
        }
        return Pair(leftList, rightList)
    }

    fun part1(input: List<String>): Int {
        val (leftList, rightList) = listPair(input)

        // Sort both lists
        leftList.sort()
        rightList.sort()

        // Calculate the total distance
        var totalDistance = 0
        for (i in leftList.indices) {
            val distance = kotlin.math.abs(leftList[i] - rightList[i])
            totalDistance += distance
        }

        return totalDistance
    }

    fun part2(input: List<String>): Int {
        val (leftList, rightList) = listPair(input)

        // Build a frequency map for numbers in the right list
        val rightFrequency = rightList.groupingBy { it }.eachCount()

        // Calculate the similarity score
        var similarityScore = 0
        for (num in leftList) {
            val countInRight = rightFrequency.getOrDefault(num, 0)
            similarityScore += num * countInRight
        }

        return similarityScore
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
