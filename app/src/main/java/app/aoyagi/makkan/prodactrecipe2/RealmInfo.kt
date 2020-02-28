package app.aoyagi.makkan.prodactrecipe2

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class RealmInfo : RealmObject() {
    @PrimaryKey
    open var id: String = UUID.randomUUID().toString()
    open var uri: String = ""
    open var date: String = ""
}