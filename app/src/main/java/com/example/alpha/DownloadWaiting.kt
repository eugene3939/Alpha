package com.example.alpha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.alpha.ui.Payment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Serializable
import java.sql.Connection

class DownloadWaiting : AppCompatActivity() {

    private lateinit var ftpClient: FTPClient

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_waiting)

        //FTP連線，下載所需資源
        GlobalScope.launch(Dispatchers.IO) {
            // 在 IO 調度器中呼叫 connectFTP()
            connectFTP()

            //讀取csv內容
            readCsv("/storage/emulated/0/Android/data/com.example.alpha/files/definitionFile/DEFFLD.csv")

            //下載完成跳轉登入頁面
            val intent = Intent(this@DownloadWaiting, Login::class.java)
            startActivity(intent)
        }
    }

    private fun readCsv(filePath: String) {

        // 讀取 CSV 文件
        val file = File(filePath)
        val inputStream = BufferedReader(FileReader(file))

        // 逐一讀取、輸出內容
        var line: String? = inputStream.readLine()
        while (line != null) {
            println(line)
            line = inputStream.readLine()
        }

        inputStream.close()
    }

    private fun connectFTP() {
        ftpClient = FTPClient()
        try {
            //1.連線遠端FTP
            ftpClient.connect("10.60.200.37",21)
            ftpClient.login("tester","eugenemiku")
            //ftpClient.connect("192.168.91.1", 21)
            //ftpClient.login("eugene", "eugenemiku")
            ftpClient.enterLocalPassiveMode()
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

            // 切換到遠端目錄
            //ftpClient.changeWorkingDirectory("/POSUp") //切換到遠端目錄

            // FTP連接成功
            Log.d("FTP 連線成功", "Connected to FTP server")

            //請下載的檔案不要有中文

            // 確保外部存儲目錄可用
            val externalFilesDir = this.getExternalFilesDir(null)   //儲存到sd card
            //val externalFilesDir = requireContext().filesDir //儲存內部私有空間
            externalFilesDir?.let { externalDir ->
                // 2. 創建 DevicePOS 資料夾在外部存儲目錄中
                val devicePOSDirectory = File(externalDir, "devicePOS")
                if (!devicePOSDirectory.exists()) {
                    devicePOSDirectory.mkdirs() // 如果目錄不存在，則創建它
                } else {
                    Log.d("絕對路徑名稱", devicePOSDirectory.absolutePath)
                }

                downloadDirectory(ftpClient, "", devicePOSDirectory)

            } ?: run {
                Log.e("外部存儲目錄不可用", "無法獲取外部存儲目錄")
            }

        } catch (e: IOException) {
            // FTP連接失敗
            Log.e("FTP 連線失敗", "Failed to connect to FTP server: ${e.message}")
        }
    }

    private fun downloadDirectory(ftpClient: FTPClient, remoteDirPath: String, localDir: File) {
        val files = ftpClient.listFiles(remoteDirPath)
        val filesMap = files.map { it.name to it }.toMap() // 將檔案列表轉換為名稱與檔案對應的 Map
        val downloadedFiles = mutableSetOf<String>() // 追蹤已下載的檔案名稱

        for ((fileName, file) in filesMap) {
            val remoteFilePath = remoteDirPath + "/" + fileName
            val localFile = File(localDir, fileName)

            if (file.isDirectory) {
                if (!localFile.exists()) {
                    localFile.mkdirs()
                }
                downloadDirectory(ftpClient, remoteFilePath, localFile) // 全部資料夾完整下載
            } else if ((remoteDirPath.endsWith("/POSUp") || remoteDirPath.endsWith("/POSDown/FullFile"))) {
                if (fileName.endsWith(".OK")){
                    // 在 POSUp 和 POSDown 資料夾中檢查檔案名稱是否以 ".OK" 結尾
                    val baseFileName = fileName.removeSuffix(".OK")
                    val baseLocalFile = File(localDir, baseFileName)

                    // 檢查對應的檔案是否存在，若存在則下載
                    if (filesMap.containsKey(baseFileName)) {
                        val outputStream = BufferedOutputStream(FileOutputStream(baseLocalFile))
                        ftpClient.retrieveFile(remoteFilePath, outputStream)
                        outputStream.close()
                        downloadedFiles.add(baseFileName) // 將已下載的檔案名稱加入集合
                    }
                }
            } else {
                // 在其他資料夾中直接下載檔案
                val outputStream = BufferedOutputStream(FileOutputStream(localFile))
                ftpClient.retrieveFile(remoteFilePath, outputStream)
                outputStream.close()
                downloadedFiles.add(fileName) // 將已下載的檔案名稱加入集合
            }
        }
    }
}