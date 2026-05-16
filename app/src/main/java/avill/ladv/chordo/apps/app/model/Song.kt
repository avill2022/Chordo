package avill.ladv.chordo.apps.app.model

data class Song(
    val name: String,
    var tone: String,
    var chords: String,
    val rhythm: String,
    val tempo: String,
    var content: String,
    var tab: String,

    val structure: String,

    val harmony: String,
    val melody: String,


    val author: String,
    val folder: String,
    val urlsong: String,
    val urltutorial: String,
    val urlmidi: String,
    val urlgpt: String,
    val urlpartiture: String,
)