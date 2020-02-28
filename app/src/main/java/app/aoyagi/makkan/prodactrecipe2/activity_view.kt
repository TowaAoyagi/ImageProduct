package app.aoyagi.makkan.prodactrecipe2

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_view.*
import java.io.BufferedInputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class activity_view : AppCompatActivity() {

    lateinit var realm: Realm

    lateinit var uri: Uri

    lateinit var bitmap: Bitmap

    companion object {
        const val REQUEST_CODE_PERMISSION: Int = 200
        const val REQUEST_CODE_CAMERA: Int = 150
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)


        Realm.init(this)
        realm = Realm.getDefaultInstance()

        fab2.setOnClickListener {
            intentCamera()

        }


    }

    fun intentCamera() {
        checkPermission(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
        if (checkPermission(Manifest.permission.CAMERA) && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startCamera()
        }
    }


    fun save(date: String) {
        realm.executeTransaction {
            val realmInfo = realm.createObject(RealmInfo::class.java, UUID.randomUUID().toString())
            realmInfo.date = date
            if (realmInfo != null) {
                realmInfo.uri = uri.toString()
            }
        }
    }


    fun update(view: View) {

        val date = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPANESE)
        val updateDate = sdf.format(date)
        val intent = Intent(this, MainActivity::class.java)
        if (bitmap != null) {
            save(updateDate)
            startActivity(intent)
            finish()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "設定から許可しましょう", Toast.LENGTH_SHORT).show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun checkPermission(tergerPermissionArray: Array<String>) {

        var chechNeededPermissionList = mutableListOf<String>()

        for (permission in tergerPermissionArray) {
            if (!checkPermission(permission)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                    Toast.makeText(this, "設定から許可しましょう", Toast.LENGTH_SHORT)
                        .show()
                    return
                } else {
                    chechNeededPermissionList.add(permission)
                }
            }
        }

        if (!chechNeededPermissionList.isEmpty()) {
            ActivityCompat.requestPermissions(
                this, chechNeededPermissionList.toTypedArray(), REQUEST_CODE_PERMISSION
            )
        }
    }

    fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun startCamera() {
        val fileName = "${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, fileName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!


        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            imageView2.setImageBitmap(bitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }
}
