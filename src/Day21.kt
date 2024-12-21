import java.util.*

fun main() {


    // Reprezentace "numerického" keypad (N_PAD) v Kotlinu:
    val N_PAD: Map<String, List<Pair<String, String>>> = mapOf(
        "7" to listOf("8" to ">", "4" to "v"),
        "8" to listOf("7" to "<", "9" to ">", "5" to "v"),
        "9" to listOf("8" to "<", "6" to "v"),
        "4" to listOf("7" to "^", "5" to ">", "1" to "v"),
        "5" to listOf("2" to "v", "8" to "^", "6" to ">", "4" to "<"),
        "6" to listOf("9" to "^", "5" to "<", "3" to "v"),
        "1" to listOf("4" to "^", "2" to ">"),
        "2" to listOf("1" to "<", "5" to "^", "3" to ">", "0" to "v"),
        "3" to listOf("2" to "<", "6" to "^", "A" to "v"),
        "0" to listOf("2" to "^", "A" to ">"),
        "A" to listOf("0" to "<", "3" to "^")
    )

// Reprezentace "směrového" keypad (D_PAD) v Kotlinu:
    val D_PAD: Map<String, List<Pair<String, String>>> = mapOf(
        "^" to listOf("A" to ">", "v" to "v"),
        "A" to listOf("^" to "<", ">" to "v"),
        "<" to listOf("v" to ">"),
        "v" to listOf("<" to "<", "^" to "^", ">" to ">"),
        ">" to listOf("v" to "<", "A" to "^")
    )

    /**
     * BFS, které hledá všechny nejkratší cesty (sekvence šipek) z `start` do `end`
     * v daném "keypadu" (pad), kde pad[start] vrací List sousedů (Pair soused, šipka).
     *
     * Vrací seznam řetězců, kde každý řetězec reprezentuje posloupnost směrových
     * tlačítek včetně finálního 'A' (aktivace).
     */
    fun bfs(
        start: String,
        end: String,
        pad: Map<String, List<Pair<String, String>>>
    ): List<String> {
        // fronta stavů: (aktuální znak, "cesta" jako list šipek)
        val queue = ArrayDeque(listOf(start to emptyList<String>()))
        var shortest: Int? = null
        val res = mutableListOf<String>()

        while (queue.isNotEmpty()) {
            val (current, path) = queue.removeFirst()

            if (current == end) {
                // narazili jsme na cíl
                if (shortest == null) {
                    shortest = path.size
                }
                if (path.size == shortest) {
                    // do výsledku uložíme path + "A" (v originálním Pythonu se také přidávalo "A")
                    val finalPath = path + "A"
                    res.add(finalPath.joinToString(""))
                }
                continue
            }

            // pokud už máme shortest a jsme delší nebo rovni shortest, nemá smysl pokračovat
            if (shortest != null && path.size >= shortest) {
                continue
            }

            // Projdeme sousedy
            val neighbors = pad[current] ?: emptyList()
            for ((neighbor, direction) in neighbors) {
                val newPath = path + direction
                queue.addLast(neighbor to newPath)
            }
        }
        return res
    }

    fun findPathLen(code: String, level: Int, isNumPad: Boolean): Int {
        val pad = if (isNumPad) N_PAD else D_PAD
        var res = 0
        var current = "A"  // výchozí "pozice" (robot míří na 'A')

        // Pro každý znak v kódu voláme BFS (bfs) a zjišťujeme všechny
        // nejkratší cesty z `current` do `c`.
        // Pokud jsme na "úrovni" 0, jen spočteme minimální délku z BFS výsledků.
        // Jinak pro každou takovou cestu rekurzivně spočteme findPathLen.
        for (ch in code) {
            val c = ch.toString()
            val paths = bfs(current, c, pad)

            if (level == 0) {
                // Min délka cesty mezi current a c
                val minLen = paths.minOfOrNull { it.length } ?: 0
                res += minLen
            } else {
                // Zde rekurzivně voláme findPathLen pro každou z nalezených
                // "cest" (která sama je řetězec šipek plus 'A' na konci).
                // V dalším volání je isNumPad = false, protože
                // "úroveň" níž je vždy směrový keypad.
                // Najdeme minimum ze všech možností.
                val subResults = paths.map { p ->
                    findPathLen(p, level - 1, isNumPad = false)
                }
                // přičteme minimum
                val minSub = subResults.minOrNull() ?: 0
                res += minSub
            }
            current = c
        }
        return res
    }

    fun part1(input: List<String>): Long {
        val before = System.nanoTime()
        var total = 0L
        for (line in input) {
            // číslo je vše kromě posledního znaku (ten je písmeno A atd.)
            // např. "980A" -> numeric část "980"
            val numericPart = line.dropLast(1)
            val number = numericPart.toIntOrNull() ?: 0

            // find_path_len(code, 2, True) v Pythonu
            val pathLen = findPathLen(line, level = 2, isNumPad = true)

            total += number * pathLen
        }

        println(total)

        val after = System.nanoTime()
        println("Time: ${(after - before)/1_000_000.0} ms")
        return total
    }

    // Datová třída pro reprezentaci stavu v memoizaci
    data class State(val code: String, val level: Long, val isNumpad: Boolean)

    fun bfs2(start: String, end: String, pad: Map<String, List<Pair<String, String>>>): List<String> {
        val queue: Queue<Pair<String, String>> = LinkedList()
        queue.add(Pair(start, ""))
        var shortest: Int? = null
        val res = mutableListOf<String>()

        while (queue.isNotEmpty()) {
            val (current, path) = queue.poll()

            if (current == end) {
                if (shortest == null) {
                    shortest = path.length
                }
                if (path.length == shortest) {
                    res.add(path + "A")
                }
                continue
            }

            if (shortest != null && path.length >= shortest!!) {
                continue
            }

            for ((neighbor, direction) in pad.getOrDefault(current, emptyList())) {
                queue.add(Pair(neighbor, path + direction))
            }
        }

        return res
    }

    // Funkce pro výpočet délky cesty s memoizací
    fun findPathLen2(code: String, level: Long, isNumpad: Boolean, dp: MutableMap<State, Long>): Long {
        val state = State(code, level, isNumpad)
        if (dp.containsKey(state)) {
            return dp[state]!!
        }

        val pad = if (isNumpad) N_PAD else D_PAD
        var res = 0L
        var current = "A"

        for (c in code) {
            val target = c.toString()
            val paths = bfs2(current, target, pad)

            if (paths.isEmpty()) {
                // Pokud neexistuje cesta, může být problém s kódem
                // Pro tuto implementaci budeme předpokládat, že cesta vždy existuje
                // Pokud cestu nenajdeme, můžeme vrátit nějakou hodnotu nebo pokračovat
                // Zde vrátíme Int.MAX_VALUE jako indikátor neexistující cesty
                dp[state] = Long.MAX_VALUE
                return Long.MAX_VALUE
            }

            if (level == 0L) {
                val minLen = paths.minOfOrNull { it.length } ?: Int.MAX_VALUE
                res += minLen
            } else {
                val minPathLen = paths.map { findPathLen2(it, level - 1, false, dp) }.minOrNull() ?: Long.MAX_VALUE
                res += minPathLen
            }

            current = target
        }

        dp[state] = res
        return res
    }

    fun part2(input: List<String>): Long {
        val lines = input.map { it.trim() }.filter { it.isNotEmpty() }
        var total = 0L
        val dp = mutableMapOf<State, Long>()

        for (line in lines) {
            if (line.length < 2) {
                // Ignorovat nepřiměřené řádky
                continue
            }

            val numberPart = line.dropLast(1)
            val number = numberPart.toIntOrNull() ?: 0
            val code = line

            val pathLen = findPathLen2(code, 25, true, dp)

            // Pokud cesta neexistuje, můžeme ji ignorovat nebo přidat nějakou hodnotu
            // Zde předpokládáme, že cesta vždy existuje
            total += number * pathLen
        }

        println(total)

        return total
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 126384L)
    check(part2(testInput) == 154115708116294)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}
