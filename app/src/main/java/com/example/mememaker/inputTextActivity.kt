package com.example.mememaker

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


private fun BitmapConvertFile(bitmap: Bitmap, strFilePath: String) {
    // 파일 선언 -> 경로는 파라미터에서 받는다
    val file = File(strFilePath)

    // OutputStream 선언 -> bitmap데이터를 OutputStream에 받아 File에 넣어주는 용도
    var out: OutputStream? = null
    try {
        // 파일 초기화
        file.createNewFile()

        // OutputStream에 출력될 Stream에 파일을 넣어준다
        out = FileOutputStream(file)

        // bitmap 압축
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            out!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

public fun bitmapToFile(bitmap: Bitmap, path: String): File{
    var file = File(path)
    var out: OutputStream? = null
    try{
        file.createNewFile()
        out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)

    }finally{
        out?.close()
    }
    return file
}

fun viewToBitmap(view: View): Bitmap {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)

    return bitmap
}

class inputTextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_text)

        val layout = findViewById<LinearLayout>(R.id.meme_img)

        /*
        val selectImg = findViewById<ImageView>(R.id.meme_img)

        Glide.with(this)
            .load(imgURL) // 불러올 이미지 url
            .into(selectImg) // 이미지를 넣을 뷰
        */
        val imgURL = intent.getStringExtra("img")
        Glide.with(this)
            .load(imgURL)
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    layout.background = resource
                }
            })

        findViewById<EditText>(R.id.text_edit1).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                findViewById<TextView>(R.id.meme_text1).text = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        val save_btn = findViewById<Button>(R.id.save_btn)
        save_btn.setOnClickListener {
            val bitmap_img: Bitmap = viewToBitmap(layout)
            saveImage(bitmap_img, this, "meme")
//            // BitmapConvertFile(bitmap_img, "")
//            val path = Environment.getExternalStorageDirectory().toString()
//            var fOut: OutputStream? = null
//            val counter = 0
//            val file = File(path, "meme.jpg") // the File to save , append increasing numeric counter to prevent files from getting overwritten.
//
//            fOut = FileOutputStream(file)
//
//            bitmap_img.compress(
//                Bitmap.CompressFormat.JPEG,
//                85,
//                fOut
//            ) // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
//
//            fOut.flush() // Not really required
//            fOut.close() // do not forget to close the stream

        }
    }

    private fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? =
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory =
                File(Environment.getExternalStorageDirectory().toString() + "/" + folderName)
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        }
    }

    private fun contentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {

            }
        }
    }
}