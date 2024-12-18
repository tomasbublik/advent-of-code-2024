fun main() {

    fun searchFromPosition(
        grid: List<CharArray>,
        word: String,
        startRow: Int,
        startCol: Int,
        deltaRow: Int,
        deltaCol: Int
    ): Boolean {
        val numRows = grid.size
        val numCols = grid[0].size
        val wordLength = word.length

        var row = startRow
        var col = startCol

        for (index in 0 until wordLength) {
            // Check if position is within bounds
            if (row !in 0 until numRows || col !in 0 until numCols) {
                return false
            }

            // Check if character matches
            if (grid[row][col] != word[index]) {
                return false
            }

            // Move to next character position
            row += deltaRow
            col += deltaCol
        }

        // All characters matched
        return true
    }

    fun countWordOccurrences(grid: List<CharArray>, word: String): Int {
        val numRows = grid.size
        val numCols = grid[0].size
        val wordLength = word.length
        var count = 0

        // Directions represented as (deltaRow, deltaCol)
        val directions = listOf(
            Pair(-1, 0),   // North
            Pair(-1, 1),   // North-East
            Pair(0, 1),    // East
            Pair(1, 1),    // South-East
            Pair(1, 0),    // South
            Pair(1, -1),   // South-West
            Pair(0, -1),   // West
            Pair(-1, -1)   // North-West
        )

        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                for ((deltaRow, deltaCol) in directions) {
                    if (searchFromPosition(grid, word, row, col, deltaRow, deltaCol)) {
                        count++
                    }
                }
            }
        }

        return count
    }

    fun isXMasAtPosition(
        grid: List<CharArray>,
        centerRow: Int,
        centerCol: Int,
        diag1Seq: String,
        diag2Seq: String
    ): Boolean {
        val numRows = grid.size
        val numCols = grid[0].size

        // Offsets for the diagonals
        val diag1Offsets = listOf(Pair(-1, -1), Pair(0, 0), Pair(1, 1))
        val diag2Offsets = listOf(Pair(-1, 1), Pair(0, 0), Pair(1, -1))

        // Check first diagonal
        for (k in diag1Offsets.indices) {
            val newRow = centerRow + diag1Offsets[k].first
            val newCol = centerCol + diag1Offsets[k].second
            if (newRow !in 0 until numRows || newCol !in 0 until numCols) {
                return false
            }
            if (grid[newRow][newCol] != diag1Seq[k]) {
                return false
            }
        }

        // Check second diagonal
        for (k in diag2Offsets.indices) {
            val newRow = centerRow + diag2Offsets[k].first
            val newCol = centerCol + diag2Offsets[k].second
            if (newRow !in 0 until numRows || newCol !in 0 until numCols) {
                return false
            }
            if (grid[newRow][newCol] != diag2Seq[k]) {
                return false
            }
        }

        // Both diagonals match
        return true
    }


    fun countXMasOccurrences(grid: List<CharArray>): Int {
        val numRows = grid.size
        val numCols = grid[0].size
        var count = 0

        // Possible sequences for MAS and SAM
        val sequences = listOf("MAS", "SAM")

        // Iterate over each cell in the grid
        for (i in 0 until numRows) {
            for (j in 0 until numCols) {
                // Check if the current cell can be the center of an X-MAS (must be 'A')
                if (grid[i][j] == 'A') {
                    // Check all combinations of sequences for both diagonals
                    for (seq1 in sequences) {
                        for (seq2 in sequences) {
                            if (isXMasAtPosition(grid, i, j, seq1, seq2)) {
                                count++
                            }
                        }
                    }
                }
            }
        }

        return count
    }

    fun part1(input: List<String>): Int {
        val grid = input.map { it.toCharArray() }
        val word = "XMAS"

        val count = countWordOccurrences(grid, word)
        println("The word '$word' appears $count times in the grid.")

        return count
    }

    fun part2(input: List<String>): Int {
        val grid = input.map { it.toCharArray() }
        val count = countXMasOccurrences(grid)
        println("An X-MAS appears $count times in the grid.")

        return count
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day04_test")

    check(part1(testInput) == 18)
    check(
        part2(
            listOf(
                "MMMSXXMASM",
                "MSAMXMSMSA",
                "AMXSXMAAMM",
                "MSAMASMSMX",
                "XMASAMXAMM",
                "XXAMMXXAMA",
                "SMSMSASXSS",
                "SAXAMASAAA",
                "MAMMMXMMMM",
                "MXMXAXMASX"
            )
        ) == 9
    )

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day04")

    println(part1(input))
    println(part2(input))
}
