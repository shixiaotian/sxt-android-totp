package com.shixiaotian.totp.scan.application.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.shixiaotian.totp.scan.application.db.DatabaseHelper
import com.shixiaotian.totp.scan.application.tools.EncodeTools
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.shixiaotian.totp.scan.application.R

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CodeAddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CodeAddFragment : Fragment() {
    private lateinit var saveButton: View
    private lateinit var scanButton: View
    private lateinit var nameText: TextView
    private lateinit var secretKeyText: TextView
    private lateinit var issuerText: TextView

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_code_add, container, false)
        val dbHelper = DatabaseHelper(requireContext())
        saveButton = view.findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            nameText = view.findViewById<TextView>(R.id.addUsernameText)
            val name = nameText.getText();
            secretKeyText = view.findViewById<TextView>(R.id.addSecretKeyText)
            val secretKey = secretKeyText.getText();
            issuerText = view.findViewById<TextView>(R.id.addIssuerText)
            val issuer = issuerText.getText();

            if(name.isEmpty()) {
                alertAddError("Name can't be blank")
            } else if(secretKey.isEmpty()) {
                alertAddError("SecretKey can't be blank")
            } else if(issuer.isEmpty()){
                alertAddError("issuer can't be blank")
            } else {

                var saveId = dbHelper.insertUser(name.toString(), secretKey.toString(), issuer.toString());
                nameText.setText("")
                secretKeyText.setText("")
                issuerText.setText("")

                val fragment = CodeShowFragment.newInstance(saveId.toString(), "")
                parentFragmentManager.beginTransaction().replace(R.id.viewPager, fragment).commit()

            }
        }

        scanButton = view.findViewById(R.id.cameraButton)

        scanButton.setOnClickListener {

            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setOrientationLocked(false)
            integrator.captureActivity = CaptureActivity::class.java
            integrator.setRequestCode(5766)  //_scan为自己定义的请求码
            integrator.initiateScan()
        }

        return view
    }

    fun alertAddError(msg : String) {
        println("alertAddError : " + msg)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("add error")
        builder.setMessage(msg)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            //_scan为自己定义的扫码请求码
            5766 -> {
                // 跳转扫描页面返回扫描数据
                var scanResult = IntentIntegrator.parseActivityResult(
                    IntentIntegrator.REQUEST_CODE,
                    resultCode,
                    data
                );
                //  判断返回值是否为空
                if (scanResult != null) {
                    //返回条形码数据
                    var result = scanResult.contents
                    if(result == null) {
                        parentFragmentManager.beginTransaction().replace(R.id.viewPager, this).commit()
                        return
                    }
                    val user = EncodeTools.decode(result);

                    if(user == null) {
                        Toast.makeText(context, "Scan Fail", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.beginTransaction().replace(R.id.viewPager, this).commit()
                        return
                    }

                    // 保存数据
                    val dbHelper = DatabaseHelper(requireContext())
                    var saveId = dbHelper.insertUser(user!!.getUsername(), user.getSecretKey(), user.getIssuer())
                    if(saveId < 0 ) {
                        Toast.makeText(context, "Save Data Fail", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.beginTransaction().replace(R.id.viewPager, this).commit()
                        return
                    }

                    // 跳转
                    val fragment = CodeShowFragment.newInstance(saveId.toString(), "")
                    parentFragmentManager.beginTransaction().replace(R.id.viewPager, fragment).commit()
                } else {
                    Toast.makeText(context, "Scan Fail:ERROR", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction().replace(R.id.viewPager, this).commit()

                }
            } else -> {
                val fragment = CodeAddFragment()
                parentFragmentManager.beginTransaction().replace(R.id.viewPager, fragment).commit()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CodeAddFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CodeAddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}