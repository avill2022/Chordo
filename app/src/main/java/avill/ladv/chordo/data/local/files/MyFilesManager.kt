package avill.ladv.chordo.data.local.files

import android.content.Context
import android.os.Environment
import android.util.Log
import avill.ladv.chordo.Constants
import avill.ladv.chordo.data.local.shared.MySharedPreferences
import avill.ladv.chordo.util.DatePatterns
import avill.ladv.chordo.util.Now
import avill.ladv.chordo.util.isValidDate
import java.io.*
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyFilesManager @Inject constructor(private val context: Context) {
    fun addInformationToTheFile(fileName: String, data: String) {
        var dataResult = data
        try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                if (!isEmpty(context, fileName)) {
                    dataResult = ",$dataResult"
                }
            } else {
                val sharedPreferences = MySharedPreferences(context, fileName)
                sharedPreferences.saveString("CREATE_AT" + Constants.Parameters.IMEI, Now.format(DatePatterns.YMD_HMS))
            }
            val fos = context.openFileOutput(fileName, Context.MODE_APPEND)
            val outputStreamWriter = OutputStreamWriter(fos, StandardCharsets.UTF_8)
            outputStreamWriter.write(dataResult)
            outputStreamWriter.close()
            fos.close()
            if (Constants.DEBUGGING) Log.d(MyFilesManager::class.simpleName, "new data: ${Now.format(DatePatterns.YMD_HMS)} ${getInformation(fileName).length}")
        } catch (e: Exception) {
            if (Constants.DEBUGGING) Log.wtf(MyFilesManager::class.simpleName, "WTF: ${e.message}")
        }
    }

    @Throws(FileNotFoundException::class)
    fun getInformation(fileName: String): String {
        val file: String
        val fis = context.openFileInput(fileName)
        val inputStreamReader = InputStreamReader(fis, StandardCharsets.UTF_8)
        val stringBuilder = StringBuilder()
        try {
            BufferedReader(inputStreamReader).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line).append('\n')
                    line = reader.readLine()
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Error file")
        } finally {
            file = stringBuilder.toString()
            if (Constants.DEBUGGING) Log.i(MyFilesManager::class.simpleName, "from file success: ${file.length}")
        }
        return file
    }

    fun removeFile(fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            if (file.delete()) {
                if (Constants.DEBUGGING) Log.v(MyFilesManager::class.simpleName, "File was successfully deleted: $fileName")
                true
            } else {
                if (Constants.DEBUGGING) Log.e(MyFilesManager::class.simpleName, "File deletion failed: $fileName")
                false
            }
        } else {
            if (Constants.DEBUGGING) Log.i(MyFilesManager::class.simpleName, "File does not exist: $fileName")
            true
        }
    }

    fun removeFile(dir: File, fileName: String, tag: String): Boolean {
        val file = File(dir, fileName)
        return if (file.exists()) {
            if (file.delete()) {
                if (Constants.DEBUGGING) Log.v(tag, "File was successfully deleted: $fileName")
                true
            } else {
                if (Constants.DEBUGGING) Log.e(tag, "File deletion failed: $fileName")
                false
            }
        } else {
            if (Constants.DEBUGGING) Log.i(tag, "File does not exist: $fileName")
            true
        }
    }

    fun save(fileName: String, content: String): Boolean {
        return saveToFile(this.context, fileName, content)
    }

    fun saveToFile(context: Context, nameFile: String, content: String): Boolean {
        if (content.isNotEmpty() && content != "[]") {
            try {
                val fos: FileOutputStream = context.openFileOutput(nameFile, Context.MODE_PRIVATE)
                fos.write(content.toByteArray())
                fos.close()
                if (Constants.DEBUGGING) Log.v(MyFilesManager::class.simpleName, "saveToFile(NameFile: $nameFile):success ${content.length}")
                return true
            } catch (e: IOException) {
                if (Constants.DEBUGGING) Log.e(MyFilesManager::class.simpleName, "saveToFile(NameFile: $nameFile):ERROR: ${e.message}")
                return false
            }
        } else {
            if (Constants.DEBUGGING) Log.e(MyFilesManager::class.simpleName, "There is not content to save in: $nameFile")
            return false
        }
    }

    companion object {
        fun isEmpty(context: Context, nameFile: String): Boolean {
            return try {
                val fis = context.openFileInput(nameFile)
                val availableBytes = fis.available()
                fis.close()
                availableBytes == 0
            } catch (e: IOException) {
                e.printStackTrace()
                if (Constants.DEBUGGING) Log.e(MyFilesManager::class.simpleName, "IOException: $e")
                false
            }
        }
        @JvmStatic
        fun getFileSizePrint(file: File) {
            if (file.exists()) {
                val fileSizeInBytes = file.length()
                val fileSizeInKB = fileSizeInBytes / 1024
                val fileSizeInMB = fileSizeInKB / 1024
                if (Constants.DEBUGGING) Log.v(MyFilesManager::class.simpleName, "File Size: $fileSizeInBytes bytes")
                if (Constants.DEBUGGING) Log.v(MyFilesManager::class.simpleName, "File Size: $fileSizeInKB KB")
                if (Constants.DEBUGGING) Log.v(MyFilesManager::class.simpleName, "File Size: $fileSizeInMB MB")
            } else {
                if (Constants.DEBUGGING) Log.v(MyFilesManager::class.simpleName, "File does not exist.")
            }
        }

        @JvmStatic
        fun getFiles(context: Context): Array<File>? {
            val filesDir: File = context.filesDir
            return filesDir.listFiles()
        }

        @JvmStatic
        fun getValidFiles(context: Context): File? {
            val filesDir: File = context.filesDir
            val files: Array<File>? = filesDir.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.name.isValidDate(DatePatterns.HMS) && Now.format(DatePatterns.HMS) != file.name) {
                        if (Constants.DEBUGGING) Log.v(MyFilesManager::class.simpleName, "${file.name} is a valid date in MM-dd-yyyy format.")
                        return file
                    } else {
                        if (Constants.DEBUGGING) Log.e(MyFilesManager::class.simpleName, "${file.name} is not a valid date in MM-dd-yyyy format.")
                    }
                }
            } else {
                if (Constants.DEBUGGING) Log.e(MyFilesManager::class.simpleName, "No files found in the directory.")
            }
            return null
        }

        @Throws(IOException::class)
        @JvmStatic
        fun saveFileToFolder(content: String, fileName: String) {
            val dir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val file = File(dir, "$fileName.ort")
            if (file.exists()) {
                if (getFileSize(file) == -1L) {
                    appendFile(file, content)
                } else {
                    appendFile(file, ",$content")
                }
            } else {
                appendFile(file, content)
            }
        }

        @Throws(IOException::class)
        private fun appendFile(file: File, content: String) {
            val fileWriter = FileWriter(file, true)
            fileWriter.append(content)
            if (Constants.DEBUGGING) Log.e(MyFilesManager::class.simpleName, "new data: $content ${Now.format(DatePatterns.YMD_HMS)}")
            fileWriter.close()
        }

        @JvmStatic
        fun deleteFile(fileName: String, context: Context, extension: String): Boolean {
            val uriFile = "${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + File.separator + fileName + extension)}"
            val file = File(uriFile)
            return file.delete()
        }

        @JvmStatic
        fun checkIfPdfExists(fileName: String, context: Context, extension: String): Boolean {
            val uriFile = "${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + File.separator + fileName + extension)}"
            val file = File(uriFile)
            return file.exists()
        }

        private fun getFileSize(file: File): Long {
            return if (file.exists()) {
                file.length()
            } else {
                -1L
            }
        }
    }
}
