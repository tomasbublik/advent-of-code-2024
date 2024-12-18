package cz.tomasbublik

import readInput
import java.util.*

fun main() {

    fun part1(input: List<String>): Long {

        val numRows = input.size
        val numCols = if (numRows > 0) input[0].length else 0

        val grid = Array(numRows) { r -> input[r].toCharArray() }

        val visited = Array(numRows) { BooleanArray(numCols) }

        // 4 directions
        val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        fun inBounds(r: Int, c: Int) = r in 0 until numRows && c in 0 until numCols

        // DFS to identify a region (component)
        fun dfs(startR: Int, startC: Int): Pair<Int, Int> {
            val stack = mutableListOf(startR to startC)
            val letter = grid[startR][startC]
            var area = 0
            var perimeter = 0
            val cells = mutableListOf<Pair<Int, Int>>()

            visited[startR][startC] = true
            while (stack.isNotEmpty()) {
                val (r, c) = stack.removeLast()
                area++
                cells.add(r to c)
                // Count contribution to the perimeter
                // For each edge where there is no neighbor of the same region, +1
                for ((dr, dc) in directions) {
                    val nr = r + dr
                    val nc = c + dc
                    if (!inBounds(nr, nc) || grid[nr][nc] != letter) {
                        // Edge, add 1 to the perimeter
                        perimeter++
                    } else {
                        // A neighbor of the same letter and is within the map
                        if (!visited[nr][nc] && grid[nr][nc] == letter) {
                            visited[nr][nc] = true
                            stack.add(nr to nc)
                        }
                    }
                }
            }

            return Pair(area, perimeter)
        }

        var totalPrice = 0L

        // Find all regions
        for (r in 0 until numRows) {
            for (c in 0 until numCols) {
                if (!visited[r][c]) {
                    val (area, perimeter) = dfs(r, c)
                    val price = area.toLong() * perimeter
                    totalPrice += price
                }
            }
        }

        println(totalPrice)

        return totalPrice
    }

    fun Int.sign(): Int = when {
        this > 0 -> 1
        this < 0 -> -1
        else -> 0
    }

    // Data class to represent a fence edge
    data class FenceEdge(val type: Char, val orientation: Char, val position: Pair<Int, Int>)

    // Data class to represent a region
    data class Region(
        val id: Int,
        val plantType: Char,
        var area: Int = 0,
        val fenceEdges: MutableList<FenceEdge> = mutableListOf()
    )

     fun calculateTotalFencingPrice(mapInput: String): Long {
        // Split the input into lines and remove any empty lines
        val lines = mapInput.lines().filter { it.isNotBlank() }

        // Dimensions of the grid
        val rows = lines.size
        val cols = lines[0].length

        // Create the grid
        val grid = Array(rows) { CharArray(cols) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                grid[i][j] = lines[i][j]
            }
        }

        // Initialize variables for region identification
        val visited = Array(rows) { BooleanArray(cols) }
        var regionId = 0
        val regions = mutableListOf<Region>()

        // Directions: up, down, left, right
        val directions = listOf(
            Pair(-1, 0),
            Pair(1, 0),
            Pair(0, -1),
            Pair(0, 1)
        )

        // BFS to identify regions
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (!visited[i][j]) {
                    // Start a new region
                    val currentPlant = grid[i][j]
                    val region = Region(regionId, currentPlant)
                    regions.add(region)
                    regionId++

                    // BFS queue
                    val queue: Queue<Pair<Int, Int>> = LinkedList()
                    queue.add(Pair(i, j))
                    visited[i][j] = true

                    while (queue.isNotEmpty()) {
                        val (x, y) = queue.poll()
                        region.area++

                        // Check all four directions
                        for ((dx, dy) in directions) {
                            val nx = x + dx
                            val ny = y + dy

                            if (nx in 0 until rows && ny in 0 until cols && grid[nx][ny] == currentPlant) {
                                if (!visited[nx][ny]) {
                                    visited[nx][ny] = true
                                    queue.add(Pair(nx, ny))
                                }
                            } else {
                                // This edge is part of the fence
                                when {
                                    dx == -1 && dy == 0 -> { // Top edge
                                        region.fenceEdges.add(FenceEdge(currentPlant, 'H', Pair(x, y)))
                                    }

                                    dx == 1 && dy == 0 -> { // Bottom edge
                                        region.fenceEdges.add(FenceEdge(currentPlant, 'H', Pair(x + 1, y)))
                                    }

                                    dx == 0 && dy == -1 -> { // Left edge
                                        region.fenceEdges.add(FenceEdge(currentPlant, 'V', Pair(x, y)))
                                    }

                                    dx == 0 && dy == 1 -> { // Right edge
                                        region.fenceEdges.add(FenceEdge(currentPlant, 'V', Pair(x, y + 1)))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Now, for each region, calculate the number of sides
        var totalPrice: Long = 0

        for (region in regions) {
            val horizontalEdges = region.fenceEdges.filter { it.orientation == 'H' }.map { it.position }
            val verticalEdges = region.fenceEdges.filter { it.orientation == 'V' }.map { it.position }

            // Function to count contiguous segments
            fun countSegments(edges: List<Pair<Int, Int>>, isHorizontal: Boolean): Int {
                if (edges.isEmpty()) return 0
                // Sort edges
                val sorted = if (isHorizontal) {
                    edges.sortedWith(compareBy({ it.first }, { it.second }))
                } else {
                    edges.sortedWith(compareBy({ it.second }, { it.first }))
                }

                var segments = 0
                var prev: Pair<Int, Int>? = null

                for (edge in sorted) {
                    if (prev == null) {
                        segments++
                    } else {
                        if (isHorizontal) {
                            // For horizontal, check if same row and adjacent columns
                            if (edge.first == prev.first && edge.second == prev.second + 1) {
                                // Same segment
                            } else {
                                segments++
                            }
                        } else {
                            // For vertical, check if same column and adjacent rows
                            if (edge.second == prev.second && edge.first == prev.first + 1) {
                                // Same segment
                            } else {
                                segments++
                            }
                        }
                    }
                    prev = edge
                }

                return segments
            }

            val horizontalSegments = countSegments(horizontalEdges, isHorizontal = true)
            val verticalSegments = countSegments(verticalEdges, isHorizontal = false)
            val numberOfSides = horizontalSegments + verticalSegments

            // Calculate the price for this region
            val price = region.area.toLong() * numberOfSides
            totalPrice += price
        }

        return totalPrice
    }

    fun part2(input: List<String>): Long {
        // Example test cases
        val testCases = listOf(
            // Test Case 1
            """
            AAAA
            BBCD
            BBCC
            EEEC
        """.trimIndent(),
            // Test Case 2
            """
            OOOOO
            OXOXO
            OOOOO
            OXOXO
            OOOOO
        """.trimIndent(),
            // Test Case 3
            """
            RRRRIICCFF
            RRRRIICCCF
            VVRRRCCFFF
            VVRCCCJFFF
            VVVVCJJCFE
            VVIVCCJJEE
            VVIIICJJEE
            MIIIIIJJEE
            MIIISIJEEE
            MMMISSJEEE
        """.trimIndent(),
            // Additional Test Cases can be added here
            // Part Two Example 1
            """
            AAAA
            BBCD
            BBCC
            EEEC
        """.trimIndent(),
            // Part Two Example 2
            """
            OOOOO
            OXOXO
            OOOOO
            OXOXO
            OOOOO
        """.trimIndent(),
            // Part Two Example with E-shaped region
            """
            EEEEE
            EXXXX
            EEEEE
            EXXXX
            EEEEE
        """.trimIndent(),
            // Another Part Two Example
            """
            AAAAAA
            AAABBA
            AAABBA
            ABBAAA
            ABBAAA
            AAAAAA
        """.trimIndent(),
            // Larger Example from the problem statement (updated for Part Two)
            """
            RRRRIICCFF
            RRRRIICCCF
            VVRRRCCFFF
            VVRCCCJFFF
            VVVVCJJCFE
            VVIVCCJJEE
            VVIIICJJEE
            MIIIIIJJEE
            MIIISIJEEE
            MMMISSJEEE
        """.trimIndent()
        )
        // Iterate through test cases
        for ((index, testCase) in testCases.withIndex()) {
            println("=== Test Case ${index + 1} ===")
            val totalPrice = calculateTotalFencingPrice(testCase)
            println("Total Price: $totalPrice")
            println()
        }

        val calculateTotalFencingPrice = calculateTotalFencingPrice(input.joinToString("\n"))

        println(calculateTotalFencingPrice)
        return calculateTotalFencingPrice
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 1930.toLong())
    check(part2(testInput) == 1206.toLong())

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

