import java.util.*

fun main() {
    fun solve(input: List<String>): Int {
        // Definujeme rozměry mřížky
        val WIDTH = 71
        val HEIGHT = 71

        // Inicializace mřížky - false znamená bezpečná buňka, true korumpovaná
        val grid = Array(HEIGHT) { BooleanArray(WIDTH) { false } }

        // Simulace prvních 1024 byteů (nebo méně, pokud je vstup kratší)
        val bytesToSimulate = minOf(1024, input.size)
        for (i in 0 until bytesToSimulate) {
            val line = input[i].trim()
            if (line.isEmpty()) continue
            val parts = line.split(",")
            if (parts.size != 2) continue
            val x = parts[0].toIntOrNull() ?: continue
            val y = parts[1].toIntOrNull() ?: continue
            if (x in 0 until WIDTH && y in 0 until HEIGHT) {
                grid[y][x] = true
            }
        }

        // Start a cílový bod
        val start = Pair(0, 0)
        val end = Pair(WIDTH - 1, HEIGHT - 1)

        // Kontrola, zda jsou start nebo end korumpované
        if (grid[start.second][start.first] || grid[end.second][end.first]) {
            return -1 // Cesta neexistuje
        }

        // BFS pro nalezení nejkratší cesty
        val directions = listOf(
            Pair(-1, 0), // Nahoru
            Pair(1, 0),  // Dolů
            Pair(0, -1), // Vlevo
            Pair(0, 1)   // Vpravo
        )

        // Vytvoření mřížky pro sledování navštívených buněk
        val visited = Array(HEIGHT) { BooleanArray(WIDTH) { false } }
        visited[start.second][start.first] = true

        // Fronta pro BFS, obsahuje Pair(r, c) a počet kroků
        val queue: Queue<Triple<Int, Int, Int>> = LinkedList()
        queue.add(Triple(start.second, start.first, 0)) // (r, c, steps)

        while (queue.isNotEmpty()) {
            val (r, c, steps) = queue.poll()

            // Pokud jsme dosáhli cíle, vrátíme počet kroků
            if (Pair(c, r) == end) {
                return steps
            }

            // Pro každého souseda
            for ((dr, dc) in directions) {
                val nr = r + dr
                val nc = c + dc

                // Kontrola, zda jsou nové souřadnice v mřížce
                if (nr in 0 until HEIGHT && nc in 0 until WIDTH) {
                    // Kontrola, zda je buňka bezpečná a nebyla navštívena
                    if (!grid[nr][nc] && !visited[nr][nc]) {
                        visited[nr][nc] = true
                        queue.add(Triple(nr, nc, steps + 1))
                    }
                }
            }
        }

        // Pokud cesta neexistuje
        return -1
    }

    fun pathExists(
        grid: Array<BooleanArray>,
        start: Pair<Int, Int>,
        end: Pair<Int, Int>,
        directions: List<Pair<Int, Int>>
    ): Boolean {
        if (grid[start.second][start.first] || grid[end.second][end.first]) {
            return false
        }

        val visited = Array(grid.size) { BooleanArray(grid[0].size) { false } }
        val queue: Queue<Pair<Int, Int>> = LinkedList()
        queue.add(start)
        visited[start.second][start.first] = true

        while (queue.isNotEmpty()) {
            val (r, c) = queue.poll()

            if (Pair(c, r) == end) {
                return true
            }

            for ((dr, dc) in directions) {
                val nr = r + dr
                val nc = c + dc

                if (nr in grid.indices && nc in grid[0].indices && !grid[nr][nc] && !visited[nr][nc]) {
                    visited[nr][nc] = true
                    queue.add(Pair(nr, nc))
                }
            }
        }

        return false
    }

    fun findFirstBlockingByte(input: List<String>): String {
        // Definujeme rozměry mřížky
        val WIDTH = 71
        val HEIGHT = 71

        // Inicializace mřížky - false znamená bezpečná buňka, true korumpovaná
        val grid = Array(HEIGHT) { BooleanArray(WIDTH) { false } }

        // Start a cílový bod
        val start = Pair(0, 0)
        val end = Pair(WIDTH - 1, HEIGHT - 1)

        // Pokud je start nebo end již korumpovaný, cesta neexistuje
        // Ale v počátečním stavu jsou všechny buňky bezpečné
        if (grid[start.second][start.first] || grid[end.second][end.first]) {
            return "-1,-1" // Cesta neexistuje
        }

        // Definujeme směr pohybu: nahoru, dolů, vlevo, vpravo
        val directions = listOf(
            Pair(-1, 0), // Nahoru
            Pair(1, 0),  // Dolů
            Pair(0, -1), // Vlevo
            Pair(0, 1)   // Vpravo
        )

        // Iterace přes bytey jeden po druhém
        for (byte in input) {
            val trimmed = byte.trim()
            if (trimmed.isEmpty()) continue
            val parts = trimmed.split(",")
            if (parts.size != 2) continue
            val x = parts[0].toIntOrNull() ?: continue
            val y = parts[1].toIntOrNull() ?: continue
            if (x !in 0 until WIDTH || y !in 0 until HEIGHT) continue

            // Oznámíme buňku jako korumpovanou
            grid[y][x] = true

            // Pokud jsme korumpovali start nebo end, cesta je blokována
            if ((x == start.first && y == start.second) || (x == end.first && y == end.second)) {
                return "$x,$y"
            }

            // Zkontrolujeme, zda cesta stále existuje pomocí BFS
            if (!pathExists(grid, start, end, directions)) {
                return "$x,$y"
            }
        }

        // Pokud žádný byte nezablokoval cestu, vrátíme -1,-1
        return "-1,-1"
    }

    fun part1(input: List<String>): Int {
        val result = solve(input)

        println("Result is: $result")

        return result
    }

    fun part2(input: List<String>): String {

        val result = findFirstBlockingByte(input)
        println("Part 2 result: $result") // V tomto příkladu by měl výstup být "6,1"
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 146)
//    check(part2(testInput) == 1206.toLong())

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
