package app.aoyagi.makkan.prodactrecipe2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import java.io.BufferedInputStream
import java.io.InputStream
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
        holder.titleTextView.text = task.id
        val uri = Uri.parse(task.uri)
        val bitmap = makeBitmap(uri, context)
        holder.imageView.setImageBitmap(bitmap)
    }

    //    Inflaterをここで設定する
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.activity_image, viewGroup, false)
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.imageView
        var linearLayout: LinearLayout = view.linear
        var titleTextView : TextView = view.textView

    }

    fun makeBitmap(uri: Uri, context: Context): Bitmap {
        val stream: InputStream? = context.getContentResolver().openInputStream(uri)
        val bitmap: Bitmap = BitmapFactory.decodeStream(BufferedInputStream(stream))
        return bitmap
    }

    interface OnItemClickListener {
        fun onItemClick(item: RealmInfo)

    }
}
