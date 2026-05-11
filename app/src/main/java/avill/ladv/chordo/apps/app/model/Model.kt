package avill.ladv.chordo.model.attendance

import java.util.UUID

data class Model(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
)