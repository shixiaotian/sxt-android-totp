package com.shixiaotian.totp.scan.application.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.shixiaotian.totp.scan.application.R
import com.shixiaotian.totp.scan.application.db.DatabaseHelper
import com.shixiaotian.totp.scan.application.tools.EncodeTools
import com.shixiaotian.totp.scan.application.tools.MyTimeUtils

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CodeShowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CodeShowFragment : Fragment() {
    private var codeView: TextView? =null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var start: Long = 30000
    private val handler = Handler()
    private var runnable: Runnable? = null
    private var secretKey: String =""
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var id: String? = param1
        if(id == null) {
            id = "0"
        }
        // 查询数据库
        val dbHelper = DatabaseHelper(requireContext())
        val user = dbHelper.getUser(id.toInt())
        val view = inflater.inflate(R.layout.fragment_code_show, container, false)
        // 初始化进度条
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        // 设置进度条的最大值
        progressBar.max = 30000
        // 设置当前进度
        progressBar.progress = 30000
        // 显示进度条
        progressBar.visibility = ProgressBar.VISIBLE

        val showIssuerTextView = view.findViewById<TextView>(R.id.showIssuerTextView)
        val usernameView = view.findViewById<TextView>(R.id.showUsernameTextView)
        codeView = view.findViewById<TextView>(R.id.showCodeView)
        val timeView3 = view.findViewById<TextView>(R.id.showTimeView)
        if(user != null) {
            secretKey = user!!.getSecretKey();
            // 开启个线程，动态计算密钥，并更新到ui界面
            showIssuerTextView.setText(user.getIssuer())
            usernameView.setText(user.getUsername())
            if (codeView != null) {
                codeView!!.setText(EncodeTools.encode(user.getSecretKey()))
            }
            timer(timeView3)
        }

        // 删除按钮
        val deleteButton = view.findViewById<TextView>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(id)
        }

        return view
    }

    private fun refresh() {
        codeView!!.setText(EncodeTools.encode(secretKey))
    }

    private fun timer(textView : TextView) {
        // 动态计算当前秒数
        start = MyTimeUtils.getCurrentSec()

        runnable = Runnable {

            val formattedNumber = String.format("%02d",start/1000)
            textView.setText(formattedNumber + "s")
            progressBar.setProgress(start.toInt());
            start = start -100
            if(start < 0) {
                start= MyTimeUtils.getCurrentSec()
                var refreshRunnable =  Runnable {
                    refresh()
                }
                Thread(refreshRunnable).start()

            }
            // 在这里设置下一次循环的延时时间，例如1秒
            handler.postDelayed(runnable!!, 100)
        }

        // 初始化计时器
        handler.postDelayed(runnable!!, 50) // 延时1秒后开始循环

    }

    fun showDeleteConfirmationDialog(deleteId : String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("确定要删除吗？")
            .setPositiveButton("Yes") { dialog, id ->
                // 删除操作
                val dbHelper = DatabaseHelper(requireContext())
                dbHelper.deleteUser(deleteId.toInt())
                val codeListFragment = CodeListFragment()
                parentFragmentManager.beginTransaction().replace(R.id.viewPager, codeListFragment).commit()
            }
            .setNegativeButton("No") { dialog, id ->
                // 取消操作，对话框不会被关闭
            }
            .setCancelable(false)
        val alert = builder.create()
        alert.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CodeShowFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CodeShowFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroyView() {
        if(handler!= null && runnable != null) {
            handler.removeCallbacks(runnable!!)
        }
        super.onDestroyView()
    }

}