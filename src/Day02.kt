fun main() {

    fun determineTrend(firstDifference: Int): String? {
        return when {
            firstDifference > 0 -> "increasing"
            firstDifference < 0 -> "decreasing"
            else -> null // Zároveň platné úrovně, tedy nebezpečné
        }
    }

    fun calculateDifference(current: Int, next: Int) = next - current

    fun isDifferenceInAllowedRange(difference: Int): Boolean {
        val allowedRange = 1..3
        val absoluteDifference = kotlin.math.abs(difference)
        return absoluteDifference in allowedRange
    }

    fun isTrendConsistent(trend: String, difference: Int): Boolean {
        return when (trend) {
            "increasing" -> difference > 0
            "decreasing" -> difference < 0
            else -> false
        }
    }

    fun isReportSafe(levels: List<Int>): Boolean {
        if (levels.size < 2) return false // Nedostatečný počet úrovní pro určení trendu

        val firstDifference = levels[1] - levels[0]
        val trend = determineTrend(firstDifference) ?: return false

        for (index in 0 until levels.size - 1) {
            val difference = calculateDifference(levels[index], levels[index + 1])

            if (!isDifferenceInAllowedRange(difference)) return false
            if (!isTrendConsistent(trend, difference)) return false
        }
        return true
    }

    fun isReportSafeWithRemoval(levels: List<Int>): Boolean {
        // First, check if the original report is safe
        if (isReportSafe(levels)) {
            return true
        }

        // Try removing one level at each position and check if the result is safe
        for (i in levels.indices) {
            val modifiedLevels = levels.toMutableList()
            modifiedLevels.removeAt(i)

            if (isReportSafe(modifiedLevels)) {
                return true
            }
        }

        // If no removal results in a safe report, return false
        return false
    }

    fun part1(input: List<String>): Int {
        val reports = input.map { line ->
            line.trim().split("\\s+".toRegex()).map { it.toInt() }
        }

        val safeReportsCount = reports.count { isReportSafe(it) }

        println("Number of safe reports: $safeReportsCount")

        return safeReportsCount
    }

    fun part2(input: List<String>): Int {
        val reports = input.map { line ->
            line.trim().split("\\s+".toRegex()).map { it.toInt() }
        }

        val safeReportsCount = reports.count { isReportSafeWithRemoval(it) }

        println("Number of safe reports: $safeReportsCount")
        return safeReportsCount
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
