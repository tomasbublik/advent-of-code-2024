import readInput
import java.util.*

fun main() {

    fun part1(input: List<String>): Int {
        val map = input.map { it.toCharArray() }
        val rows = map.size
        val cols = map[0].size

        var startX = -1
        var startY = -1
        var endX = -1
        var endY = -1

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                when (map[r][c]) {
                    'S' -> {
                        startX = c
                        startY = r
                    }

                    'E' -> {
                        endX = c
                        endY = r
                    }
                }
            }
        }

        if (startX < 0 || startY < 0 || endX < 0 || endY < 0) {
            error("Map does not contain S or E")
        }

        val dx = arrayOf(0, 1, 0, -1)
        val dy = arrayOf(-1, 0, 1, 0)

        val dist = Array(rows) { Array(cols) { IntArray(4) { Int.MAX_VALUE } } }

        dist[startY][startX][1] = 0

        data class State(val x: Int, val y: Int, val dir: Int, val cost: Int)

        val pq = PriorityQueue<State>(compareBy { it.cost })
        pq.add(State(startX, startY, 1, 0))

        while (pq.isNotEmpty()) {
            val (x, y, dir, cost) = pq.poll()
            if (cost > dist[y][x][dir]) continue

            if (x == endX && y == endY) {
                return cost
            }

            val leftDir = (dir + 3) % 4
            val rightDir = (dir + 1) % 4

            val costLeft = cost + 1000
            if (costLeft < dist[y][x][leftDir]) {
                dist[y][x][leftDir] = costLeft
                pq.add(State(x, y, leftDir, costLeft))
            }

            val costRight = cost + 1000
            if (costRight < dist[y][x][rightDir]) {
                dist[y][x][rightDir] = costRight
                pq.add(State(x, y, rightDir, costRight))
            }

            val nx = x + dx[dir]
            val ny = y + dy[dir]
            if (ny in 0 until rows && nx in 0 until cols && map[ny][nx] != '#') {
                val forwardCost = cost + 1
                if (forwardCost < dist[ny][nx][dir]) {
                    dist[ny][nx][dir] = forwardCost
                    pq.add(State(nx, ny, dir, forwardCost))
                }
            }
        }

        return -1
    }

    data class Position(val x: Int, val y: Int)

    data class State(
        val position: Position,
        val direction: Int, // 0: North, 1: East, 2: South, 3: West
        val score: Long
    ) : Comparable<State> {
        override fun compareTo(other: State): Int {
            return this.score.compareTo(other.score)
        }
    }

    fun parseGrid(input: List<String>): List<List<Char>> {
        return input.map { it.toList() }
    }

    fun findPosition(grid: List<List<Char>>, target: Char): Position? {
        for (y in grid.indices) {
            for (x in grid[y].indices) {
                if (grid[y][x] == target) {
                    return Position(x, y)
                }
            }
        }
        return null
    }

    fun dijkstra(
        grid: List<List<Char>>,
        start: Position,
        end: Position,
        directions: List<Pair<Int, Int>>,
        width: Int,
        height: Int
    ): Pair<Long, MutableMap<State, MutableList<State>>> {
        val queue = PriorityQueue<State>()

        val minScores = mutableMapOf<State, Long>()

        val predecessors = mutableMapOf<State, MutableList<State>>()

        val startState = State(start, 1, 0)
        queue.add(startState)
        minScores[startState] = 0

        while (queue.isNotEmpty()) {
            val current = queue.poll()

            val (dx, dy) = directions[current.direction]
            val newX = current.position.x + dx
            val newY = current.position.y + dy

            if (newX in 0 until width && newY in 0 until height && grid[newY][newX] != '#') {
                val newPosition = Position(newX, newY)
                val newState = State(newPosition, current.direction, current.score + 1)

                if (newState.score < minScores.getOrDefault(newState, Long.MAX_VALUE)) {
                    minScores[newState] = newState.score
                    queue.add(newState)
                    predecessors[newState] = mutableListOf(current)
                } else if (newState.score == minScores.getOrDefault(newState, Long.MAX_VALUE)) {
                    predecessors.getOrPut(newState) { mutableListOf() }.add(current)
                }
            }

            // 2. Turn Left (counterclockwise)
            val newDirLeft = (current.direction + 3) % 4 // Equivalent to -1 mod 4
            val turnLeftState = State(current.position, newDirLeft, current.score + 1000)
            if (turnLeftState.score < minScores.getOrDefault(turnLeftState, Long.MAX_VALUE)) {
                minScores[turnLeftState] = turnLeftState.score
                queue.add(turnLeftState)
                predecessors[turnLeftState] = mutableListOf(current)
            } else if (turnLeftState.score == minScores.getOrDefault(turnLeftState, Long.MAX_VALUE)) {
                predecessors.getOrPut(turnLeftState) { mutableListOf() }.add(current)
            }

            // 3. Turn Right (clockwise)
            val newDirRight = (current.direction + 1) % 4
            val turnRightState = State(current.position, newDirRight, current.score + 1000)
            if (turnRightState.score < minScores.getOrDefault(turnRightState, Long.MAX_VALUE)) {
                minScores[turnRightState] = turnRightState.score
                queue.add(turnRightState)
                predecessors[turnRightState] = mutableListOf(current)
            } else if (turnRightState.score == minScores.getOrDefault(turnRightState, Long.MAX_VALUE)) {
                predecessors.getOrPut(turnRightState) { mutableListOf() }.add(current)
            }
        }

        val endStates = minScores.filter { it.key.position == end }
        if (endStates.isEmpty()) {
            return Pair(Long.MAX_VALUE, predecessors)
        }

        val minScore = endStates.minOf { it.value }

        val bestEndStates = endStates.filter { it.value == minScore }.keys

        return Pair(minScore, predecessors)
    }

    fun traversePredecessors(
        current: State,
        predecessors: Map<State, MutableList<State>>,
        bestPathTiles: MutableSet<Position>,
        start: Position
    ) {
        if (current.position == start) {
            bestPathTiles.add(start)
            return
        }

        val preds = predecessors[current]
        if (preds == null) return

        for (pred in preds) {
            bestPathTiles.add(pred.position)
            traversePredecessors(pred, predecessors, bestPathTiles, start)
        }
    }

      fun reconstructBestPaths(
        predecessors: Map<State, MutableList<State>>,
        start: Position,
        end: Position
    ): Set<Position> {
        val endStates = predecessors.keys.filter { it.position == end }

        val bestPathTiles = mutableSetOf<Position>()

        for (endState in endStates) {
            traversePredecessors(endState, predecessors, bestPathTiles, start)
        }

        bestPathTiles.add(start)

        return bestPathTiles
    }

    fun part2(input: List<String>): Int {
        val grid = parseGrid(input)
        val width = grid[0].size
        val height = grid.size

        val start = findPosition(grid, 'S') ?: throw IllegalArgumentException("Start (S) not found")
        val end = findPosition(grid, 'E') ?: throw IllegalArgumentException("End (E) not found")

        val directions = listOf(
            Pair(0, -1),  // North
            Pair(1, 0),   // East
            Pair(0, 1),   // South
            Pair(-1, 0)   // West
        )

        val (minScore, predecessors) = dijkstra(grid, start, end, directions, width, height)

        if (minScore == Long.MAX_VALUE) {
            println("No path found from S to E.")
            return 0
        }

        val bestPathTiles = reconstructBestPaths(predecessors, start, end)

        val safetyFactor = bestPathTiles.size.toLong()

        println("Nejmenší skóre pro dosažení E: $minScore")
        println("Počet dlaždic na všech nejlepších cestách: $safetyFactor")

        return 0
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 7036)
    check(part2(testInput) == 45)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}
