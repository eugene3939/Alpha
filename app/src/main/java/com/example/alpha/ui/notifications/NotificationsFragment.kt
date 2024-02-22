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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.IOException
import java.sql.Connection

class NotificationsFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var ftpClient: FTPClient

    var connect: Connection? = null
    var ConnectionResult = ""

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
            //ftpClient.connect("192.168.91.1", 21)
            //ftpClient.login("eugene", "eugenemiku")
            ftpClient.connect("10.60.200.22",21)
            ftpClient.login("tester","eugenemiku")
            ftpClient.enterLocalPassiveMode()
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

            // FTP連接成功
            Log.d("FTP 連線成功", "Connected to FTP server")

            // FTP操作，下載或上傳檔案
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

    override fun onDestroyView() {
        super.onDestroyView()
        // 關閉 CoroutineScope
        cancel()
        _binding = null
    }
}