package avill.ladv.chordo.data.network.firebase.entities

import com.google.gson.annotations.SerializedName

data class MultipleResource(
    @SerializedName("page")
    val page: Int?,
    @SerializedName("per_page")
    val perPage: Int?,
    @SerializedName("total")
    val total: Int?,
    @SerializedName("total_pages")
    val totalPages: Int?,
    @SerializedName("data")
    val data: List<Datum>?
) {
    data class Datum(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("year")
        val year: Int?,
        @SerializedName("pantone_value")
        val pantoneValue: String?
    )
}
