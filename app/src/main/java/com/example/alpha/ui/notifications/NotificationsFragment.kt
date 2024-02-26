package com.example.alpha.ui.notifications

import android.R.attr
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.alpha.ConnectionHelper
import com.example.alpha.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.sql.Connection
import android.R.attr.path

import android.os.Environment
import java.io.FileNotFoundException


class NotificationsFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var ftpClient: FTPClient

    var connect: Connection? = null
    var ConnectionResult = ""

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //getTextFromSQL(binding.txtConnectionTest1,binding.txtConnectionTest2)

        //FTP連線
        GlobalScope.launch(Dispatchers.IO) {
            // 在 IO 調度器中呼叫 connectFTP()
            connectFTP()
        }

        return root
    }

    private fun connectFTP() {
        ftpClient = FTPClient()
        try {
            ftpClient.connect("192.168.91.1", 21)
            ftpClient.login("eugene", "eugenemiku")
            ftpClient.enterLocalPassiveMode()
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

            // FTP連接成功
            Log.d("FTP 連線成功", "Connected to FTP server")

            // 確保外部存儲目錄可用
            val externalFilesDir = requireContext().getExternalFilesDir(null)
            externalFilesDir?.let { externalDir ->
                // 2. 創建 DevicePOS 資料夾在外部存儲目錄中
                val devicePOSDirectory = File(externalDir, "devicePOS")
                if (!devicePOSDirectory.exists()) {
                    devicePOSDirectory.mkdirs() // 如果目錄不存在，則創建它
                } else {
                    Log.d("絕對路徑名稱", devicePOSDirectory.absolutePath)
                }

                // 列出遠端目錄中的文件列表
                val files = ftpClient.listFiles()
                for (file in files) {
                    // 打印文件名稱
                    Log.d("遠端文件", file.name)

                    // 3. 下載檔案到 DevicePOS 資料夾中
                    val localFile = File(devicePOSDirectory, file.name)
                    val outputStream = FileOutputStream(localFile)

                    // 下載遠端檔案並寫入到本地檔案
                    ftpClient.retrieveFile(file.name, outputStream)

                    Log.d("下載成功", "儲存在: ${localFile.absolutePath}")
                }

                // 檢查是否成功下載到外部存儲目錄中的 devicePOS 目錄
                if (devicePOSDirectory.exists() && devicePOSDirectory.isDirectory) {
                    val j = devicePOSDirectory.listFiles()
                    for (i in j!!) {
                        Log.d("下載目錄", i.name)
                    }
                } else {
                    Log.d("DevicePOS", "Directory does not exist or is not a directory")
                }
            } ?: run {
                Log.e("外部存儲目錄不可用", "無法獲取外部存儲目錄")
            }

        } catch (e: IOException) {
            // FTP連接失敗
            Log.e("FTP 連線失敗", "Failed to connect to FTP server: ${e.message}")
        }
    }

    private fun getTextFromSQL(txtConnectionTest1: TextView, txtConnectionTest2: TextView) {
        try {
            val connectionHelper = ConnectionHelper()
            connect = connectionHelper.connectionClass()
            if (connect != null) {
                val query = "Select * From BARCODE"
                val st = connect!!.createStatement()
                val rs = st.executeQuery(query)
                while (rs.next()) {
                    txtConnectionTest1.text = rs.getString(1)
                    txtConnectionTest2.text = rs.getString(2)

                    //顯示資料內容

                    println(rs.getString(1) + "\t" +
                            rs.getString(2) + "\t")
                }

                connect!!.close()
            } else {
                ConnectionResult = "Check Connection"
            }
        } catch (ex: Exception) {
            Log.e("連線錯誤(kt)", ex.message!!)
        }
    }

    // 將源目錄複製到目標目錄
    fun copyDirectory(sourceDirPath: String, targetDirPath: String) {
        val sourceDir = File(sourceDirPath)
        val targetDir = File(targetDirPath)

        if (!sourceDir.exists() || !sourceDir.isDirectory) {
            throw IllegalArgumentException("資料不存在")
        }

        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }

        Files.walk(Paths.get(sourceDirPath)).forEach { sourcePath ->
            val relativePath = sourceDir.toPath().relativize(sourcePath)
            val targetPath = Paths.get(targetDirPath, relativePath.toString())

            if (Files.isDirectory(sourcePath)) {
                Files.createDirectories(targetPath)
            } else {
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // 關閉 CoroutineScope
        cancel()
        _binding = null
    }
}