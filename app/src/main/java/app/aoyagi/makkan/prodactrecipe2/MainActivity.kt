package app.aoyagi.makkan.prodactrecipe2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.R.attr.data
import android.graphics.BitmapFactory

import android.graphics.Bitmap

import android.R.attr.data
import android.content.Context
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URI


class MainActivity : AppCompatActivity() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val imageList = readAll()

        makeAdapter(imageList)
        fab1.setOnClickListener {
            val intent = Intent(this, activity_view::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()

        val imageList = readAll()
        makeAdapter(imageList)
    }


    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }


    fun makeAdapter(todoList: RealmResults<RealmInfo>) {
        val realmAdapter = RealmAdapter(this, todoList, object : RealmAdapter.OnItemClickListener {
            override fun onItemClick(item: RealmInfo) {
                // クリック時の処理
                delete(item.id)
            }

        }, true)

        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = realmAdapter
    }

    fun readAll(): RealmResults<RealmInfo> {
        return realm.where(RealmInfo::class.java).findAll()
    }

    fun delete(id: String) {
        realm.executeTransaction {
            val task = realm.where(RealmInfo::class.java).equalTo("id", id).findFirst()
                ?: return@executeTransaction
            task.deleteFromRealm()
        }
    }

}