package cz.tomasbublik

import readInput

enum class Direction(val deltaRow: Int, val deltaCol: Int) {
    UP(-1, 0),
    RIGHT(0, 1),
    DOWN(1, 0),
    LEFT(0, -1);

    // Function to turn right (clockwise)
    fun turnRight(): Direction {
        return when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }

    companion object {
        fun fromChar(ch: Char): Direction? = when (ch) {
            '^' -> UP
            '>' -> RIGHT
            'v' -> DOWN
            '<' -> LEFT
            else -> null
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        // Convert the grid input into a 2D array of characters
        val grid = input.map { it.toCharArray() }
        val numRows = grid.size
        val numCols = grid[0].size

        // Find the starting position and initial direction of the guard
        var guardRow = -1
        var guardCol = -1
        var guardDirection: Direction? = null

        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                when (grid[row][col]) {
                    '^' -> {
                        guardRow = row
                        guardCol = col
                        guardDirection = Direction.UP
                    }

                    'v' -> {
                        guardRow = row
                        guardCol = col
                        guardDirection = Direction.DOWN
                    }

                    '>' -> {
                        guardRow = row
                        guardCol = col
                        guardDirection = Direction.RIGHT
                    }

                    '<' -> {
                        guardRow = row
                        guardCol = col
                        guardDirection = Direction.LEFT
                    }
                }
                if (guardDirection != null) break
            }
            if (guardDirection != null) break
        }

        // If guard not found, exit
        if (guardDirection == null) {
            println("Error: Guard starting position not found in the grid.")
            return 0
        }

        // Initialize a set to track visited positions
        val visitedPositions = mutableSetOf<Pair<Int, Int>>()
        visitedPositions.add(Pair(guardRow, guardCol))

        // Function to check if a position is within grid bounds
        fun isWithinBounds(row: Int, col: Int): Boolean {
            return row in 0 until numRows && col in 0 until numCols
        }

        // Simulation loop
        while (true) {
            // Calculate the position directly in front of the guard
            val nextRow = guardRow + guardDirection!!.deltaRow
            val nextCol = guardCol + guardDirection.deltaCol

            if (!isWithinBounds(nextRow, nextCol)) {
                // Guard moves out of the grid
                break
            }

            if (grid[nextRow][nextCol] == '#') {
                // Obstacle detected, turn right
                guardDirection = guardDirection.turnRight()
            } else {
                // Move forward
                guardRow = nextRow
                guardCol = nextCol
                visitedPositions.add(Pair(guardRow, guardCol))
            }
        }

        // Output the result
        println("The guard visited ${visitedPositions.size} distinct positions before leaving the area.")

        return visitedPositions.size
    }

    fun part2(input: List<String>): Int {
        val grid = input.map { it.toCharArray() }
        val numRows = grid.size
        val numCols = grid[0].size

        // Find guard's starting position and direction
        var guardRow = -1
        var guardCol = -1
        var guardDir: Direction? = null

        outer@ for (r in 0 until numRows) {
            for (c in 0 until numCols) {
                val ch = grid[r][c]
                val dir = Direction.fromChar(ch)
                if (dir != null) {
                    guardRow = r
                    guardCol = c
                    guardDir = dir
                    break@outer
                }
            }
        }

        if (guardDir == null) {
            println("Guard starting position not found.")
            return 0
        }

        // The guard's starting cell might contain '^', '>', 'v', or '<'. Replace it with '.' for simulation.
        val originalStartChar = grid[guardRow][guardCol]
        grid[guardRow][guardCol] = '.'

        // Helper functions
        fun isWithinBounds(r: Int, c: Int) = r in 0 until numRows && c in 0 until numCols

        // Simulate the guard's movement. Return true if it results in a loop, false if the guard leaves the grid.
        fun simulateWithGrid(startR: Int, startC: Int, startDir: Direction): Boolean {
            var r = startR
            var c = startC
            var dir = startDir

            val visitedStates = mutableSetOf<Triple<Int, Int, Direction>>()
            visitedStates.add(Triple(r, c, dir))

            while (true) {
                val frontR = r + dir.deltaRow
                val frontC = c + dir.deltaCol

                // Check if guard is about to leave the grid
                if (!isWithinBounds(frontR, frontC)) {
                    // Guard leaves the area - no loop
                    return false
                }

                if (grid[frontR][frontC] == '#') {
                    // Obstacle ahead, turn right
                    dir = dir.turnRight()
                } else {
                    // Move forward
                    r = frontR
                    c = frontC
                }

                val state = Triple(r, c, dir)
                if (state in visitedStates) {
                    // Loop detected
                    return true
                }
                visitedStates.add(state)
            }
        }

        // Now we try placing an obstruction in each possible position and see if it causes a loop.
        var loopCount = 0

        for (r in 0 until numRows) {
            for (c in 0 until numCols) {
                if (r == guardRow && c == guardCol) {
                    // Can't place obstruction at guard's starting position
                    continue
                }
                if (grid[r][c] == '.') {
                    // Temporarily place an obstruction
                    grid[r][c] = '#'

                    // Simulate
                    val causesLoop = simulateWithGrid(guardRow, guardCol, guardDir!!)
                    if (causesLoop) {
                        loopCount++
                    }

                    // Restore cell
                    grid[r][c] = '.'
                }
            }
        }

        // Restore the guard's starting char if needed
        grid[guardRow][guardCol] = originalStartChar

        println("Number of positions that cause a loop when obstructed: $loopCount")

        return loopCount
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 41)
    check(part2(testInput) == 6)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
