package avill.ladv.chordo.util.maps

import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator



import java.io.Serializable

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ClusterMarker(
    private var position: LatLng, // Required field
    private var title: String?, // Required field
    private var snippet: String?, // Required field
    private var iconPicture: Int,
    private var report: Report?
) : ClusterItem {

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String? {
        return title
    }

    override fun getSnippet(): String? {
        return snippet
    }

    fun getIconPicture(): Int {
        return iconPicture
    }

    fun setIconPicture(iconPicture: Int) {
        this.iconPicture = iconPicture
    }

    fun getReport(): Report? {
        return report
    }

    fun setReport(report: Report?) {
        this.report = report
    }

    fun setPosition(position: LatLng) {
        this.position = position
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun setSnippet(snippet: String?) {
        this.snippet = snippet
    }
}

class Report(
    var key: String? = null,
    var noReport: String? = null,
    var date: String? = null,
    var location: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var description: String? = null,
    var references: String? = null,
    var answer: String? = null,
    var dateAnswer: String? = null,
    var estate: String? = null,
    var images: Map<String, String>? = HashMap(),
    var department: String? = null,
    var idDepartment: String? = null,
    var idUser: String? = null
) : Serializable


class MyClusterManagerRenderer(
    context: Context,
    googleMap: GoogleMap,
    clusterManager: ClusterManager<ClusterMarker>
) : com.google.maps.android.clustering.view.DefaultClusterRenderer<ClusterMarker>(context, googleMap, clusterManager) {

    private val iconGenerator: IconGenerator = IconGenerator(context.applicationContext)
    private val imageView: ImageView = ImageView(context.applicationContext)
    private val markerWidth: Int = 80
    private val markerHeight: Int = 80

    init {
        imageView.layoutParams = ViewGroup.LayoutParams(markerWidth, markerHeight)
        val padding = 5
        imageView.setPadding(padding, padding, padding, padding)
        iconGenerator.setContentView(imageView)
    }

    override fun onBeforeClusterItemRendered(item: ClusterMarker, markerOptions: MarkerOptions) {
        imageView.setImageResource(item.getIconPicture())
        val icon: Bitmap = iconGenerator.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.title)
        val r: Report? = item.getReport()
        if (r != null) {
            var img = ""
            // for (key in r.images.keys) {
            //     img += r.images[key] + "┼"
            // }
            markerOptions.snippet(
                "${r.noReport}┴${r.date}┴${r.location}┴${r.description}┴${r.estate}┴${r.idUser}┴${r.idDepartment}┴${r.key}┴$img┴${r.answer}┴${r.dateAnswer}"
            )
            markerOptions.title(item.title)
        } else {
            markerOptions.snippet(item.snippet)
            markerOptions.title(item.title)
        }
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ClusterMarker>): Boolean {
        return false
    }

    fun setUpdateMarker(clusterMarker: ClusterMarker) {
        val marker: Marker? = getMarker(clusterMarker)
        if (marker != null) {
            marker.position = clusterMarker.position
        }
    }
}
