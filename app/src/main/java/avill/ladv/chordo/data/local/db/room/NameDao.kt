package avill.ladv.chordo.data.local.db.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import avill.ladv.chordo.data.local.db.room.entities.ModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertModel(modelEntity: ModelEntity)
    @Query("SELECT * FROM model_table WHERE id = :id")
    fun getModelBy(id: Int): Flow<ModelEntity> // Note: This function should ideally return a Flow<ModelEntity> for better reactive programming practices.
    @Query("DELETE FROM model_table WHERE id = :id")
    fun deleteModelById(id: Int)

    @Delete
    suspend fun deleteModel(modelEntity: ModelEntity)
    @Update
    suspend fun updateModel(modelEntity: ModelEntity)

    @Query("SELECT * FROM model_table ORDER BY id ASC")
    fun getAllModels(): Flow<MutableList<ModelEntity>>
    @Query("DELETE FROM model_table")
    fun deleteAllModels()
}
