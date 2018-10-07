package amarenkov.movieraid.managers

import amarenkov.movieraid.utils.now
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareManager {

    fun sendImage(activity: Activity,
                          image: Bitmap?,
                          shareTitle: String,
                          shareText: String,
                          onPrepared: (() -> Unit)) {
        saveTempImage(activity, image)?.let {
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, shareTitle)
//                putExtra(Intent.EXTRA_SUBJECT, shareTitle)
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_STREAM, it)
                type = "image/*"

                resolveActivity(activity.packageManager)?.let {
                    activity.startActivityForResult(Intent.createChooser(this, shareTitle), 0)
                }

                onPrepared.invoke()
            }
        }
    }

    private fun saveTempImage(context: Context, mBitmap: Bitmap?): Uri? {
        if (mBitmap == null)
            return null

        val outputFile = File.createTempFile("tmp_$now", ".png", context.cacheDir)
        try {
            val outStream = FileOutputStream(outputFile)
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", outputFile)
    }
}