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
import java.sql.Connection

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        getTextFromSQL(binding.txtConnectionTest1,binding.txtConnectionTest2)

        return root
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

                    println(rs.getString(1) + "\t" +
                            rs.getString(2) + "\t")
                }

                connect!!.close()
            } else {
                ConnectionResult = "Check Connection"
            }
        } catch (ex: Exception) {
            Log.e("連線錯誤", ex.message!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}