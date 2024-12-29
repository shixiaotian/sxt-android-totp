package com.shixiaotian.totp.scan.application

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import com.shixiaotian.totp.scan.application.fragments.CodeAddFragment
import com.shixiaotian.totp.scan.application.fragments.CodeListFragment
import com.shixiaotian.totp.scan.application.db.DatabaseHelper
import com.shixiaotian.totp.scan.application.tools.FirstRunTools
import net.sqlcipher.database.SQLiteDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var listButton: View
    private lateinit var addButton: View
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // 初始化预处理
        init()

        val codeAddFragment = CodeAddFragment()
        val codeListFragment = CodeListFragment()

        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.viewPager, codeListFragment).commit()

        // 菜单按钮监听
        listButton = findViewById(R.id.menuButton)
        listButton.setOnClickListener {
            fragmentManager.beginTransaction().replace(R.id.viewPager, codeListFragment).commit()

        }

        // 添加按钮监听
        addButton = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            fragmentManager.beginTransaction().replace(R.id.viewPager, codeAddFragment).commit()

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init() {
        SQLiteDatabase.loadLibs(this);
        println("---开始初始化")
        // 判断是否首次运行
        if(FirstRunTools.isFirstRun(this)) {
            println("---首次运行触发")
            val dbHelper = DatabaseHelper(this)
            // 初始化数据库
            dbHelper.initDB()
            dbHelper.init()
        }
    }



}