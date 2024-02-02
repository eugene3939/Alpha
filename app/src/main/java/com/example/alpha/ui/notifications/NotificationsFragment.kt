package com.example.alpha.ui.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.alpha.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val url = "jdbc:jtds:sqlserver://192.168.91.1:1433/kgpos_test"
        val user = "kgpos"
        val password = "admkgpos"

        // 使用協程來執行資料庫操作
        lifecycleScope.launch {
            var connection: Connection? = null
            var statement: Statement? = null

            try {
                // 連接資料庫
                connection = withContext(Dispatchers.IO) {
                    DriverManager.getConnection(url, user, password)
                }
                statement = connection.createStatement()

                // 執行查詢
                val resultSet = statement.executeQuery("SELECT userName, password FROM UserTable")

                // 將結果集中的資料顯示在 TextView 上
                while (resultSet.next()) {
                    val userName = resultSet.getString("userName")
                    val password = resultSet.getString("password")

                    // 假設存在名為 textView 的 TextView
                    binding.txtConnectionTest1.text = "使用者名稱: $userName\n密碼: $password"
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                // 關閉連線和聲明
                statement?.close()
                connection?.close()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}