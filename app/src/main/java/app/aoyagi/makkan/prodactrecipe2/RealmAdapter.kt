package app.aoyagi.makkan.prodactrecipe2

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_image.view.*
import java.text.SimpleDateFormat
import java.util.*

class RealmAdapter(
    private val context: Context,
    private var list: OrderedRealmCollection<RealmInfo>?,
    private var listener: OnItemClickListener,
    private val autoUpdate: Boolean
) : RealmRecyclerViewAdapter<RealmInfo, RealmAdapter.ViewHolder>(list, autoUpdate) {

    override fun getItemCount(): Int = list?.size ?: 0

    //    ここで中身をセットする
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task: RealmInfo = list?.get(position) ?: return

        holder.linearLayout.setOnClickListener {
            listener.onItemClick(task)
        }
//        holder.titleTextView.text = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPANESE).format(task.createdAt)
        holder.titleTextView.text = "tilte"
        val uri = task.uri.toUri()
        Log.d("String to URI", uri.toString())
        holder.imageView.setImageURI(uri)
    }

    //    Inflaterをここで設定する
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.activity_image, viewGroup, false)
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.textView
        var imageView: ImageView = view.imageView
        var linearLayout: LinearLayout = view.linear

    }

    interface OnItemClickListener {
        fun onItemClick(item: RealmInfo)

    }
}
