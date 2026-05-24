package avill.ladv.chordo.apps.app.helpers

val CHROMATIC_SCALE = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

fun noteIndex(note: String): Int = CHROMATIC_SCALE.indexOf(note)

val majorMatriz = listOf(
    listOf(0, -1, 3, 2, 0, 1, 0),
    listOf(3, -1, 3, 5, 5, 5, 3),
    listOf(5, 8, 7, 5, 5, 5, 8),
    listOf(8, 8, 10, 10, 9, 8, 8),
    listOf(10, -1, 10, 10, 12, 13, 12)
)

val majorSeven = listOf(
    listOf(0, -1, 3, 2, 3, 1, 0),
    listOf(3, -1, 3, 5, 3, 5, 3),
    listOf(5, 8, 7, 5, 5, 5, 6),
    listOf(8, 8, 10, 18, 9, 8, 8),
    listOf(10, -1, 10, 10, 12, 11, 12)
)

val minorMatriz = listOf(
    listOf(3, -1, 3, 5, 5, 4, 3),
    listOf(8, 8, 10, 10, 8, 8, 8),
    listOf(10, -1, 10, 10, 12, 13, 11)
)

val c5Matrix = listOf(
    listOf(0, -1, 3, 5, -1, -1, -1),
    listOf(0, 3, -1, -1, 5, 6, -1),
    listOf(3, -1, 3, 5, 5, -1, 8)
)

val cdimMatrix = listOf(
    listOf(0, -1, 3, -1, 4, -1, 2),
    listOf(3, 3, 4, 5, 4, 5, -1),
    listOf(8, -1, 8, 9, 8, 9, -1)
)

val caugMatrix = listOf(
    listOf(0, -1, 3, 2, 1, 1, -1),
    listOf(0, 3, 2, 1, 1, 2, 3),
    listOf(6, -1, 8, 7, 5, 6, -1)
)

val csus2Matrix = listOf(
    listOf(0, -1, 3, 0, 0, 1, 3),
    listOf(0, 3, 0, 0, 2, 3, 3),
    listOf(10, -1, 10, 12, 12, 13, 12)
)

val csus4Matrix = listOf(
    listOf(0, -1, 3, 3, 0, 1, 1),
    listOf(0, 3, 3, 0, 1, 1, 4),
    listOf(8, -1, 8, 10, 8, 8, 8)
)

val cmaj7Matrix = listOf(
    listOf(0, -1, 3, 2, 0, 0, 0),
    listOf(0, 3, 2, 0, 0, 0, 3),
    listOf(7, -1, 8, 9, 9, 8, 7)
)

val cm7Matrix = listOf(
    listOf(0, -1, 3, 1, 3, 1, 1),
    listOf(0, 3, 1, 3, 1, 1, 3),
    listOf(8, -1, 8, 8, 8, 8, 8)
)

val c6Matrix = listOf(
    listOf(0, -1, 3, 2, 2, 1, 0),
    listOf(0, 3, 2, 2, 2, 1, 3),
    listOf(9, -1, 10, 9, 10, 10, 12)
)

val cm6Matrix = listOf(
    listOf(0, -1, 3, 1, 2, 1, 0),
    listOf(0, 3, 1, 2, 1, 1, 4),
    listOf(8, -1, 8, 9, 9, 9, 8)
)

val c9Matrix = listOf(
    listOf(0, -1, 3, 2, 3, 3, 3),
    listOf(0, 3, 2, 3, 3, 3, 3),
    listOf(8, -1, 8, 9, 8, 8, 10)
)

val cm9Matrix = listOf(
    listOf(0, -1, 3, 1, 3, 3, 3),
    listOf(0, 3, 1, 3, 3, 3, 3),
    listOf(8, -1, 8, 8, 8, 8, 10)
)

val VARIATIONS = mapOf(
    "major" to majorMatriz,
    "minor" to minorMatriz,
    "major7" to majorSeven,
    "c5" to c5Matrix,
    "cdim" to cdimMatrix,
    "caug" to caugMatrix,
    "csus2" to csus2Matrix,
    "csus4" to csus4Matrix,
    "cmaj7" to cmaj7Matrix,
    "cm7" to cm7Matrix,
    "c6" to c6Matrix,
    "cm6" to cm6Matrix,
    "c9" to c9Matrix,
    "cm9" to cm9Matrix
)

class FiguresRepo(var matriz: List<MutableList<Int>>) {
    var index = 0

    fun add(n: Int): List<MutableList<Int>> {
        index = n
        matriz = matriz.map { row -> row.map { if (it == -1) -1 else it + n }.toMutableList() }
        return matriz
    }

    fun twelveLess() {
        for (row in matriz) {
            while (true) {
                val vals = row.filter { it != -1 }
                if (vals.isEmpty()) break
                if (vals.min() < 12) break
                for (i in row.indices) {
                    if (row[i] != -1) row[i] -= 12
                }
            }
        }
    }

    fun organize() {
        var minVal = Int.MAX_VALUE
        var minIdx = 0
        for ((i, row) in matriz.withIndex()) {
            val vals = row.filter { it != -1 }
            if (vals.isNotEmpty()) {
                val rmin = vals.min()
                if (rmin < minVal) {
                    minVal = rmin
                    minIdx = i
                }
            }
        }
        if (minVal == 0) return
        matriz = (matriz.drop(minIdx) + matriz.take(minIdx - 1)).toMutableList()
    }

    fun fiveLess() {
        for (row in matriz) {
            val vals = row.filter { it != -1 }
            if (vals.isNotEmpty() && vals.max() >= 5) {
                val first = row[0]
                for (i in 1 until row.size) {
                    if (row[i] != -1) row[i] -= (first - 1)
                }
            }
        }
    }

    fun cNormalization() {
        for (row in matriz) {
            val vals = row.filter { it != -1 }
            if (vals.isNotEmpty() && vals.max() <= 4) {
                row[0] = 0
            }
        }
    }

    fun showComplete() {
        val strings = listOf("e", "B", "G", "D", "A", "E")
        for (row in matriz) {
            val capo = row[0]
            val vals = row.drop(1).reversed()

            val relPositions = mutableListOf<Int?>()
            var maxRel = 0
            for (v in vals) {
                if (v != -1) {
                    val rel = v - capo
                    relPositions.add(rel)
                    if (rel > maxRel) maxRel = rel
                } else {
                    relPositions.add(null)
                }
            }

            val numFrets = maxRel + 1

            println("     $capo")
            for ((label, rel, v) in strings.zip(relPositions).zip(vals) { (l, r), v -> Triple(l, r, v) }) {
                val lbl = if (v == -1) "x" else label
                val line = if (rel == null || rel < 0) {
                    "$lbl -||${"---|".repeat(numFrets)}"
                } else {
                    val parts = (0 until numFrets).map { if (it == rel) "-O-" else "---" }
                    "$lbl -||${parts.joinToString("|")}|"
                }
                println(line)
            }
        }
    }

    fun showChords() {
        val strings = listOf("e", "B", "G", "D", "A", "E")
        for (row in matriz) {
            val capo = if (row[0] == 0) "no" else row[0].toString()
            println("capo:$capo")
            for ((label, ndata) in strings.zip(row.drop(1).reversed())) {
                val v = if (ndata == -1) "x" else ndata.toString()
                println("$label |---$v---|")
            }
        }
    }

    fun showCompleteChords() {
        val strings = listOf("e", "B", "G", "D", "A", "E")
        for (row in matriz) {
            val capo = if (row[0] == 0) "" else row[0].toString()
            println("     $capo")
            for ((label, ndata) in strings.zip(row.drop(1).reversed())) {
                val v = if (ndata == -1) "x" else ndata.toString()
                if (v == "x") {
                    println("x-||---|---|---|---|")
                } else {
                    val frets = (1..4).joinToString("|") { if (it == v.toInt()) "-O-" else "---" }
                    println("$label-||$frets|")
                }
            }
        }
    }
    fun getCompleteChords(index:Int = 0):String {
        val strings = listOf("e", "B", "G", "D", "A", "E")
        var str = ""
        val row = matriz[index]
            val capo = if (row[0] == 0) "" else row[0].toString()
            println("     $capo")
            for ((label, ndata) in strings.zip(row.drop(1).reversed())) {
                val v = if (ndata == -1) "x" else ndata.toString()
                if (v == "x") {
                    println("x-||---|---|---|---|")
                    str += "x-||---|---|---|---|"+"\n"
                } else {
                    val frets = (1..4).joinToString("|") { if (it == v.toInt()) "-O-" else "---" }
                    println("$label-||$frets|")
                    str += "$label-||$frets|"+"\n"
                }
            }

        return str
    }

    fun show() {
        val note = CHROMATIC_SCALE[index - 1]
        for (row in matriz) {
            val vals = row.drop(1).joinToString("") { if (it == -1) "x" else it.toString() }
            val capo = if (row[0] == 0) "NO" else row[0].toString()
            println("$note:$vals CAPO:$capo")
        }
    }
}

fun main() {
    val data = FiguresRepo(cm6Matrix.map { it.toMutableList() })
    data.twelveLess()
    data.organize()
    data.fiveLess()
    data.showCompleteChords()
    data.show()
}
