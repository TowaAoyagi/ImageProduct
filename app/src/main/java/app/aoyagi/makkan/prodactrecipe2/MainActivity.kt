package app.aoyagi.makkan.prodactrecipe2

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imageList = readAll()

        makeAdapter(imageList)
        fab1.setOnClickListener {
            dispatchTakePictureIntent()
            realm.close()
        }



    }



    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            create(imageBitmap)

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    fun create(bitmap: Bitmap) {
        realm.executeTransaction {
            val realmInfo = realm.createObject(RealmInfo::class.java, UUID.randomUUID().toString())
            realmInfo.imageBitmap = bitmap


        }
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
//        チェックボックスにチェックがついてる順にソート
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
