import readInput

fun main() {

    fun part1(input: List<String>): Long {

        // 1. Parsing the disk map
        // Alternating digits: (file-len, free-len, file-len, free-len, ...)
        val digits = input[0].map { it.digitToInt() }

        // Disk represented as a mutable list of Int
        // file blocks: >= 0 (fileId)
        // free blocks: -1
        val disk = mutableListOf<Int>()

        var fileId = 0
        for ((index, length) in digits.withIndex()) {
            if (index % 2 == 0) {
                // file-len
                // Add fileId this many times
                repeat(length) {
                    disk.add(fileId)
                }
                fileId++
            } else {
                // free-len
                repeat(length) {
                    disk.add(-1)
                }
            }
        }

        // Function to determine whether a gap exists
        fun hasGap(): Boolean {
            val maxIndexOfFile = disk.indexOfLast { it != -1 }
            if (maxIndexOfFile == -1) return false // no files => no gaps
            // Find '.' (=-1) at an index < maxIndexOfFile
            return disk.subList(0, maxIndexOfFile).contains(-1)
        }

        while (hasGap()) {
            // Find the rightmost file block
            val maxIndexOfFile = disk.indexOfLast { it != -1 }
            // Find the leftmost '.' at an index < maxIndexOfFile
            val leftmostDot = disk.subList(0, maxIndexOfFile).indexOf(-1)
            // subList returns an index relative to 0, so it's the same index
            // Move
            disk[leftmostDot] = disk[maxIndexOfFile]
            disk[maxIndexOfFile] = -1
        }

        // Compute the checksum
        var checksum = 0L
        for ((pos, block) in disk.withIndex()) {
            if (block != -1) {
                val fid = block // fileId
                checksum += pos.toLong() * fid
            }
        }

        println("Checksum is: $checksum")

        return checksum
    }

    fun part2(input: List<String>): Long {
        val digits = input[0].map { it.digitToInt() }

        val disk = mutableListOf<Int>()
        var fileId = 0

        // Parse input to disk: file-len, free-len, ...
        for ((index, length) in digits.withIndex()) {
            if (index % 2 == 0) {
                // file-len
                repeat(length) {
                    disk.add(fileId)
                }
                fileId++
            } else {
                // free-len
                repeat(length) {
                    disk.add(-1)
                }
            }
        }

        val numBlocks = disk.size

        // Determine the maximum fileId
        val maxFileId = (disk.filter { it >= 0 }.maxOrNull() ?: -1)

        // Function to find a file (startIndex, length) by fileId
        fun findFile(fileId: Int): Pair<Int, Int>? {
            // A file is a continuous segment of disk[..] == fileId
            // Find the first occurrence
            val start = disk.indexOf(fileId)
            if (start == -1) return null // file not found
            // Now go from start onward as long as it matches fileId
            var end = start
            while (end + 1 < numBlocks && disk[end + 1] == fileId) {
                end++
            }
            val length = end - start + 1
            return start to length
        }

        // Function to find the leftmost free span that fits before the file's startIndex
        fun findLeftmostFreeSpan(maxEndIndex: Int, lengthNeeded: Int): Int? {
            // maxEndIndex is the file's startIndex - we want a segment < maxEndIndex
            // Search for contiguous -1 from the left up to (maxEndIndex-1)
            // maxEndIndex can also be 0 => then there's no space to the left
            if (maxEndIndex <= 0) return null
            var start = -1
            var count = 0
            for (i in 0 until maxEndIndex) {
                if (disk[i] == -1) {
                    if (start == -1) start = i
                    count++
                    if (count == lengthNeeded) {
                        // Found a sufficiently long segment
                        // start is the beginning of the segment
                        return start
                    }
                } else {
                    // reset
                    start = -1
                    count = 0
                }
            }
            return null
        }

        // Move files in descending order of fileId
        for (fid in maxFileId downTo 0) {
            val (start, length) = findFile(fid) ?: continue
            // Find a suitable free segment to the left
            val freeStart = findLeftmostFreeSpan(start, length)
            if (freeStart != null) {
                // Move the file to freeStart
                // Copy fileId into disk[freeStart..freeStart+length-1]
                // Fill the original location with -1
                // Note that the file may skip over other files.
                // This is allowed according to the requirements.

                // First, save the file blocks
                val fileBlocks = disk.subList(start, start+length).toList()
                // Replace the original position with -1
                for (i in start until start+length) {
                    disk[i] = -1
                }
                // Place the file in the new location
                for (i in 0 until length) {
                    disk[freeStart + i] = fileBlocks[i]
                }
            } else {
                // There's no place to move, the file stays
            }
        }

        // Compute the checksum
        var checksum = 0L
        for ((pos, block) in disk.withIndex()) {
            if (block >= 0) {
                checksum += pos.toLong() * block
            }
        }

        println(checksum)

        return checksum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 1928L)
    check(part2(testInput) == 2858L)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
