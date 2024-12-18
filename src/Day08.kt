package cz.tomasbublik

import readInput
import kotlin.math.abs

fun main() {

    fun part1(input: List<String>): Int {

        val numRows = input.size
        val numCols = input[0].length

        // Store antenna positions based on their frequency
        // Map: Frequency sign -> list of positions (row, col)
        val antennasByFreq = mutableMapOf<Char, MutableList<Pair<Int, Int>>>()

        for (r in 0 until numRows) {
            for (c in 0 until numCols) {
                val ch = input[r][c]
                if (ch != '.') {
                    // Antena found
                    antennasByFreq.computeIfAbsent(ch) { mutableListOf() }.add(r to c)
                }
            }
        }

        // Set to store all antinodes
        val antinodes = mutableSetOf<Pair<Int, Int>>()

        // For each frequency, generate antinodes from all pairs of antennas
        for ((_, positions) in antennasByFreq) {
            val n = positions.size
            // All pairs (i,j) with i<j
            for (i in 0 until n) {
                for (j in i + 1 until n) {
                    val (rA, cA) = positions[i]
                    val (rB, cB) = positions[j]

                    // Two antinodes:
                    // P1 = (2*A - B) = (2rA - rB, 2cA - cB)
                    val p1R = 2 * rA - rB
                    val p1C = 2 * cA - cB
                    // P2 = (2*B - A) = (2rB - rA, 2cB - cA)
                    val p2R = 2 * rB - rA
                    val p2C = 2 * cB - cA

                    // Check if the antinodes are inside the map
                    if (p1R in 0 until numRows && p1C in 0 until numCols) {
                        antinodes.add(p1R to p1C)
                    }
                    if (p2R in 0 until numRows && p2C in 0 until numCols) {
                        antinodes.add(p2R to p2C)
                    }
                }
            }
        }

        // Count how many unique antinodes are in the map
        val result = antinodes.size

        println(result)
        return result
    }

    fun part2(input: List<String>): Int {

        val height = input.size
        val width = input.first().length

        // 1. Find all antennas and store them grouped by frequency
        val antennasByFreq = mutableMapOf<Char, MutableList<Pair<Int, Int>>>()
        for (y in 0 until height) {
            for (x in 0 until width) {
                val c = input[y][x]
                if (c != '.') {
                    antennasByFreq.computeIfAbsent(c) { mutableListOf() }.add(Pair(x, y))
                }
            }
        }

        // Function to calculate gcd
        fun gcd(a: Int, b: Int): Int {
            return if (b == 0) abs(a) else gcd(b, a % b)
        }

        // Function for line normalization
        // Line through points (x1,y1) and (x2,y2):
        // A = y2 - y1
        // B = x1 - x2
        // C = x2*y1 - x1*y2
        // Normalize so that gcd(A,B,C)=1 and ensure unique representation of signs
        fun lineFromPoints(x1: Int, y1: Int, x2: Int, y2: Int): Triple<Int, Int, Int> {
            val A = y2 - y1
            val B = x1 - x2
            val C = x2 * y1 - x1 * y2
            val g = gcd(gcd(A, B), C)
            val A1 = A / if (g == 0) 1 else g
            val B1 = B / if (g == 0) 1 else g
            val C1 = C / if (g == 0) 1 else g

            // Normalize signs: we want a unique representation
            // Ensure A>0 or if A=0, then B>0; if both A and B=0 (should not happen with different points), then C>0.
            return if (A1 < 0 || (A1 == 0 && B1 < 0) || (A1 == 0 && B1 == 0 && C1 < 0)) {
                Triple(-A1, -B1, -C1)
            } else {
                Triple(A1, B1, C1)
            }
        }

        // 2. For each frequency, find all unique lines that contain at least a pair of antennas
        val linesByFreq = mutableMapOf<Char, MutableSet<Triple<Int, Int, Int>>>()
        for ((freq, positions) in antennasByFreq) {
            val setOfLines = mutableSetOf<Triple<Int, Int, Int>>()
            // Consider pairs of antennas
            for (i in positions.indices) {
                for (j in i + 1 until positions.size) {
                    val (x1, y1) = positions[i]
                    val (x2, y2) = positions[j]
                    // If the points are identical (should not happen), ignore them
                    if (x1 == x2 && y1 == y2) continue
                    val line = lineFromPoints(x1, y1, x2, y2)
                    setOfLines.add(line)
                }
            }
            linesByFreq[freq] = setOfLines
        }

        // 3. Now, for each frequency, we have a set of unique lines.
        // Check every point on the map to see if it lies on any of these lines.
        // If yes, it is an antinode for that frequency.
        // It's enough for the point to be an antinode for any frequency, add it to the global set of antinodes.

        val antinodes = mutableSetOf<Pair<Int, Int>>()
        for ((freq, lines) in linesByFreq) {
            // If a frequency has only one antenna, no lines can form, so no antinodes exist
            if (lines.isEmpty()) {
                // A single antenna of a given frequency does not form an antinode
                // (According to part 2, at least two same antennas must lie on a line)
                continue
            }
            // Otherwise, iterate over all points
            for (y in 0 until height) {
                for (x in 0 until width) {
                    // Zkontrolujeme, zda (x,y) leží na některé z přímek
                    // Podmínka: A*x + B*y + C = 0
                    val onLine = lines.any { (A, B, C) ->
                        A * x + B * y + C == 0
                    }
                    if (onLine) {
                        antinodes.add(Pair(x, y))
                    }
                }
            }
        }

        println(antinodes.size)

        return antinodes.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 14)
    check(part2(testInput) == 34)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
