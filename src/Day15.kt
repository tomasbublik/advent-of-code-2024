import readInput

fun main() {

    fun part1(input: List<String>): Int {
// Separate the map and moves

// Find the first line of the map (contains #) and the last line of the map (contains #)

// The remaining lines are moves.

// Find the block of the map:
        val mapLines = mutableListOf<String>()
        var idx = 0
// The map will remain until the first line that does not start with '#' and does not end with '#',

// but in this example, the entire map may be surrounded by walls.

// To be sure, take lines as long as they contain at least one '#'.

// After going through the map, the remaining lines only contain moves (arrows and spaces).

// In puzzle examples, it's typical for the map to be a block enclosed in #, followed by several lines of moves.

// We will read the map as long as a line contains '#'.
        while (idx < input.size && input[idx].contains('#')) {
            mapLines.add(input[idx])
            idx++
        }

// The rest are moves
        val movesLines = input.drop(idx)
        val movesStr = movesLines.joinToString("") { it.trim() } // spojíme všechny do jednoho řetězce bez mezer a konců řádků

        val height = mapLines.size
        val width = mapLines[0].length
        val map = mapLines.map { it.toCharArray() }.toTypedArray()

// Find the robot @
        var robotY = 0
        var robotX = 0
        outer@for (y in 0 until height) {
            for (x in 0 until width) {
                if (map[y][x] == '@') {
                    robotY = y
                    robotX = x
                    break@outer
                }
            }
        }

// Function to move a string of boxes starting from the box at (by, bx)

// Returns true if the move was successful, false otherwise.
        fun pushBoxes(by: Int, bx: Int, dy: Int, dx: Int): Boolean {
// Find the chain of boxes in a straight line in the direction of motion (dy, dx)

// Essentially, we move in the direction of motion as long as there is a box.

// Find the most distant box in this direction.
            var chain = mutableListOf<Pair<Int,Int>>()
            var cy = by
            var cx = bx
            while (map[cy][cx] == 'O') {
                chain.add(cy to cx)
                cy += dy
                cx += dx
            }
            if (map[cy][cx] == '#') return false
            map[cy][cx] = 'O'
            for (i in chain.size-1 downTo 1) {
                val (y1,x1) = chain[i]
                val (y2,x2) = chain[i-1]
                map[y1][x1] = map[y2][x2] // 'O'
            }
            // So the original place of the first box is freed.
            map[by][bx] = '.'

            return true
        }

        fun tryMove(dy: Int, dx: Int) {
            val ny = robotY + dy
            val nx = robotX + dx
            if (map[ny][nx] == '#') {
                return
            } else if (map[ny][nx] == 'O') {
                if (pushBoxes(ny, nx, dy, dx)) {
                    map[robotY][robotX] = '.'
                    robotY = ny
                    robotX = nx
                    map[robotY][robotX] = '@'
                } else {
                    return
                }
            } else {
            // Free space, the robot moves
                map[robotY][robotX] = '.'
                robotY = ny
                robotX = nx
                map[robotY][robotX] = '@'
            }
        }

        // Traverse all moves from movesStr
        for (c in movesStr) {
            val (dy,dx) = when(c) {
                '^' -> Pair(-1,0)
                'v' -> Pair(1,0)
                '<' -> Pair(0,-1)
                '>' -> Pair(0,1)
                else -> Pair(0,0) // invalid character - ignore
            }
            if (dy!=0 || dx!=0) {
                tryMove(dy,dx)
            }
        }

        var sumGPS = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (map[y][x] == 'O') {
                    val gps = 100*y + x
                    sumGPS += gps
                }
            }
        }

        return sumGPS
    }

    fun part2(input: List<String>): Int {
        // Prvně jako v první části: oddělíme mapu a pohyby.
        val mapLines = mutableListOf<String>()
        var idx = 0
        while (idx < input.size && input[idx].contains('#')) {
            mapLines.add(input[idx])
            idx++
        }

        val movesLines = input.drop(idx)
        val movesStr = movesLines.joinToString("") { it.trim() }

        val originalHeight = mapLines.size
        val originalWidth = mapLines[0].length

        // Zvětšení šířky:
        // # -> ##
        // O -> []
        // . -> ..
        // @ -> @.
        fun expandLine(line: String): String {
            val sb = StringBuilder()
            for (ch in line) {
                when(ch) {
                    '#' -> sb.append("##")
                    'O' -> sb.append("[]")
                    '.' -> sb.append("..")
                    '@' -> sb.append("@.")
                    else -> sb.append(ch.toString()+".") // pokud by nastal neznámý znak, zachováme princip
                }
            }
            return sb.toString()
        }

        val expandedMap = mapLines.map { expandLine(it) }.toTypedArray()
        val height = originalHeight
        val width = originalWidth * 2 // šířka v znacích se zdvojnásobila

        var robotY = 0
        var robotX = 0
        outer@for (y in 0 until height) {
            for (xChar in 0 until width step 2) {
                if (expandedMap[y][xChar] == '@') {
                    robotY = y
                    robotX = xChar/2
                    break@outer
                }
            }
        }

        fun getCell(y: Int, x: Int): String {
            val line = expandedMap[y]
            return line.substring(x*2, x*2+2)
        }

        fun setCell(y: Int, x: Int, cell: String) {
            val arr = expandedMap[y].toCharArray()
            arr[x*2] = cell[0]
            arr[x*2+1] = cell[1]
            expandedMap[y] = String(arr)
        }

        fun isWall(cell: String) = cell == "##"
        fun isBox(cell: String) = cell == "[]"
        fun isRobot(cell: String) = cell.startsWith('@') // '@.'
        fun isEmpty(cell: String) = cell == ".."

        fun pushBoxes(by: Int, bx: Int, dy: Int, dx: Int): Boolean {
            var chain = mutableListOf<Pair<Int,Int>>()
            var cy = by
            var cx = bx
            while (true) {
                val c = getCell(cy,cx)
                if (isBox(c)) {
                    chain.add(cy to cx)
                    cy += dy
                    cx += dx
                } else {
                    break
                }
            }

            val target = getCell(cy,cx)
            if (isWall(target)) {
                return false
            }
            if (!isEmpty(target) && !isRobot(target) && !isBox(target)) {
                return false
            }

            setCell(cy,cx,"[]")
            for (i in chain.size-1 downTo 1) {
                val (y1,x1) = chain[i]
                val (y2,x2) = chain[i-1]
                setCell(y1,x1,"[]")
            }
            val (fy,fx) = chain[0]
            setCell(fy,fx,"..")

            return true
        }

        fun tryMove(dy: Int, dx: Int) {
            val ny = robotY + dy
            val nx = robotX + dx
            val front = getCell(ny,nx)
            if (isWall(front)) {
                return
            } else if (isBox(front)) {
                if (pushBoxes(ny,nx,dy,dx)) {
                    setCell(robotY, robotX, "..")
                    robotY = ny
                    robotX = nx
                    setCell(robotY, robotX, "@.")
                } else {
                    return
                }
            } else {
                setCell(robotY, robotX, "..")
                robotY = ny
                robotX = nx
                setCell(robotY, robotX, "@.")
            }
        }

        for (c in movesStr) {
            val (dy,dx) = when(c) {
                '^' -> Pair(-1,0)
                'v' -> Pair(1,0)
                '<' -> Pair(0,-1)
                '>' -> Pair(0,1)
                else -> Pair(0,0)
            }
            if (dy!=0 || dx!=0) {
                tryMove(dy,dx)
            }
        }

        var sumGPS = 0
        for (y in 0 until height) {
            for (x in 0 until (width/2)) { // x je cell-index
                val cell = getCell(y,x)
                if (isBox(cell)) {
                    sumGPS += (100*y + (x*2))
                }
            }
        }

        return sumGPS
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput) == 10092)
    check(part2(testInput) == 9021)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}
