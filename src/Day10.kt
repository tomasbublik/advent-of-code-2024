import readInput
import java.math.BigInteger

fun main() {

    fun part1(input: List<String>): Int {

        val numRows = input.size
        val numCols = input[0].length
        val map = Array(numRows) { r -> IntArray(numCols) { c -> input[r][c].digitToInt() } }

        // Find all positions with a height of 9 and assign them an ID
        val nines = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until numRows) {
            for (c in 0 until numCols) {
                if (map[r][c] == 9) {
                    nines.add(r to c)
                }
            }
        }
        // ID '9' will be the index in the nines list
        val nineIdMap = Array(numRows) { IntArray(numCols) { -1 } }
        nines.forEachIndexed { id, (rr, cc) ->
            nineIdMap[rr][cc] = id
        }

        // direction of movement
        val directions = arrayOf(
            -1 to 0, // up
            1 to 0,  // down
            0 to -1, // left
            0 to 1   // right
        )

        // Memoization: for each position we will store the set of reachable nine IDs
        // null means we haven't calculated it yet
        val memo = Array<MutableSet<Int>?>(numRows * numCols) { null }

        fun idx(r: Int, c: Int) = r * numCols + c

        fun reachableNines(r: Int, c: Int): MutableSet<Int> {
            val m = memo[idx(r, c)]
            if (m != null) return m

            val h = map[r][c]
            val result = mutableSetOf<Int>()
            if (h == 9) {
                // this position is 9, it can reach itself
                result.add(nineIdMap[r][c])
            } else {
                // h < 9, look for neighbors with h+1
                val hNext = h + 1
                for ((dr, dc) in directions) {
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in 0 until numRows && nc in 0 until numCols && map[nr][nc] == hNext) {
                        // unify the sets
                        result.addAll(reachableNines(nr, nc))
                    }
                }
            }

            memo[idx(r, c)] = result
            return result
        }

        // Find all trailheads (height 0)
        val trailheads = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until numRows) {
            for (c in 0 until numCols) {
                if (map[r][c] == 0) {
                    trailheads.add(r to c)
                }
            }
        }

        // Calculate the total "score" of all trailheads
        var totalScore = 0L
        for ((r, c) in trailheads) {
            val reached = reachableNines(r, c)
            totalScore += reached.size
        }

        println(totalScore)
        return totalScore.toInt()
    }

    fun part2(input: List<String>): BigInteger {
        val numRows = input.size
        val numCols = input[0].length
        val map = Array(numRows) { r -> IntArray(numCols) { c -> input[r][c].digitToInt() } }

        // Definice směru pohybu
        val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        // dp memo for counting the number of paths
        val dp = Array<BigInteger?>(numRows*numCols) { null }

        fun idx(r:Int,c:Int) = r*numCols+c

        fun countPaths(r:Int,c:Int): BigInteger {
            val h = map[r][c]
            val memoVal = dp[idx(r,c)]
            if (memoVal != null) return memoVal

            if (h == 9) {
                dp[idx(r,c)] = BigInteger.ONE
                return BigInteger.ONE
            }

            val nextH = h+1
            var result = BigInteger.ZERO
            for ((dr,dc) in directions) {
                val nr = r+dr
                val nc = c+dc
                if (nr in 0 until numRows && nc in 0 until numCols) {
                    if (map[nr][nc] == nextH) {
                        result = result.add(countPaths(nr,nc))
                    }
                }
            }
            dp[idx(r,c)] = result
            return result
        }

        // Trailheads are cells with a height of 0
        var totalRating = BigInteger.ZERO
        for (r in 0 until numRows) {
            for (c in 0 until numCols) {
                if (map[r][c] == 0) {
                    // rating = number of different paths from (r,c)
                    val rating = countPaths(r,c)
                    totalRating = totalRating.add(rating)
                }
            }
        }

        println(totalRating)

        return totalRating
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 36)
    check(part2(testInput) == BigInteger.valueOf(81))

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
