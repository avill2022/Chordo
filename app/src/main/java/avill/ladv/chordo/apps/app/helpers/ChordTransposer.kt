package avill.ladv.chordo.apps.app.helpers

object ChordTransposer {

    // Map of notes and their semitone positions
    private val noteOrder = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    // Map for enharmonic equivalents
    private val enharmonicMap = mapOf(
        "Db" to "C#", "Eb" to "D#", "Gb" to "F#", "Ab" to "G#", "Bb" to "A#"
    )

    /**
     * Main transpose function
     * @param songText The song with chords
     * @param semitones Number of semitones to transpose (+ for up, - for down)
     * @return Transposed song text
     */
    fun transpose(songText: String, semitones: Int): String {
        val lines = songText.lines()
        return lines.joinToString("\n") { line ->
            transposeLine(line, semitones)
        }
    }

    /**
     * Transpose a single line of text
     */
    private fun transposeLine(line: String, semitones: Int): String {
        val result = StringBuilder()
        var i = 0

        while (i < line.length) {
            when {
                // Check if current character is part of a chord (letter A-G)
                line[i].isLetter() && line[i].uppercase() in "ABCDEFG" -> {
                    val chord = extractChord(line, i)
                    val transposedChord = transposeChord(chord, semitones)
                    result.append(transposedChord)
                    i += chord.length
                }
                else -> {
                    result.append(line[i])
                    i++
                }
            }
        }

        return result.toString()
    }

    /**
     * Extract full chord starting at position i
     */
    private fun extractChord(line: String, startIdx: Int): String {
        var endIdx = startIdx
        val firstChar = line[startIdx].uppercase()

        // Check for sharp or flat after the root note
        if (endIdx + 1 < line.length && (line[endIdx + 1] == '#' || line[endIdx + 1] == 'b')) {
            endIdx++
        }

        // Continue to get chord suffix (m, maj, 7, sus, etc.)
        while (endIdx + 1 < line.length) {
            val nextChar = line[endIdx + 1]
            if (nextChar.isLetterOrDigit() || nextChar in setOf('m', 'M', '7', '9', '1', '2', '3', '4', '5', '6', 's', 'u', 'a', 'j', 'd', 'i', 'm', 'M')) {
                endIdx++
            } else {
                break
            }
        }

        return line.substring(startIdx, endIdx + 1)
    }

    /**
     * Transpose a single chord
     */
    private fun transposeChord(chord: String, semitones: Int): String {
        if (chord.isEmpty()) return chord

        // Separate root note from the rest of the chord
        var rootNote: String
        var chordSuffix: String

        when {
            chord.length >= 2 && chord[1] == '#' -> {
                rootNote = chord.substring(0, 2)
                chordSuffix = chord.substring(2)
            }
            chord.length >= 2 && chord[1] == 'b' -> {
                rootNote = chord.substring(0, 2)
                chordSuffix = chord.substring(2)
            }
            else -> {
                rootNote = chord.substring(0, 1)
                chordSuffix = chord.substring(1)
            }
        }

        // Normalize enharmonic equivalents
        rootNote = enharmonicMap[rootNote] ?: rootNote

        // Find index and transpose
        val currentIndex = noteOrder.indexOf(rootNote)
        if (currentIndex == -1) return chord

        var newIndex = (currentIndex + semitones) % noteOrder.size
        if (newIndex < 0) newIndex += noteOrder.size

        val newRootNote = noteOrder[newIndex]

        return newRootNote + chordSuffix
    }

    // Convenience methods
    fun transposeUpOneSemitone(songText: String) = transpose(songText, 1)
    fun transposeDownOneSemitone(songText: String) = transpose(songText, -1)
    fun transposeUpOneTone(songText: String) = transpose(songText, 2)
    fun transposeDownOneTone(songText: String) = transpose(songText, -2)
}

// Alternative class with named parameters
class SongTransposer {

    enum class TransposeAmount {
        HALF_UP,    // +1/2 tone
        HALF_DOWN,  // -1/2 tone
        FULL_UP,    // +1 tone
        FULL_DOWN   // -1 tone
    }

    fun transpose(song: String, amount: TransposeAmount): String {
        val semitones = when (amount) {
            TransposeAmount.HALF_UP -> 1
            TransposeAmount.HALF_DOWN -> -1
            TransposeAmount.FULL_UP -> 2
            TransposeAmount.FULL_DOWN -> -2
        }
        return ChordTransposer.transpose(song, semitones)
    }
}

// Usage example
fun main() {
    val originalSong = """
        Verse 1:
        C          G          Am         F
        Hello, is it me you're looking for?
        
        Chorus:
        C          G          Am         F
        I can see it in your eyes, I can see it in your smile
        
        Bridge:
        Dm7        G7         Cmaj7      A7
        You're all I've ever wanted
    """.trimIndent()

    println("Original Song:")
    println(originalSong)
    println("\n" + "=".repeat(50))

    // Transpose up 1 semitone (+1/2 tone)
    val halfUp = ChordTransposer.transposeUpOneSemitone(originalSong)
    println("\nTransposed +1/2 tone:")
    println(halfUp)

    // Transpose down 1 semitone (-1/2 tone)
    val halfDown = ChordTransposer.transposeDownOneSemitone(originalSong)
    println("\nTransposed -1/2 tone:")
    println(halfDown)

    // Transpose up 1 tone (+1 tone)
    val fullUp = ChordTransposer.transposeUpOneTone(originalSong)
    println("\nTransposed +1 tone:")
    println(fullUp)

    // Using the alternative class
    val transposer = SongTransposer()
    val halfUpAlt = transposer.transpose(originalSong, SongTransposer.TransposeAmount.HALF_UP)
    println("\nUsing alternative class (+1/2 tone):")
    println(halfUpAlt)

    // Test various chord types
    val chordTest = "Am   A7   Amaj7   Asus4   A#m   C#dim   Bbm"
    println("\n\nChord Test:")
    println("Original: $chordTest")
    println("+1 tone: ${ChordTransposer.transposeUpOneTone(chordTest)}")
    println("-1/2 tone: ${ChordTransposer.transposeDownOneSemitone(chordTest)}")
}