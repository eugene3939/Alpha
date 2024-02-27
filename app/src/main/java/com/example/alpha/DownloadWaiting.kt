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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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

            //下載完成跳轉登入頁面
            val intent = Intent(this@DownloadWaiting, Login::class.java)
            startActivity(intent)
        }
    }

    private fun connectFTP() {
        ftpClient = FTPClient()
        try {
            //1.連線遠端FTP
            ftpClient.connect("10.60.200.15",21)
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
        for (file in files) {
            val remoteFilePath = remoteDirPath + "/" + file.name
            val localFile = File(localDir, file.name)
            if (file.isDirectory) {
                if (!localFile.exists()) {
                    localFile.mkdirs()
                }
                downloadDirectory(ftpClient, remoteFilePath, localFile)
            } else {
                val outputStream = BufferedOutputStream(FileOutputStream(localFile))
                ftpClient.retrieveFile(remoteFilePath, outputStream)
                outputStream.close()
            }
        }
    }
}