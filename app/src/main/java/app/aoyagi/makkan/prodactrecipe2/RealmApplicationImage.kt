package app.aoyagi.makkan.prodactrecipe2

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class RealmApplicationImage : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfig)

    }


}