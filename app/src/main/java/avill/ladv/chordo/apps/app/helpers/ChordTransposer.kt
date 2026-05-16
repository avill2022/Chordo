package avill.ladv.chordo.apps.app.helpers

object ChordTransposer {
    // Map of notes and their semitone positions
    private val noteOrder = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    // Map for enharmonic equivalents
    private val enharmonicMap = mapOf(
        "Db" to "C#", "Eb" to "D#", "Gb" to "F#", "Ab" to "G#", "Bb" to "A#"
    )
    // Regex for chord detection:
    // (?<=\s|^|-) : Positive lookbehind for space, start of line, or hyphen.
    // -? : Optional leading hyphen.
    // ([A-G][#b]?(?:m|maj|min|dim|aug|sus|add|M|6|7|9|11|13|maj7|m7|sus[24]|add[249])*\d*) : Root and suffix (Group 1).
    // -? : Optional trailing hyphen.
    // (?=\s|$|-) : Positive lookahead for space, end of line, or hyphen.
    val chordRegex = """(?<=\s|^|-)-?([A-G][#b]?(?:m|maj|min|dim|aug|sus|add|M|6|7|9|11|13|maj7|m7|sus[24]|add[249])*\d*)-?(?=\s|$|-)""".toRegex()
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
     * Removes all chords from the text, returning only the lyrics.
     * @param text The song with chords.
     * @return Clean text without chords.
     */
    fun removeChords(text: String): String {
        val lines = text.lines()
        return lines.map { line ->
            chordRegex.replace(line, "").trimEnd()
        }.filterIndexed { index, cleaned ->
            // Keep the line if it still has content, OR if it was originally an empty line.
            // This removes lines that were purely chords while preserving intended spacing.
            cleaned.isNotEmpty() || lines[index].isEmpty()
        }.joinToString("\n")
    }

    /**
     * Extracts a list of unique chords from the provided text.
     * @param text The song text containing chords.
     * @return List of unique chords found in the text.
     */
    fun getUniqueChords(text: String): List<String> {
        return chordRegex.findAll(text)
            .map { it.groupValues[1] } // Extracts the core chord part
            .distinct()
            .toList()
    }

    /**
     * Transpose a single line of text using a regular expression to detect chords.
     */
    private fun transposeLine(line: String, semitones: Int): String {
        return chordRegex.replace(line) { matchResult ->
            transposeChord(matchResult.value, semitones)
        }
    }

    /**
     * Transpose a single chord
     */
    private fun transposeChord(chord: String, semitones: Int): String {
        if (chord.isEmpty()) return chord

        // Preserve optional surrounding hyphens
        val hasLeadingHyphen = chord.startsWith("-")
        val hasTrailingHyphen = chord.endsWith("-") && chord.length > (if (hasLeadingHyphen) 1 else 0)
        
        val pureChord = chord.removeSurrounding("-")
        if (pureChord.isEmpty()) return chord

        // Separate root note from the rest of the chord
        val (rootNote, chordSuffix) = if (pureChord.length >= 2 && (pureChord[1] == '#' || pureChord[1] == 'b')) {
            pureChord.substring(0, 2) to pureChord.substring(2)
        } else {
            pureChord.substring(0, 1) to pureChord.substring(1)
        }

        // Normalize enharmonic equivalents (e.g., Db -> C#)
        val normalizedRoot = enharmonicMap[rootNote] ?: rootNote

        // Find index and transpose
        val currentIndex = noteOrder.indexOf(normalizedRoot)
        if (currentIndex == -1) return chord

        var newIndex = (currentIndex + semitones) % noteOrder.size
        if (newIndex < 0) newIndex += noteOrder.size

        val newRootNote = noteOrder[newIndex]

        return (if (hasLeadingHyphen) "-" else "") + 
               newRootNote + chordSuffix + 
               (if (hasTrailingHyphen) "-" else "")
    }
}
