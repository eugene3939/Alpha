package com.example.alpha.ui.notifications

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


class NotificationsFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var ftpClient: FTPClient

    private var connect: Connection? = null
    private var connectionResult = ""

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //更新ftp連線
        binding.btnUpdateFTP.setOnClickListener {
            //FTP連線
            GlobalScope.launch(Dispatchers.IO) {
                // 在 IO 調度器中呼叫 connectFTP()
                connectFTP()
            }
        }

        return root
    }

    private fun connectFTP() {
        ftpClient = FTPClient()
        try {
            //1.連線遠端FTP
            ftpClient.connect("10.60.200.19",21)
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
            val externalFilesDir = requireContext().getExternalFilesDir(null)   //儲存到sd card
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
                connectionResult = "Check Connection"
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