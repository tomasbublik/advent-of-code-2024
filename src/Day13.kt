package cz.tomasbublik

import readInput
import java.math.BigInteger

fun main() {

    // Data class to represent a Claw Machine
    data class ClawMachine(
        val buttonA_X: Long,
        val buttonA_Y: Long,
        val buttonB_X: Long,
        val buttonB_Y: Long,
        val prize_X: Long,
        val prize_Y: Long
    )

    data class Machine(val Ax: BigInteger, val Ay: BigInteger, val Bx: BigInteger, val By: BigInteger, val Xp: BigInteger, val Yp: BigInteger)

    fun parseButton(line: String): Pair<Long, Long> {
        // Example: "Button A: X+94, Y+34"
        val parts = line.split(":")[1].split(",").map { it.trim() }
        val x = parts[0].substringAfter("X+").toLong()
        val y = parts[1].substringAfter("Y+").toLong()
        return Pair(x, y)
    }

    fun parsePrize(line: String): Pair<Long, Long> {
        // Example: "Prize: X=8400, Y=5400"
        val parts = line.split(":")[1].split(",").map { it.trim() }
        val x = parts[0].substringAfter("X=").toLong()
        val y = parts[1].substringAfter("Y=").toLong()
        return Pair(x, y)
    }

    fun parseInput(input: String): List<ClawMachine> {
        val machines = mutableListOf<ClawMachine>()
        val lines = input.lines().filter { it.isNotBlank() }

        // Process every 3 lines as one device
        for (i in lines.indices step 3) {
            if (i + 2 >= lines.size) break // Zajistit dostatečný počet řádků

            val buttonA = parseButton(lines[i])
            val buttonB = parseButton(lines[i + 1])
            val prize = parsePrize(lines[i + 2])

            machines.add(
                ClawMachine(
                    buttonA_X = buttonA.first,
                    buttonA_Y = buttonA.second,
                    buttonB_X = buttonB.first,
                    buttonB_Y = buttonB.second,
                    prize_X = prize.first,
                    prize_Y = prize.second
                )
            )
        }

        return machines
    }

    fun extendedGCD(a: Long, b: Long): Triple<Long, Long, Long> {
        if (b == 0L) return Triple(a, 1L, 0L)
        val (g, x1, y1) = extendedGCD(b, a % b)
        val x = y1
        val y = x1 - (a / b) * y1
        return Triple(g, x, y)
    }

    fun ceilDiv(dividend: Long, divisor: Long): Long {
        return if (dividend % divisor == 0L) {
            dividend / divisor
        } else {
            dividend / divisor + 1
        }
    }

     fun floorDiv(dividend: Long, divisor: Long): Long {
        return dividend / divisor
    }

    fun findMinimumTokens(machine: ClawMachine): Long? {
        val Ax = machine.buttonA_X
        val Ay = machine.buttonA_Y
        val Bx = machine.buttonB_X
        val By = machine.buttonB_Y
        val Px = machine.prize_X
        val Py = machine.prize_Y

        val det = Ax * By - Bx * Ay

        return if (det != 0L) {
            // Det != 0, unique solution
            val aNumerator = Px * By - Bx * Py
            val bNumerator = Ax * Py - Ay * Px

            if (aNumerator % det != 0L || bNumerator % det != 0L) {
                // Solution is not an integer
                null
            } else {
                val a = aNumerator / det
                val b = bNumerator / det

                if (a >= 0 && b >= 0) {
                    3 * a + b
                } else {
                    null
                }
            }
        } else {
            // Det == 0, the system is either inconsistent or has infinitely many solutions
            // First, check consistency
            if (Ax * Py != Ay * Px || Bx * Py != By * Px) {
                // Inconsistent system, no solution
                null
            } else {
                // The system has infinitely many solutions
                // Find a, b >=0, integers that solve Ax * a + Bx * b = Px
                // Minimize 3a + b

                // If Ax ==0 and Bx ==0, and Px !=0, no solution
                if (Ax == 0L && Bx == 0L) {
                    return if (Px == 0L && Ay * 0L + By * 0L == Py) 0 else null
                }

                // If Ax ==0, then Bx * b = Px
                if (Ax == 0L) {
                    if (Bx == 0L) {
                        return null
                    }
                    if (Px % Bx !=0L) {
                        return null
                    }
                    val b = Px / Bx
                    val a = 0L
                    if (Ay * a + By * b == Py) {
                        return 3 * a + b
                    } else {
                        return null
                    }
                }

                // If Bx ==0, then Ax * a = Px
                if (Bx == 0L) {
                    if (Ax ==0L) {
                        return null
                    }
                    if (Px % Ax !=0L) {
                        return null
                    }
                    val a = Px / Ax
                    val b = 0L
                    if (Ay * a + By * b == Py) {
                        return 3 * a + b
                    } else {
                        return null
                    }
                }

                // General case: Ax !=0 and Bx !=0
                // Find all b that satisfy Ax * a + Bx * b = Px, where a = (Px - Bx * b)/Ax must be an integer and >=0
                // Minimize 3a + b

                // Use Extended GCD to solve Ax * a + Bx * b = Px

                val (gcd, x, y) = extendedGCD(Bx, Ax)
                if (Px % gcd !=0L) {
                    // Žádné řešení
                    null
                } else {
                    // Najdeme základní řešení
                    val scale = Px / gcd
                    val a0 = y * scale
                    val b0 = x * scale

                    // Generalní řešení:
                    // a = a0 + k * (Bx / gcd)
                    // b = b0 - k * (Ax / gcd)
                    //
                    // Musí být a >=0 a b >=0
                    // K >= ceil(-a0 / (Bx / gcd)) a <= floor(b0 / (Ax / gcd))

                    val kMin = ceilDiv(-a0, Bx / gcd).toLong()
                    val kMax = floorDiv(b0, Ax / gcd).toLong()

                    if (kMin > kMax) {
                        // No solution in non-negative integers
                        null
                    } else {
                        // Minimalizujeme 3a + b
                        // Vyjádříme 3a + b = 3(a0 + k * (Bx/gcd)) + (b0 - k * (Ax/gcd)) = 3a0 + b0 + k*(3Bx/gcd - Ax/gcd) = constant + k*(3Bx - Ax)/gcd

                        val coeff = (3 * Bx - Ax) / gcd

                        // Pokud coeff >0, chceme minimalizovat k
                        // Pokud coeff <0, chceme maximalizovat k
                        // Pokud coeff ==0, jakýkoli k dává stejné 3a + b

                        val optimalK = when {
                            coeff >0 -> kMin
                            coeff <0 -> kMax
                            else -> kMin // libovolné k, zvolíme kMin
                        }

                        // Vypočítáme a a b pro optimalK
                        val a = a0 + optimalK * (Bx / gcd)
                        val b = b0 - optimalK * (Ax / gcd)

                        if (a >=0 && b >=0 && Ax * a + Bx * b == Px && Ay * a + By * b == Py) {
                            3 * a + b
                        } else {
                            null
                        }
                    }
                }
            }
        }
    }

    fun calculateTotalMinimumTokens(machines: List<ClawMachine>): Long {
        var totalTokens = 0L

        for ((index, machine) in machines.withIndex()) {
            val minTokens = findMinimumTokens(machine)
            if (minTokens != null) {
                println("Stroj ${index + 1}: Minimální počet tokenů = $minTokens")
                totalTokens += minTokens
            } else {
                println("Stroj ${index + 1}: Cena je nedosažitelná")
            }
        }

        return totalTokens
    }

    fun part1(input: List<String>): Long {
        // Parse the input into a list of ClawMachines
        val machines = parseInput(input.joinToString("\n"))

        // Calculate the total minimum tokens required to win all possible prizes
        val totalTokens = calculateTotalMinimumTokens(machines)

        println("Total Minimum Tokens to Win All Possible Prizes: $totalTokens")
        return totalTokens
    }

    fun solveMachine(m: Machine): BigInteger? {
        // D = Ax*By - Bx*Ay
        val D = m.Ax*m.By - m.Bx*m.Ay

        if (D != BigInteger.ZERO) {
            // Jedno řešení
            // a = (Xp*By - Yp*Bx)/D
            // b = (Ax*Yp - Ay*Xp)/D
            val aNum = m.Xp*m.By - m.Yp*m.Bx
            val bNum = m.Ax*m.Yp - m.Ay*m.Xp

            if (aNum.mod(D) != BigInteger.ZERO || bNum.mod(D) != BigInteger.ZERO) {
                // Není celočíselné řešení
                return null
            }

            val a = aNum.divide(D)
            val b = bNum.divide(D)

            if (a < BigInteger.ZERO || b < BigInteger.ZERO) {
                return null
            }

            // Cena
            return a.multiply(BigInteger.valueOf(3)).add(b)
        } else {
            // D=0 => (Ax,Ay) a (Bx,By) jsou lineárně závislé.
            // Zkontrolujeme konzistenci.
            // Pokud je (Xp,Yp) také na téže přímce, pak řešíme lineární rodinu řešení.
            // Např. zkontrolujeme poměr X a Y souřadnic:
            // Pokud Ax=0 a Ay=0, pak musíme Bx,By použít atd.
            // Zde je nástin:

            val vectorA = Pair(m.Ax, m.Ay)
            val vectorB = Pair(m.Bx, m.By)
            val vectorP = Pair(m.Xp, m.Yp)

            // Zkontrolujeme, zda (Xp,Yp) je lineární kombinace (Ax,Ay):
            // Pokud Ax nebo Ay není nula, můžeme poměrem ověřit.
            // Pokud Ax != 0: λ = Xp/Ax, zkontrolovat jestli Yp = λ*Ay
            // nebo pokud Ay !=0 obdobně.
            // Pokud Ax=0 a Ay!=0: λ=Yp/Ay -> zkontroluj Xp
            // atd.

            // Předpokládejme Ax nebo Ay !=0 (jinak analogicky):
            val lambda: BigInteger? = when {
                m.Ax != BigInteger.ZERO -> {
                    if (m.Xp.mod(m.Ax) != BigInteger.ZERO) return null
                    val l = m.Xp.divide(m.Ax)
                    if (m.Ay*l == m.Yp) l else null
                }
                m.Ay != BigInteger.ZERO -> {
                    if (m.Yp.mod(m.Ay)!=BigInteger.ZERO) return null
                    val l = m.Yp.divide(m.Ay)
                    if (m.Ax*l == m.Xp) l else null
                }
                else -> {
                    // Ax=0,Ay=0 => oba vektory nulové, pak Xp,Yp musí být 0, jinak žádné řešení
                    if (m.Xp==BigInteger.ZERO && m.Yp==BigInteger.ZERO) {
                        // každý a,b=0 je řešení
                        return BigInteger.ZERO // cena 0
                    } else return null
                }
            } ?: return null

             return null
        }
    }

    fun part2(input: List<String>): Long {
        // Parse the input into a list of ClawMachines
        val machines = parseInput(input.joinToString("\n"))

        // Přidání 10^13 k pozicím cenin pro druhou část
        val adjustedMachines = machines.map { machine ->
            machine.copy(
                prize_X = machine.prize_X + 10_000_000_000_000.toInt(),
                prize_Y = machine.prize_Y + 10_000_000_000_000.toInt()
            )
        }

        // Calculate the total minimum tokens required to win all possible prizes
        val totalTokens = calculateTotalMinimumTokens(adjustedMachines)

        println("Total Minimum Tokens to Win All Possible Prizes: $totalTokens")
        return totalTokens
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 480L)
//    check(part2(testInput) == BigInteger.valueOf(81))

    val input = readInput("Day13_test")
    println(part1(input))
    println(part2(input))
}
