package com.shixiaotian.totp.scan.application.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.shixiaotian.totp.scan.application.CodeListAdapter
import com.shixiaotian.totp.scan.application.R
import com.shixiaotian.totp.scan.application.db.DatabaseHelper
import com.shixiaotian.totp.scan.application.tools.MyTimeUtils
import com.shixiaotian.totp.scan.application.vo.User

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CodeListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CodeListFragment : Fragment() {
    private var start: Long = 30
    private lateinit var adapter: CodeListAdapter
    private val handler = Handler()
    private var runnable: Runnable? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var userList: List<User>? =null

    private val data = listOf("apple","pear","apricot","peach","grape","banana","pineapple","plum","watermelon","orange","lemon","mango","strawberry",
        "medlar","mulberry","nectarine","cherry","pomegranate","fig","persimmon")

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
        // 获取视图
        val view = inflater.inflate(R.layout.fragment_code_list, container, false)
        val listView = view.findViewById<ListView>(R.id.listView)
        var textView3 = view.findViewById<TextView>(R.id.textView3)
        // 查询数据库
        val dbHelper = DatabaseHelper(requireContext())
        userList = dbHelper.getAllUser();
        //获取当前分钟秒数
        // 启动定时器
        timer(textView3)

        // 创建ArrayAdapter，将数据源传递给它
        adapter = CodeListAdapter(requireContext(), R.layout.code_item, userList!!)

        // 将适配器与ListView关联
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val textView = view.findViewById<TextView>(R.id.user_id);

            val fragment = CodeShowFragment.newInstance(textView.text.toString(), "")
            // 执行跳转
            parentFragmentManager.beginTransaction().replace(R.id.viewPager, fragment).commit()
            val codeShowFragment = CodeShowFragment()
            switchFragment(codeShowFragment)

        }

        return view
    }

    // 刷新数据
    private fun refresh() {
        if(userList != null) {
            adapter.notifyDataSetChanged()
        }

    }

    // 定时器
    private fun timer(textView : TextView) {
        // 动态计算当前秒数
        start = MyTimeUtils.getCurrentSec()

        runnable = Runnable {

            val formattedNumber = String.format("%02d",start/1000)
            textView.setText(formattedNumber + "s")
            start = start -100
            if(start <0) {
                refresh()
                start= MyTimeUtils.getCurrentSec()
            }
            // 在这里设置下一次循环的延时时间，例如1秒
            handler.postDelayed(runnable!!, 100)
        }

        // 初始化计时器
        handler.postDelayed(runnable!!, 50) // 延时1秒后开始循环

    }



    @SuppressLint("SuspiciousIndentation")
    private fun switchFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()

        transaction.show(fragment)

        transaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CodeListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CodeListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroyView() {
        println("onDestroyView")
        handler.removeCallbacks(runnable!!)
        super.onDestroyView()
    }
}