package avill.ladv.chordo.apps.app.helpers

import avill.ladv.chordo.apps.app.helpers.ChordTransposer.chordRegex

// Mapping for American to Latin/Solfège notation
val americanToLatin = mapOf(
    "C" to "DO",
    "D" to "RE",
    "E" to "MI",
    "F" to "FA",
    "G" to "SOL",
    "A" to "LA",
    "B" to "SI"
)

// Function to convert American chord notation to Latin/Solfège notation
fun convertAmericanToLatin(text: String): String {
    val chordRegex = """(?<=\s|^|-)-?([A-G][#b]?(?:m|maj|min|dim|aug|sus|add|M|6|7|9|11|13|maj7|m7|sus[24]|add[249])*\d*)-?(?=\s|$|-)""".toRegex()

    var result = text

    // Find all chord matches and replace them
    chordRegex.findAll(text).forEach { matchResult ->
        val originalChord = matchResult.value
        var convertedChord = originalChord

        // Replace each American note with its Latin equivalent
        americanToLatin.forEach { (american, _) ->
            // Match the note at the beginning of the chord (considering sharps/flats)
            val notePattern = Regex("^($american)([#b]?)")
            convertedChord = convertedChord.replace(notePattern) { noteMatch ->
                val note = noteMatch.groupValues[1]
                val accidental = noteMatch.groupValues[2]
                "${americanToLatin[note]}$accidental"
            }
        }

        result = result.replace(originalChord, convertedChord)
    }

    return result
}

// Enhanced version that also handles chords with spaces (like "C#m7", "Bb", "F#m")
fun convertAmericanToLatinEnhanced(text: String): String {
    val chordRegex = """(?<=\s|^|-)-?([A-G][#b]?(?:m|maj|min|dim|aug|sus|add|M|6|7|9|11|13|maj7|m7|sus[24]|add[249])*\d*)-?(?=\s|$|-)""".toRegex()

    return chordRegex.replace(text) { matchResult ->
        var chord = matchResult.value

        // Remove leading/trailing hyphens if present
        val leadingHyphen = chord.startsWith("-")
        val trailingHyphen = chord.endsWith("-")
        val cleanChord = chord.trim('-')

        // Convert the clean chord
        var convertedChord = cleanChord
        americanToLatin.forEach { (american, latin) ->
            val notePattern = Regex("^($american)([#b]?)")
            convertedChord = convertedChord.replace(notePattern) { noteMatch ->
                noteMatch.groupValues[1]
                val accidental = noteMatch.groupValues[2]
                latin + accidental
            }
        }

        // Re-add hyphens if they were present
        val finalChord = when {
            leadingHyphen && trailingHyphen -> "-$convertedChord-"
            leadingHyphen -> "-$convertedChord"
            trailingHyphen -> "$convertedChord-"
            else -> convertedChord
        }

        finalChord
    }
}

// Function that also handles chord inversions and complex notations
fun convertAmericanToLatinAdvanced(text: String): String {
    // More comprehensive regex for chord detection

    val chordProcessor: (MatchResult) -> String = { matchResult ->
        var chord = matchResult.value
        val prefix = if (chord.startsWith("-")) "-" else ""
        val suffix = if (chord.endsWith("-")) "-" else ""
        chord = chord.trim('-')

        // Convert the root note
        var convertedChord = chord
        americanToLatin.forEach { (american, latin) ->
            val notePattern = Regex("^($american)([#b]?)")
            convertedChord = convertedChord.replace(notePattern) { noteMatch ->
                val accidental = noteMatch.groupValues[2]
                latin + accidental
            }
        }

        "$prefix$convertedChord$suffix"
    }

    return chordRegex.replace(text, chordProcessor)
}

// Example usage and test function
fun testChordConversion() {
    val testCases = listOf(
        "C G Am F" to "DO SOL LAm FA",
        "C#m7 Dmaj7 Em9 F#m" to "DO#m7 REmaj7 MIm9 FA#m",
        "Bb Eb/G Abmaj7" to "SIb MIb/SOL LAbmaj7",
        "C/E F#m/A G7" to "DO/MI FA#m/LA SOL7",
        "Am7 Dm7 G7 Cmaj7" to "LAm7 REm7 SOL7 DOmaj7",
        "F#dim B7 Em" to "FA#dim SI7 MIm",
        "C#m7b5 F#7sus4" to "DO#m7b5 FA#7sus4",
        "G/B C D" to "SOL/SI DO RE",
        "-C#m- -F#- -G#m-" to "-DO#m- -FA#- -SOL#m-"
    )

    println("=== Testing American to Latin Chord Conversion ===\n")

    testCases.forEach { (input, expected) ->
        val result = convertAmericanToLatinEnhanced(input)
        println("Input:    $input")
        println("Output:   $result")
        println("Expected: $expected")
        println("Match:    ${if (result == expected) "✓" else "✗"}")
        println()
    }

    // Test with a longer text
    val songText = """
        Here's a chord progression in C major: C - Dm - Em - F - G - Am - Bdim
        Another progression: C#m7 F#m7 B7 Emaj7
        With inversions: C/E D/F# G/B
        End with a -C#m- chord.
    """.trimIndent()

    println("=== Full Text Conversion ===\n")
    println("Original:")
    println(songText)
    println("\nConverted:")
    println(convertAmericanToLatinAdvanced(songText))
}

// Function to check if a chord was converted correctly
fun validateChordConversion(input: String): Boolean {
    val converted = convertAmericanToLatinEnhanced(input)
    // Check if any American notes remain unconverted
    val americanNotesRegex = Regex("\\b[A-G][#b]?(?=[mM]?\\d*\\b)")
    val hasAmericanNotes = americanNotesRegex.containsMatchIn(converted)

    if (hasAmericanNotes) {
        println("Warning: Some American notation might remain: $converted")
        return false
    }
    return true
}



// Mapping for Latin/Solfège to American notation
val latinToAmerican = mapOf(
    "DO" to "C",
    "RE" to "D",
    "MI" to "E",
    "FA" to "F",
    "SOL" to "G",
    "LA" to "A",
    "SI" to "B"
)

// Function to convert Latin/Solfège chord notation to American notation
fun convertLatinToAmerican(text: String): String {
    val chordRegex = """(?<=\s|^|-)-?([A-Z]{2,3}[#b]?(?:m|maj|min|dim|aug|sus|add|M|6|7|9|11|13|maj7|m7|sus[24]|add[249])*\d*)-?(?=\s|$|-)""".toRegex()

    var result = text

    // Find all chord matches and replace them
    chordRegex.findAll(text).forEach { matchResult ->
        var originalChord = matchResult.value
        var convertedChord = originalChord

        // Replace each Latin note with its American equivalent
        latinToAmerican.forEach { (latin, _) ->
            // Match the note at the beginning of the chord (considering sharps/flats)
            val notePattern = Regex("^($latin)([#b]?)")
            convertedChord = convertedChord.replace(notePattern) { noteMatch ->
                val note = noteMatch.groupValues[1]
                val accidental = noteMatch.groupValues[2]
                "${latinToAmerican[note]}$accidental"
            }
        }

        result = result.replace(originalChord, convertedChord)
    }

    return result
}

// Enhanced version that also handles chords with spaces and hyphens
fun convertLatinToAmericanEnhanced(text: String): String {
    val chordRegex = """(?<=\s|^|-)-?([A-Z]{2,3}[#b]?(?:m|maj|min|dim|aug|sus|add|M|6|7|9|11|13|maj7|m7|sus[24]|add[249])*\d*)-?(?=\s|$|-)""".toRegex()

    return chordRegex.replace(text) { matchResult ->
        var chord = matchResult.value

        // Remove leading/trailing hyphens if present
        val leadingHyphen = chord.startsWith("-")
        val trailingHyphen = chord.endsWith("-")
        val cleanChord = chord.trim('-')

        // Convert the clean chord
        var convertedChord = cleanChord
        latinToAmerican.forEach { (latin, american) ->
            val notePattern = Regex("^($latin)([#b]?)")
            convertedChord = convertedChord.replace(notePattern) { noteMatch ->
                noteMatch.groupValues[1]
                val accidental = noteMatch.groupValues[2]
                american + accidental
            }
        }

        // Re-add hyphens if they were present
        val finalChord = when {
            leadingHyphen && trailingHyphen -> "-$convertedChord-"
            leadingHyphen -> "-$convertedChord"
            trailingHyphen -> "$convertedChord-"
            else -> convertedChord
        }

        finalChord
    }
}

// Advanced version that handles inversions and complex notations
fun convertLatinToAmericanAdvanced(text: String): String {
    // More comprehensive regex for chord detection
    val chordRegex = """(?<=\s|^|-|\(|\[)-?([A-Z]{2,3}[#b]?(?:maj|min|m|M|dim|aug|sus|add|6|7|9|11|13|maj7|m7|maj9|m9|dim7|aug7|sus2|sus4|add9|add11|add13|\([^)]+\))*\d*)-?(?=\s|$|-|\)|\]|,)""".toRegex()

    val chordProcessor: (MatchResult) -> String = { matchResult ->
        var chord = matchResult.value
        val prefix = if (chord.startsWith("-")) "-" else ""
        val suffix = if (chord.endsWith("-")) "-" else ""
        chord = chord.trim('-')

        // Convert the root note
        var convertedChord = chord
        latinToAmerican.forEach { (latin, american) ->
            val notePattern = Regex("^($latin)([#b]?)")
            convertedChord = convertedChord.replace(notePattern) { noteMatch ->
                val accidental = noteMatch.groupValues[2]
                american + accidental
            }
        }

        "$prefix$convertedChord$suffix"
    }

    return chordRegex.replace(text, chordProcessor)
}

// Simple version for basic chord conversion (without regex complexity)
fun convertLatinToAmericanSimple(text: String): String {
    var result = text

    // Handle longer notes first (SOL, LA, SI, DO, RE, MI, FA)
    val orderedMapping = listOf(
        "SOL" to "G",
        "LA" to "A",
        "SI" to "B",
        "DO" to "C",
        "RE" to "D",
        "MI" to "E",
        "FA" to "F"
    )

    orderedMapping.forEach { (latin, american) ->
        // Match Latin note followed by optional sharp/flat and chord modifiers
        val pattern = Regex("\\b($latin)([#b]?)([mM]?\\d*(?:maj|min|dim|aug|sus|add|6|7|9|11|13)?\\b)")
        result = pattern.replace(result) { match ->
            val accidental = match.groupValues[2]
            val modifiers = match.groupValues[3]
            "$american$accidental$modifiers"
        }
    }

    return result
}

// Function specifically for chord progressions (space-separated chords)
fun convertChordProgressionLatinToAmerican(progression: String): String {
    val chords = progression.split(Regex("\\s+"))
    val convertedChords = chords.map { chord ->
        var converted = chord
        latinToAmerican.forEach { (latin, american) ->
            if (converted.startsWith(latin)) {
                val rest = converted.removePrefix(latin)
                converted = american + rest
            }
        }
        converted
    }
    return convertedChords.joinToString(" ")
}

// Example usage and test function
fun testLatinToAmericanConversion() {
    val testCases = listOf(
        "DO SOL LAm FA" to "C G Am F",
        "DO#m7 REmaj7 MIm9 FA#m" to "C#m7 Dmaj7 Em9 F#m",
        "SIb MIb/SOL LAbmaj7" to "Bb Eb/G Abmaj7",
        "DO/MI FA#m/LA SOL7" to "C/E F#m/A G7",
        "LAm7 REm7 SOL7 DOmaj7" to "Am7 Dm7 G7 Cmaj7",
        "FA#dim SI7 MIm" to "F#dim B7 Em",
        "DO#m7b5 FA#7sus4" to "C#m7b5 F#7sus4",
        "SOL/SI DO RE" to "G/B C D",
        "-DO#m- -FA#- -SOL#m-" to "-C#m- -F#- -G#m-"
    )

    println("=== Testing Latin to American Chord Conversion ===\n")

    testCases.forEach { (input, expected) ->
        val result = convertLatinToAmericanEnhanced(input)
        println("Input:    $input")
        println("Output:   $result")
        println("Expected: $expected")
        println("Match:    ${if (result == expected) "✓" else "✗"}")
        println()
    }

    // Test simple version
    println("=== Simple Version Test ===\n")
    val simpleTest = "DO SOL LAm FA DO#m SIb"
    println("Input:  $simpleTest")
    println("Output: ${convertLatinToAmericanSimple(simpleTest)}")
    println()
}

// Validate conversion back and forth
fun validateBidirectionalConversion(chord: String): Boolean {
    val toLatin = convertAmericanToLatinEnhanced(chord)
    val backToAmerican = convertLatinToAmericanEnhanced(toLatin)
    val isValid = backToAmerican == chord

    println("Original: $chord")
    println("To Latin: $toLatin")
    println("Back to American: $backToAmerican")
    println("Valid: ${if (isValid) "✓" else "✗"}")
    println()

    return isValid
}

// Integration function to process text with both tabs and Latin notation
fun processTextWithLatinNotation(text: String): Pair<List<Tab>, String> {
    // First extract tabs
    val tabs = emptyList<Tab>()

    // Then replace tabs with placeholders
    var result = replaceTabsFlexible(text)

    // Finally convert Latin notation to American
    result = convertLatinToAmericanEnhanced(result)

    return Pair(tabs, result)
}

// Example usage
fun main() {
    // Test chord conversion
    testLatinToAmericanConversion()

    // Test bidirectional conversion
    println("=== Bidirectional Conversion Test ===\n")
    validateBidirectionalConversion("C#m7")
    validateBidirectionalConversion("F#m/A")
    validateBidirectionalConversion("Bbmaj7")

    // Test with real text containing tabs and Latin notation
    val textWithTabsAndLatin = """
        Here's a progression in Latin notation:
        
        Verse: DO - SOL - LAm - FA
        
        Guitar tab:
        e|-----------------|-----------------|
        B|-----1-------1---|-----1-----------|
        G|---0-------0---0-|---0---0---------|
        D|-2-------2-------|-3-------3-------|
        A|-----------------|-----------------|
        E|-----------------|-----------------|
        
        Chorus: FA - DO - SOL - MIm
    """.trimIndent()

    println("\n=== Processing Text with Latin Notation and Tabs ===\n")
    val (tabs, processedText) = processTextWithLatinNotation(textWithTabsAndLatin)

    println("Extracted Tabs:")
    tabs.forEach { tab ->
        println("Tab ${tab.index}:")
        println(tab.content)
        println()
    }

    println("Processed Text (with replaced tabs and converted American chords):")
    println(processedText)
}