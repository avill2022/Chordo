package avill.ladv.chordo.data.local.db.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import avill.ladv.chordo.data.local.LocalDataSource.Companion.DATABASE_TABLE_NAME

@Entity(tableName = "model_table")
class ModelEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String
    var data: String
    constructor(name:String,data: String) {
        this.name = name
        this.data = data
    }
}
