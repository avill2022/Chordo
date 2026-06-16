package avill.ladv.chordo.apps.app.helpers

private val ENHARMONIC = mapOf(
    "Db" to "C#", "Eb" to "D#", "Gb" to "F#", "Ab" to "G#", "Bb" to "A#"
)

private val SUFFIX_MAP = mapOf(
    "" to "major",
    "m" to "minor",
    "minor" to "minor",
    "7" to "major7",
    "dom7" to "major7",
    "maj7" to "cmaj7",
    "M7" to "cmaj7",
    "m7" to "cm7",
    "dim" to "cdim",
    "aug" to "caug",
    "+" to "caug",
    "sus2" to "csus2",
    "sus4" to "csus4",
    "6" to "c6",
    "m6" to "cm6",
    "9" to "c9",
    "m9" to "cm9",
    "5" to "c5",
    "power" to "c5"
)

private val ZERO_MATRIX = listOf(listOf(0, 0, 0, 0, 0, 0, 0))

class ChordFinder {
    private val scale = CHROMATIC_SCALE
    private val variations = VARIATIONS
    private val enharmonic = ENHARMONIC
    private val suffixMap = SUFFIX_MAP

    fun parse(chordStr: String): Pair<String, String>? {
        val s = chordStr.trim()
        if (s.isEmpty()) return null

        val root = buildString {
            append(s[0].uppercaseChar())
            if (s.length > 1 && s[1] in setOf('#', 'b')) {
                append(s[1])
            }
        }

        val suffix = if (root.length > 1 && root[1] in setOf('#', 'b'))
            s.drop(2) else s.drop(1)

        val normalizedRoot = enharmonic.getOrDefault(root, root)

        if (normalizedRoot !in scale) return null

        val variation = suffixMap[suffix] ?: return null

        return normalizedRoot to variation
    }

    fun find(chordStr: String): FiguresRepo {
        val (root, variation) = parse(chordStr) ?: return FiguresRepo(ZERO_MATRIX.map { it.toMutableList() })

        val template = variations[variation]!!.map { it.toMutableList() }
        val f = FiguresRepo(template)
        val n = noteIndex(root)
        f.add(n)
        f.twelveLess()
        f.organize()
        f.fiveLess()
        return f
    }

    fun show(chordStr: String) {
        val f = find(chordStr)
        f.showCompleteChords()
        f.show()
    }

    fun getMatrix(chordStr: String): List<List<Int>> {
        val f = find(chordStr)
        return f.matriz
    }
}

fun main() {
    val finder = ChordFinder()
    finder.show("A#")

}
