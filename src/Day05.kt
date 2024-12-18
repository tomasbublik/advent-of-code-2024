fun main() {

    fun parseInput(inputText: String): Pair<List<Pair<Int, Int>>, List<List<Int>>> {
        val sections = inputText.trim().split("\n\n")
        val rulesSection = sections[0].trim()
        val updatesSection = sections[1].trim()

        // Parse ordering rules
        val orderingRules = rulesSection.lines()
            .filter { it.isNotBlank() }
            .map { line ->
                val parts = line.trim().split("|")
                val X = parts[0].toInt()
                val Y = parts[1].toInt()
                Pair(X, Y)
            }

        // Parse updates
        val updates = updatesSection.lines()
            .filter { it.isNotBlank() }
            .map { line ->
                line.trim().split(",").map { it.trim().toInt() }
            }

        return Pair(orderingRules, updates)
    }

    fun isUpdateCorrect(orderingRules: List<Pair<Int, Int>>, update: List<Int>): Boolean {
        // Create a map from page number to its index in the update
        val pageIndices = update.withIndex().associate { it.value to it.index }

        // Filter applicable rules
        val applicableRules = orderingRules.filter { (X, Y) ->
            X in pageIndices && Y in pageIndices
        }

        // Check if all applicable rules are satisfied
        for ((X, Y) in applicableRules) {
            if (pageIndices[X]!! >= pageIndices[Y]!!) {
                // Rule violated
                return false
            }
        }

        return true
    }

    fun getMiddlePage(update: List<Int>): Int {
        val middleIndex = update.size / 2 // Integer division
        return update[middleIndex]
    }

    fun reorderUpdate(orderingRules: List<Pair<Int, Int>>, update: List<Int>): List<Int>? {
        // Filter ordering rules to include only pages in the update
        val pagesInUpdate = update.toSet()
        val applicableRules = orderingRules.filter { (X, Y) ->
            X in pagesInUpdate && Y in pagesInUpdate
        }

        // Build a graph for topological sorting
        val graph = mutableMapOf<Int, MutableList<Int>>()
        val inDegree = mutableMapOf<Int, Int>()

        // Initialize graph and in-degree counts
        for (page in pagesInUpdate) {
            graph[page] = mutableListOf()
            inDegree[page] = 0
        }

        // Build the graph edges and in-degree counts
        for ((X, Y) in applicableRules) {
            graph[X]?.add(Y)
            inDegree[Y] = inDegree.getOrDefault(Y, 0) + 1
        }

        // Perform topological sort
        val queue = ArrayDeque<Int>()
        // Start with nodes with in-degree 0
        for ((page, degree) in inDegree) {
            if (degree == 0) {
                queue.add(page)
            }
        }

        val sortedPages = mutableListOf<Int>()

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            sortedPages.add(current)

            for (neighbor in graph[current]!!) {
                inDegree[neighbor] = inDegree[neighbor]!! - 1
                if (inDegree[neighbor] == 0) {
                    queue.add(neighbor)
                }
            }
        }

        // Check if all pages are included (no cycles)
        if (sortedPages.size != pagesInUpdate.size) {
            return null // Cycle detected
        }

        return sortedPages
    }

    fun validateAndCorrectUpdates(input: List<String>): Pair<Int, Int> {
        val concatenatedInput = input.joinToString("\n")
        val (orderingRules, updates) = parseInput(concatenatedInput)
        val middlePagesCorrect = mutableListOf<Int>()
        val middlePagesIncorrect = mutableListOf<Int>()

        for (update in updates) {
            if (isUpdateCorrect(orderingRules, update)) {
                val middlePage = getMiddlePage(update)
                middlePagesCorrect.add(middlePage)
                println("Update $update is correctly ordered. Middle page: $middlePage")
            } else {
                println("Update $update is NOT correctly ordered.")
                // Reorder the incorrect update
                val correctedUpdate = reorderUpdate(orderingRules, update)
                if (correctedUpdate != null) {
                    val middlePage = getMiddlePage(correctedUpdate)
                    middlePagesIncorrect.add(middlePage)
                    println("Corrected update: $correctedUpdate. Middle page: $middlePage")
                } else {
                    println("Could not reorder update $update due to cyclic dependencies.")
                }
            }
        }

        val totalCorrect = middlePagesCorrect.sum()
        val totalIncorrect = middlePagesIncorrect.sum()

        println("\nSum of middle pages (correct updates): $totalCorrect")
        println("Sum of middle pages (corrected updates): $totalIncorrect")

        return Pair(totalCorrect, totalIncorrect)
    }


    fun part1(input: List<String>): Int {
        val (totalCorrect, _) = validateAndCorrectUpdates(input)

        return totalCorrect
    }

    fun part2(input: List<String>): Int {
        val (_, totalIncorrect) = validateAndCorrectUpdates(input)

        return totalIncorrect
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day05_test")

    check(part1(testInput) == 143)
    check(part2(testInput) == 123)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
