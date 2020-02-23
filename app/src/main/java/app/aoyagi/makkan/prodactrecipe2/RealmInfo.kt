package app.aoyagi.makkan.prodactrecipe2

import android.graphics.Bitmap
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class RealmInfo : RealmObject() {
    @PrimaryKey
    open var id: String = UUID.randomUUID().toString()

    open var imageBitmap: Bitmap? = null
    open var createdAt: Date = Date(System.currentTimeMillis())
}