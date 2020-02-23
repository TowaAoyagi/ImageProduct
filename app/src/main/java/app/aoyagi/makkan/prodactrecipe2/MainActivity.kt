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


class MainActivity : AppCompatActivity() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }
    private val REQUEST_IMAGE_CAPTURE = 1
    private val RESULT_CAMERA = 1001
    private var cameraUri: Uri? = null
    private var cameraFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val imageList = readAll()

        makeAdapter(imageList)
        fab1.setOnClickListener {
            cameraIntent()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (cameraUri != null) {
                create(cameraUri!!)
                registerDatabase(cameraFile!!)
                Log.d("camerafile",cameraFile.toString())
            } else {

            }
            val imageList = readAll()
            makeAdapter(imageList)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    fun create(uri: Uri) {
        realm.executeTransaction {
            val realmInfo = realm.createObject(RealmInfo::class.java, UUID.randomUUID().toString())
            realmInfo.uri = uri.toString()

        }
    }

    @SuppressLint("LongLogTag")
    private fun cameraIntent() {
        val cFolder = getExternalFilesDir(Environment.DIRECTORY_DCIM)
        val fileDate: String = SimpleDateFormat(
            "ddHHmmss", Locale.US
        ).format(Date())
        val fileName = String.format("CameraIntent_.jpg", fileDate)
        cameraFile = File(cFolder, fileName)
        cameraUri = FileProvider.getUriForFile(
            this@MainActivity,
            applicationContext.packageName + ".fileprovider",
            cameraFile!!
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        startActivityForResult(intent, RESULT_CAMERA)
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

    private fun registerDatabase(file: File) {
        val contentValues = ContentValues()
        val contentResolver = this@MainActivity.contentResolver
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        contentValues.put("_data", file.absolutePath)
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }


}