package com.my.muhammadaliftajudin.readsmsyp

import android.Manifest.permission_group.SMS
import android.app.ListActivity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.CursorAdapter
import com.my.muhammadaliftajudin.readsmsyp.databinding.ActivityMainBinding

class MainActivity : ListActivity() {

    private lateinit var binding: ActivityMainBinding

    // refers to content provider to have access to sms
    val SMS = Uri.parse("content://sms")

    // code permission declared by us for sms
    val PERMSSION_REQUEST_READ_SMS = 1

    // SMS will be store inside a database in Android system
    // we will retrieve ths SMSs

    object SmsColumns {
        val ID = "_id"
        val ADDRESS = "address"
        val DATE = "date"
        val BODY = "body"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            readSms()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf (android.Manifest.permission.READ_SMS),
                PERMSSION_REQUEST_READ_SMS)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            PERMSSION_REQUEST_READ_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readSms()
                } else {
                    Toast.makeText(this, "Permission not granted",Toast.LENGTH_LONG ).show()
                }
                return
            }
        }
    }

    // IN XO1 we go through about Adapter
    // An adapter is the link between a UI element and the data source
    // Because some UI element relies on data fot it to work
    // RecycleView, Spinner, AutoCompleteTextView, ListView
    // In this example, the SMS, retrieved from Content Provider content://sms
    // Will be shown inside a ListView
    // We will link the data retrieved to the ListView with an adapter

    private inner class SmsCursorAdapter(context: Context, c: Cursor, autoRequery: Boolean) :
        CursorAdapter(context, c, autoRequery) {
            // onCreateViewHolder
            override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
                return View.inflate(context, R.layout.activity_main, null)
            }
            // onBindViewHolder
            override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
                view!!.findViewById<TextView>(R.id.sms_origin).text =
                    cursor!!.getString(cursor.getColumnIndexOrThrow(SmsColumns.ADDRESS))
                view.findViewById<TextView>(R.id.sms_body).text =
                    cursor.getString(cursor.getColumnIndexOrThrow(SmsColumns.BODY))
                view.findViewById<TextView>(R.id.sms_date).text =
                    cursor.getString(cursor.getColumnIndexOrThrow(SmsColumns.DATE))
            }
        }


    private fun readSms() {
        // How to call Content Provider
        val cursor = contentResolver.query(SMS,
            arrayOf(
                SmsColumns.ID,
                SmsColumns.ADDRESS,
                SmsColumns.DATE,
                SmsColumns.BODY
            ),
            null,
            null,
            SmsColumns.DATE + " DESC"
        )

        val adapter = SmsCursorAdapter(this, cursor!!, true)
        listAdapter = adapter
    }
}

