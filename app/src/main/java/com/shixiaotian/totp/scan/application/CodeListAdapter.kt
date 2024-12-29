package com.shixiaotian.totp.scan.application

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.shixiaotian.totp.scan.application.tools.EncodeTools
import com.shixiaotian.totp.scan.application.vo.User

/**
 * 动态码列表内容适配器
 */
class CodeListAdapter (context: Context, val resourceId: Int, data: List<User>) : ArrayAdapter<User>(context, resourceId, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resourceId, parent, false)
        val userId: TextView = view.findViewById(R.id.user_id)
        val issuer: TextView = view.findViewById(R.id.user_issuer)
        val username: TextView = view.findViewById(R.id.user_username)
        val userSecretKey: TextView = view.findViewById(R.id.user_secretKey)
        val userCode: TextView = view.findViewById(R.id.user_code)

        val user = getItem(position)

        if (user!=null){

            userId.text = user.getId().toString()
            issuer.text = user.getIssuer()
            username.text = user.getUsername()
            userSecretKey.text = user.getSecretKey()

            var code = EncodeTools.encode(user.getSecretKey()) as String
            user.setCode(code)
            userCode.text = user.getCode()
        }
        return view
    }
}
