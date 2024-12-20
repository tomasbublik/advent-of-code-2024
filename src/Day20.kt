import java.util.*

fun main() {

    // Datová třída pro reprezentaci pozice na mapě
    data class Position(val x: Int, val y: Int)

    // Funkce pro výpočet úspory času při odstranění zdi
    fun calculateNewPath(first: Position, second: Position, originalPath: List<Position>): Int {
        val firstIndex = originalPath.indexOf(first)
        val secondIndex = originalPath.indexOf(second)

        // Pokud některá z pozic není v původní cestě, nepočítáme ji
        if (firstIndex == -1 || secondIndex == -1) return 0

        val pathBetween = kotlin.math.abs(secondIndex - firstIndex) - 2
        return if (pathBetween >= 100) 1 else 0
    }

    // Funkce pro nalezení cesty pomocí DFS
    fun findPath(grid: List<CharArray>, start: Position, finish: Position): List<Position> {
        val stack = ArrayDeque<Position>()
        stack.add(start)

        val visited = Array(grid.size) { BooleanArray(grid[0].size) }
        visited[start.y][start.x] = true

        // Pro uchování cesty
        val parentMap = mutableMapOf<Position, Position?>()
        parentMap[start] = null

        while (stack.isNotEmpty()) {
            val current = stack.removeLast()

            if (current == finish) {
                // Rekonstrukce cesty
                val path = mutableListOf<Position>()
                var pos: Position? = finish
                while (pos != null) {
                    path.add(pos)
                    pos = parentMap[pos]
                }
                return path.reversed()
            }

            // Prozkoumání sousedních polí (nahoru, dolů, vlevo, vpravo)
            val directions = listOf(
                Pair(0, 1),   // Dolů
                Pair(1, 0),   // Vpravo
                Pair(0, -1),  // Nahoru
                Pair(-1, 0)   // Vlevo
            )

            for ((dx, dy) in directions) {
                val newX = current.x + dx
                val newY = current.y + dy

                if (newX in grid[0].indices && newY in grid.indices) {
                    if (!visited[newY][newX] && grid[newY][newX] != '#') {
                        val neighbor = Position(newX, newY)
                        stack.add(neighbor)
                        visited[newY][newX] = true
                        parentMap[neighbor] = current
                    }
                }
            }
        }

        // Pokud cesta neexistuje
        return emptyList()
    }

    fun part1(input: List<String>): Int {
        // Vytvoření gridu jako seznam seznamů znaků
        val grid: List<CharArray> = input.map { it.toCharArray() }
        val height = grid.size
        val width = grid[0].size

        // Nalezení startovní a cílové pozice
        var start: Position? = null
        var end: Position? = null

        for (y in 0 until height) {
            for (x in 0 until width) {
                when (grid[y][x]) {
                    'S' -> start = Position(x, y)
                    'E' -> end = Position(x, y)
                }
            }
        }

        if (start == null || end == null) {
            println("Startovní ('S') nebo cílová ('E') pozice nebyla nalezena.")
            return 0
        }

        // Najít původní cestu pomocí DFS
        val originalPath = findPath(grid, start!!, end!!)
        if (originalPath.isEmpty() || originalPath.last() != end) {
            println("Cesta od startu do cíle neexistuje.")
            return 0
        }

        // Inicializace počitadla cheatů
        var counter = 0

        // Iterace přes všechny pozice na gridu
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (grid[y][x] == '#') {
                    // Kontrola vertikálních zdí
                    if (y > 0 && y < height - 1 && grid[y - 1][x] != '#' && grid[y + 1][x] != '#') {
                        val first = Position(x, y - 1)
                        val second = Position(x, y + 1)
                        counter += calculateNewPath(first, second, originalPath)
                    }
                    // Kontrola horizontálních zdí
                    if (x > 0 && x < width - 1 && grid[y][x - 1] != '#' && grid[y][x + 1] != '#') {
                        val first = Position(x - 1, y)
                        val second = Position(x + 1, y)
                        counter += calculateNewPath(first, second, originalPath)
                    }
                }
            }
        }

        // Výstup výsledku
        println("Počet cheatů, které šetří alespoň 100 pikosekund: $counter")

        return counter
    }

    // Funkce pro výpočet úspory času při odstranění zdi
    fun calculateNewPath2(firstIndex: Int, secondIndex: Int, wallPath: Int): Int {
        var pathBetween = kotlin.math.abs(secondIndex - firstIndex) - wallPath
        if (pathBetween >= 100) {
            return 1
        }
        return 0
    }

    // Funkce pro nalezení cesty pomocí DFS
    fun findPath2(grid: List<CharArray>, start: Position, finish: Position): List<Position> {
        val stack = ArrayDeque<Position>()
        stack.add(start)

        val visited = Array(grid.size) { BooleanArray(grid[0].size) }
        visited[start.y][start.x] = true

        // Mapa rodičů pro rekonstrukci cesty
        val parentMap = mutableMapOf<Position, Position?>()
        parentMap[start] = null

        while (stack.isNotEmpty()) {
            val current = stack.removeLast()

            if (current == finish) {
                // Rekonstrukce cesty
                val path = mutableListOf<Position>()
                var pos: Position? = finish
                while (pos != null) {
                    path.add(pos!!)
                    pos = parentMap[pos]
                }
                return path.reversed()
            }

            // Prozkoumání sousedních polí (nahoru, dolů, vlevo, vpravo)
            val directions = listOf(
                Pair(0, 1),   // Dolů
                Pair(1, 0),   // Vpravo
                Pair(0, -1),  // Nahoru
                Pair(-1, 0)   // Vlevo
            )

            for ((dx, dy) in directions) {
                val newX = current.x + dx
                val newY = current.y + dy

                if (newX in grid[0].indices && newY in grid.indices) {
                    if (!visited[newY][newX] && grid[newY][newX] != '#') {
                        val neighbor = Position(newX, newY)
                        stack.add(neighbor)
                        visited[newY][newX] = true
                        parentMap[neighbor] = current
                    }
                }
            }
        }

        // Pokud cesta neexistuje
        return emptyList()
    }


    fun part2(input: List<String>): Int {

        // Vytvoření gridu jako seznam seznamů znaků
        val grid: List<CharArray> = input.map { it.toCharArray() }
        val height = grid.size
        val width = grid[0].size

        // Nalezení startovní a cílové pozice
        var start: Position? = null
        var end: Position? = null

        for (y in 0 until height) {
            for (x in 0 until width) {
                when (grid[y][x]) {
                    'S' -> start = Position(x, y)
                    'E' -> end = Position(x, y)
                }
            }
        }

        if (start == null || end == null) {
            println("Startovní ('S') nebo cílová ('E') pozice nebyla nalezena.")
            return 0
        }

        // Najít původní cestu pomocí DFS
        val originalPath = findPath2(grid, start!!, end!!)
        if (originalPath.isEmpty() || originalPath.last() != end) {
            println("Cesta od startu do cíle neexistuje.")
            return 0
        }

        // Vytvoření mapy pro rychlé získání indexu pozice v cestě
        val pathIndex = originalPath.withIndex().associate { it.value to it.index }

        // Inicializace počitadla cheatů
        var counter = 0

        // Inicializace struktury pro sledování již kontrolovaných cheatů
        val alreadyChecked: MutableMap<Int, MutableSet<Int>> = mutableMapOf()

        // Iterace přes všechny pozice v původní cestě
        for ((i, point) in originalPath.withIndex()) {
            for (yOffset in 0..20) {
                for (xOffset in 0..(20 - yOffset)) {
                    val directions = listOf(
                        Pair(xOffset, yOffset),
                        Pair(-xOffset, yOffset),
                        Pair(xOffset, -yOffset),
                        Pair(-xOffset, -yOffset)
                    )
                    for ((dx, dy) in directions) {
                        val newX = point.x + dx
                        val newY = point.y + dy

                        // Přeskočení, pokud je nová pozice stejná jako současná
                        if (newX == point.x && newY == point.y) continue

                        val newPos = Position(newX, newY)
                        val newI = pathIndex[newPos] ?: -1

                        if (newI == -1) continue

                        // Kontrola, zda jsme již tento cheat zkontrolovali
                        if (alreadyChecked.containsKey(newI)) {
                            if (alreadyChecked[newI]?.contains(i) == true) continue
                            else alreadyChecked[newI]?.add(i)
                        } else {
                            alreadyChecked[newI] = mutableSetOf(i)
                        }

                        if (alreadyChecked.containsKey(i)) {
                            if (alreadyChecked[i]?.contains(newI) == true) continue
                            else alreadyChecked[i]?.add(newI)
                        } else {
                            alreadyChecked[i] = mutableSetOf(newI)
                        }

                        // Kontrola, zda je nová pozice na gridu a není zeď
                        if (newX in 0 until width && newY in 0 until height && grid[newY][newX] != '#') {
                            val wallPath = kotlin.math.abs(newX - point.x) + kotlin.math.abs(newY - point.y)
                            counter += calculateNewPath2(pathIndex[point]!!, pathIndex[newPos]!!, wallPath)
                        }
                    }
                }
            }
        }

        // Výstup výsledku
        println("Počet cheatů, které šetří alespoň 100 pikosekund: $counter")


        return counter
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 0)
    check(part2(testInput) == 0)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}
