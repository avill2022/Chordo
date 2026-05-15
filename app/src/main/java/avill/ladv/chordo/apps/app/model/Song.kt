package avill.ladv.chordo.apps.app.model

data class Song(
    val name: String,
    val tone: String,
    val chords: String,
    val rhythm: String,
    val tempo: String,
    val content: String,
    val tab: String,

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