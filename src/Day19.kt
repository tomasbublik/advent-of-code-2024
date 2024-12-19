fun main() {
    fun canFormDesign(design: String, towelPatterns: Set<String>): Boolean {
        val n = design.length
        val dp = BooleanArray(n + 1) { false }
        dp[0] = true // Prázdný design lze vytvořit bez použití vzorů

        for (i in 1..n) {
            for (pattern in towelPatterns) {
                val len = pattern.length
                if (len <= i && design.substring(i - len, i) == pattern && dp[i - len]) {
                    dp[i] = true
                    break // Najdeme alespoň jeden způsob, jak vytvořit tuto část, pokračujeme dál
                }
            }
        }

        return dp[n]
    }

    fun countPossibleDesigns(input: List<String>): Int {
        if (input.isEmpty()) return 0

        // Najdeme index prázdného řádku, který odděluje vzory od designů
        val separatorIndex = input.indexOfFirst { it.trim().isEmpty() }
        if (separatorIndex == -1) return 0 // Pokud není prázdný řádek, vstup je neplatný

        val towelPatterns = input[0].split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()

        val desiredDesigns = if (separatorIndex + 1 < input.size) {
            input.subList(separatorIndex + 1, input.size).map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            emptyList()
        }

        var count = 0

        for (design in desiredDesigns) {
            if (canFormDesign(design, towelPatterns)) {
                count++
            }
        }

        return count
    }

    fun part1(input: List<String>): Int {
        val result = countPossibleDesigns(input)
        println(result)
        return result
    }

    fun part2(input: List<String>): Int {

        return 0
    }

    // Or read a large test input from the `src/Day19_test.txt` file:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 6)
    check(part2(testInput) == 0)

    // Read the input from the `src/Day19.txt` file.
    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}
