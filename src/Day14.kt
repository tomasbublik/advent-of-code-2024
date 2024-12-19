import readInput
import kotlin.math.absoluteValue

fun main() {

    data class RobotA(
        val initialX: Int,
        val initialY: Int,
        val velocityX: Int,
        val velocityY: Int
    )

    data class Position(val x: Int, val y: Int)

    /**
     * Function for parsing input into a list of Robot objects.
     *
     * @param input List of strings representing robots.
     * @return List of Robot objects.
     */
    fun parseInput(input: List<String>): List<RobotA> {
        val robots = mutableListOf<RobotA>()
        for (line in input) {
            // Separate position and velocity parts
            val parts = line.split(" v=")
            val positionPart = parts[0].substringAfter("p=").trim()
            val velocityPart = parts[1].trim()

            // Extract X and Y position
            val (x, y) = positionPart.split(",").map { it.toInt() }

            // Extract X and Y velocity
            val (vx, vy) = velocityPart.split(",").map { it.toInt() }

            // Add the robot to the list
            robots.add(RobotA(x, y, vx, vy))
        }
        return robots
    }

    /**
     * Function to calculate the final position of the robot after a given time with wrap-around on the grid.
     *
     * @param robot Robot object.
     * @param time Time in seconds.
     * @param width Width of the grid.
     * @param height Height of the grid.
     * @return Position of the robot after the given time.
     */
    fun calculateFinalPosition(robot: RobotA, time: Int, width: Int, height: Int): Position {
        val rawX = robot.initialX + robot.velocityX * time
        val rawY = robot.initialY + robot.velocityY * time

        // Function for proper modulo (negative values also return correct positive result)
        fun mod(a: Int, m: Int): Int {
            val res = a % m
            return if (res < 0) res + m else res
        }

        val finalX = mod(rawX, width)
        val finalY = mod(rawY, height)

        return Position(finalX, finalY)
    }

    /**
     * Function to count robots in individual quadrants.
     *
     * @param positions List of final positions of the robots.
     * @param width Width of the grid.
     * @param height Height of the grid.
     * @return Map containing the counts of robots in Q1, Q2, Q3, and Q4.
     */
    fun countRobotsInQuadrants(positions: List<Position>, width: Int, height: Int): Map<String, Int> {
        // Determination of middle lines
        val midX = width / 2 // 101 /2 =50
        val midY = height / 2 // 103 /2=51

        // Initialize counts for each quadrant
        val counts = mutableMapOf("Q1" to 0, "Q2" to 0, "Q3" to 0, "Q4" to 0)

        for ((x, y) in positions) {
            // Check if the robot is not on the middle lines
            if (x == midX || y == midY) continue

            when {
                x < midX && y < midY -> counts["Q1"] = counts["Q1"]!! + 1
                x > midX && y < midY -> counts["Q2"] = counts["Q2"]!! + 1
                x < midX && y > midY -> counts["Q3"] = counts["Q3"]!! + 1
                x > midX && y > midY -> counts["Q4"] = counts["Q4"]!! + 1
                // Robots exactly on the middle lines are not counted
            }
        }

        return counts
    }

    fun part1(input: List<String>): Long {
        // Parsing input into a list of robots
        val robots = parseInput(input)

        // Calculating positions of robots after 100 seconds
        val finalPositions = robots.map { calculateFinalPosition(it, 100, 101, 103) }

        // Determining quadrants and counting robots
        val quadrantCounts = countRobotsInQuadrants(finalPositions, 101, 103)

        // Calculating the safety factor
        val safetyFactor = quadrantCounts.values.fold(1L) { acc, count -> acc * count }

        println("Počty robotů v kvadrantech:")
        println("Q1: ${quadrantCounts["Q1"]}")
        println("Q2: ${quadrantCounts["Q2"]}")
        println("Q3: ${quadrantCounts["Q3"]}")
        println("Q4: ${quadrantCounts["Q4"]}")

        println("Bezpečnostní faktor: $safetyFactor")

        return safetyFactor
    }

     fun findEarliestEasterEggTime(robots: List<RobotA>, width: Int, height: Int): Int? {
        var t = 0
        var minArea = Long.MAX_VALUE
        var earliestTime: Int? = null
        var consecutiveIncreasing = 0
        val maxTime = 20000 // Maximum time for simulation

        while (t <= maxTime) {
            val positions = robots.map { calculateFinalPosition(it, t, width, height) }

            val minX = positions.minByOrNull { it.x }?.x ?: 0
            val maxX = positions.maxByOrNull { it.x }?.x ?: 0
            val minY = positions.minByOrNull { it.y }?.y ?: 0
            val maxY = positions.maxByOrNull { it.y }?.y ?: 0

            val area = (maxX - minX).toLong() * (maxY - minY).toLong()

            if (area < minArea) {
                minArea = area
                earliestTime = t
                consecutiveIncreasing = 0
            } else {
                consecutiveIncreasing++
                // If the area hasn’t decreased for 100 seconds, we assume we found the optimal time
                if (consecutiveIncreasing >= 100) {
                    break
                }
            }

            t++
        }

        return earliestTime
    }

    fun part2a(input: List<String>): Int {

        // Parsování vstupu do seznamu robotů
        val robots = parseInput(input)


        // Definice rozměrů prostoru
        val width = 101
        val height = 103

        // Part one: Calculating the safety factor after 100 seconds
        println("=== První část: Bezpečnostní faktor po 100 sekundách ===")
        val finalPositionsAt100 = robots.map { calculateFinalPosition(it, 100, width, height) }
        val quadrantCountsAt100 = countRobotsInQuadrants(finalPositionsAt100, width, height)
        val safetyFactorAt100 = quadrantCountsAt100.values.fold(1L) { acc, count -> acc * count }
        println("Počty robotů v kvadrantech po 100 sekundách:")
        println("Q1: ${quadrantCountsAt100["Q1"]}")
        println("Q2: ${quadrantCountsAt100["Q2"]}")
        println("Q3: ${quadrantCountsAt100["Q3"]}")
        println("Q4: ${quadrantCountsAt100["Q4"]}")
        println("Bezpečnostní faktor: $safetyFactorAt100")
        println()

        // Part two: Finding the smallest number of seconds to display the Easter egg (Christmas tree)
        println("=== Druhá část: Nejmenší počet sekund pro Easter egg ===")
        val earliestTime = findEarliestEasterEggTime(robots, width, height)
        if (earliestTime != null) {
            println("Nejmenší počet sekund pro Easter egg: $earliestTime")
        } else {
            println("Easter egg nebyl nalezen v zadaném časovém rámci.")
        }

        return earliestTime!!
    }

    fun part2b(input: List<String>): Int {
        val width = 101
        val height = 103

        data class Robot(val x: Int, val y: Int, val dx: Int, val dy: Int)

        val robots = input.map { line ->
            val parts = line.split(" ")
            val pPart = parts[0].substringAfter("p=").split(",")
            val x = pPart[0].toInt()
            val y = pPart[1].toInt()
            val vPart = parts[1].substringAfter("v=").split(",")
            val dx = vPart[0].toInt()
            val dy = vPart[1].toInt()
            Robot(x, y, dx, dy)
        }

        fun mod(a: Int, m: Int): Int {
            val r = a % m
            return if (r < 0) r + m else r
        }

        fun positionAt(r: Robot, t: Int): Pair<Int, Int> {
            val fx = mod(r.x + r.dx * t, width)
            val fy = mod(r.y + r.dy * t, height)
            return fx to fy
        }

        // Returns twice the oriented area of triangle ABC
        fun area2(ax: Int, ay: Int, bx: Int, by: Int, cx: Int, cy: Int): Int {
            return (bx - ax)*(cy - ay) - (by - ay)*(cx - ax)
        }

        // Test if point P lies inside triangle ABC (including edges)
        fun pointInTriangle(px: Int, py: Int,
                            ax: Int, ay: Int,
                            bx: Int, by: Int,
                            cx: Int, cy: Int): Boolean {
            val A = area2(ax, ay, bx, by, cx, cy).toLong().absoluteValue
            val A1 = area2(px, py, bx, by, cx, cy).toLong().absoluteValue
            val A2 = area2(ax, ay, px, py, cx, cy).toLong().absoluteValue
            val A3 = area2(ax, ay, bx, by, px, py).toLong().absoluteValue
            return A == A1 + A2 + A3
        }

        // Define a triangle:
        val ax = 0; val ay = height - 1
        val bx = width - 1; val by = height - 1
        val cx = width / 2; val cy = 0

        val total = robots.size
        val threshold = (total * 0.75).toInt() // 75% hranice

        // Periodicity: max time 10403
        val maxT = 10403
        for (t in 0 until maxT) {
            val positions = robots.map { positionAt(it, t) }
            var countInside = 0
            for ((fx, fy) in positions) {
                if (pointInTriangle(fx, fy, ax, ay, bx, by, cx, cy)) {
                    countInside++
                }
                if (countInside >= threshold) {
                    println("Found time = $t where ≥75% robots are inside the triangle.")
                    return t
                }
            }
        }
        // Easter egg was not found within the given time frame.
        println("No time found up to $maxT seconds with ≥75% robots inside the triangle.")
        return maxT
    }

    fun evaluateXPattern(xs: List<Int>): Int {
        // For example, the number of unique x coordinates
        return xs.toSet().size
    }
    fun evaluateYPattern(ys: List<Int>): Int {
        // For example, the number of unique y coordinates
        return ys.toSet().size
    }

    // Funkce pro inverzi modulo, jelikož m a n jsou prvočísla, lze použít Fermatovu malou větu nebo Extended Euclid
    fun modInverse(a: Int, m: Int): Int {
        // Extended Euclidean Algorithm
        fun egcd(a: Int, b: Int): Pair<Int, Int> {
            if (a == 0) return Pair(0, 1)
            val (x1,y1) = egcd(b % a, a)
            return Pair(y1 - (b/a)*x1, x1)
        }
        val (x,y)= egcd(a,m)
        // x je inverze a mod m
        return ((x % m) + m) % m
    }

    // Řešení dvojice kongruencí t = a mod m a t = b mod n
// kde m=101, n=103 jsou nesoudělné.
// Čínská věta o zbytcích:
    fun solveChineseRemainder(a: Int, m: Int, b: Int, n: Int): Int {
        // t mod m = a
        // t mod n = b
        // Jelikož m a n jsou nesoudělná, existuje jediné řešení modulo m*n.
        // Lze použít jednoduchý algoritmus:
        // Najdeme multiplicativní inverzi m modulo n (nebo n modulo m)
        // t = a + m * k
        // a + m*k ≡ b (mod n)
        // m*k ≡ b - a (mod n)
        // k ≡ (b - a)*m^{-1} (mod n)
        val mInv = modInverse(m, n)
        val diff = ((b - a) % n + n) % n
        val k = (diff * mInv) % n
        val t = a + m*k
        return t % (m*n)
    }

    fun part2c(input: List<String>): Int {
        val width = 101
        val height = 103

        data class Robot(val x: Int, val y: Int, val dx: Int, val dy: Int)

        val robots = input.map { line ->
            val parts = line.split(" ")
            val pPart = parts[0].substringAfter("p=").split(",")
            val x = pPart[0].toInt()
            val y = pPart[1].toInt()
            val vPart = parts[1].substringAfter("v=").split(",")
            val dx = vPart[0].toInt()
            val dy = vPart[1].toInt()
            Robot(x, y, dx, dy)
        }

        fun mod(a: Int, m: Int): Int {
            val r = a % m
            return if (r < 0) r + m else r
        }

        // Pro každý robot a dané t získáme X(t) a Y(t).
        // Budeme hledat nějaký vzor. Bez konkrétního vzoru (např. "vánoční stromeček") je to abstraktní.
        // Předpokládejme, že máme funkce, které umí ohodnotit "kvalitu" uspořádání ve směru x a y.
        // Např. chceme najít tx a ty, pro které je konfigurace "nejlepší".

        // Nejprve procházíme všechny možné tx (0..100) a ohodnotíme x-konfiguraci.
        var bestTx = 0
        var bestXScore = Int.MIN_VALUE
        for (tx in 0 until width) {
            // Pozice robotů mod width v čase tx
            val xs = robots.map { mod(it.x + it.dx * tx, width) }
            // Ohodnotíme nějak pattern na ose X
            val score = evaluateXPattern(xs)
            if (score > bestXScore) {
                bestXScore = score
                bestTx = tx
            }
        }

        // Analogicky pro y
        var bestTy = 0
        var bestYScore = Int.MIN_VALUE
        for (ty in 0 until height) {
            val ys = robots.map { mod(it.y + it.dy * ty, height) }
            val score = evaluateYPattern(ys)
            if (score > bestYScore) {
                bestYScore = score
                bestTy = ty
            }
        }

        // Nyní máme bestTx a bestTy. Potřebujeme najít t tak, že:
        // t ≡ bestTx (mod 101)
        // t ≡ bestTy (mod 103)

        val t = solveChineseRemainder(bestTx, 101, bestTy, 103)
        println("The best time: $t")

        return t
    }

    data class RobotC(val x0: Int, val y0: Int, val vx: Int, val vy: Int)

    fun part2d(input: List<String>): Int {

        val width = 101
        val height = 103

        val robots = input.map { line ->
            val parts = line.split(" ")
            val pPart = parts[0].removePrefix("p=").split(",")
            val vPart = parts[1].removePrefix("v=").split(",")
            val x = pPart[0].toInt()
            val y = pPart[1].toInt()
            val vx = vPart[0].toInt()
            val vy = vPart[1].toInt()
            RobotC(x, y, vx, vy)
        }

        // Funkce pro výpočet bounding boxu v ose X pro daný čas t modulo 101
        fun boundingWidthAt(t: Int): Int {
            // x(t) = (x0 + vx*t) mod 101
            val xs = robots.map { ((it.x0 + it.vx * t) % width + width) % width }
            val minX = xs.minOrNull()!!
            val maxX = xs.maxOrNull()!!
            return maxX - minX + 1
        }

        // Funkce pro výpočet bounding boxu v ose Y pro daný čas t modulo 103
        fun boundingHeightAt(t: Int): Int {
            // y(t) = (y0 + vy*t) mod 103
            val ys = robots.map { ((it.y0 + it.vy * t) % height + height) % height }
            val minY = ys.minOrNull()!!
            val maxY = ys.maxOrNull()!!
            return maxY - minY + 1
        }

        // Najdeme čas tX v [0..100], kdy je boundingWidthAt(tX) minimální.
        var minWidth = Int.MAX_VALUE
        var tX = 0
        for (t in 0 until width) {
            val w = boundingWidthAt(t)
            if (w < minWidth) {
                minWidth = w
                tX = t
            }
        }

        // Najdeme čas tY v [0..102], kdy je boundingHeightAt(tY) minimální.
        var minHeight = Int.MAX_VALUE
        var tY = 0
        for (t in 0 until height) {
            val h = boundingHeightAt(t)
            if (h < minHeight) {
                minHeight = h
                tY = t
            }
        }

        fun Long.modPow(exp: Long, mod: Int): Long {
            var base = this % mod
            var e = exp
            var result = 1L
            while (e > 0) {
                if ((e and 1L) == 1L) result = (result * base) % mod
                base = (base * base) % mod
                e = e shr 1
            }
            return result
        }

        // Řešíme soustavu:
        // t ≡ tX (mod 101)
        // t ≡ tY (mod 103)
        // 101 a 103 jsou prvočísla, můžeme aplikovat CRT.

        // Obecný postup CRT:
        // m1 = 101, m2 = 103
        // a1 = tX, a2 = tY
        // N = m1*m2 = 101*103 = 10403
        // N1 = N/m1 = 10403/101 = 103
        // N2 = N/m2 = 10403/103 = 101
        // Najdeme inverzní prvky:
        // N1 * inv(N1, m1) ≡ 1 (mod m1)
        // N2 * inv(N2, m2) ≡ 1 (mod m2)
        fun modInverse(a: Int, m: Int): Int {
            // Protože m je prvočíslo, můžeme použít Fermatovu malou větu:
            // a^(m-1) ≡ 1 (mod m) => a^(m-2) je inverz k a mod m
            return a.toLong().modPow(m-2L, m).toInt()
        }

        val m1 = 101
        val m2 = 103
        val a1 = tX
        val a2 = tY
        val N = m1*m2
        val N1 = N/m1
        val N2 = N/m2
        val invN1 = modInverse(N1, m1)
        val invN2 = modInverse(N2, m2)

        val t = ((a1 * N1 * invN1) + (a2 * N2 * invN2)) % N

        println("Nejmenší t, kdy se objeví Easter egg (podle CRT) je: $t")

        return t
    }

    data class Robot(val x: Int, val y: Int, val vx: Int, val vy: Int)

    fun part2(input: List<String>): Int {
        val robots = input.map { line ->
            val (a, b) = line.split(" ")
            val (x, y) = a.removePrefix("p=").split(",").map { it.toInt() }
            val (vx, vy) = b.removePrefix("v=").split(",").map { it.toInt() }
            Robot(x, y, vx, vy)
        }

        val width = 101
        val height = 103

        var t = 0
        while (true) {
            t++
            val positions = HashSet<Pair<Int, Int>>()
            var valid = true

            for (r in robots) {
                val newX = ((r.x + t * (r.vx + width)) % width + width) % width
                val newY = ((r.y + t * (r.vy + height)) % height + height) % height
                val pos = newX to newY
                if (!positions.add(pos)) {
                    // Pozice už tam je, tudíž dva roboti na jednom místě
                    valid = false
                    break
                }
            }

            if (valid) {
                return t
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
//    check(part1(testInput) == 12L)
//    check(part2(testInput) == BigInteger.valueOf(81))

    val input = readInput("Day14_test")
    println(part1(input))
    println(part2(input))
}
