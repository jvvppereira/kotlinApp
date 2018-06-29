package com.example.user.kotlinapp.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import com.example.user.kotlinapp.R
import com.example.user.kotlinapp.dto.Launch
import com.example.user.kotlinapp.util.FirebaseSettings
import com.example.user.kotlinapp.util.Message
import kotlinx.android.synthetic.main.launch_crud.*
import java.text.SimpleDateFormat
import java.util.*

class LaunchActivity : AppCompatActivity(){

    private val mDatabase = FirebaseSettings().getDatabaseInstance()
    val message = Message(this)
    var operation: String = ""
    var index: Int = 0
    var userUid: String = ""
    val myFormat = "dd/MM/yyyy"
    val mDateFormat = SimpleDateFormat(myFormat)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launch_crud)

        index = intent.extras["index"] as Int
        operation = intent.extras["operation"] as String
        userUid = intent.extras["userUid"] as String

        if (operation == "DELETE") {
            updateFirebaseLaunch(index, HashMap<String, Object>() as Object)

            message.showMessage("Removido com sucesso")

            finish()
        } else {
            loadDateOpenField(dueDateEditText)
            loadDateOpenField(paymentDateEditText)

            if (operation == "UPDATE") {
                loadValuesOfUpdate()
            }

            floatingActionButton.setOnClickListener({
                save()
            })
        }
    }

    private fun loadValuesOfUpdate() {
        val loadedLaunch = intent.extras["launch"] as Launch

        nameEditText.text.insert(0, loadedLaunch.name)
        valueEditText.text.insert(0, loadedLaunch.value.toString())
        dueDateEditText.text.clear()
        dueDateEditText.text.insert(0, mDateFormat.format(loadedLaunch.dueDate))
        paymentDateEditText.text.clear()
        if (loadedLaunch.paymentDate === 0L) {
            paymentDateEditText.text.insert(0, "")
            dontKnowCheckBox.isChecked = true
        } else {
            paymentDateEditText.text.insert(0, mDateFormat.format(loadedLaunch.paymentDate))
        }
        if (loadedLaunch.receivement) {
            receivementRadioButton.isChecked = true
        } else {
            paymentRadioButton.isChecked = true
        }
    }

    private fun loadDateOpenField(editData: EditText) {
        editData.setText(mDateFormat.format(System.currentTimeMillis()))

        editData.isFocusable = false
        editData.isClickable = true

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener {
            view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val sdf = SimpleDateFormat(myFormat, Locale("pt", "BR"))
            editData.setText(sdf.format(cal.time))
        }

        editData.setOnClickListener {
            DatePickerDialog(this, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun save() {
        val launch = Launch()

        launch.value = valueEditText.text.toString().toFloat()
        launch.dueDate = mDateFormat.parse(dueDateEditText.text.toString()).time
        launch.paymentDate =
                if (dontKnowCheckBox.isChecked) 0
                else mDateFormat.parse(paymentDateEditText.text.toString()).time
        launch.receivement = receivementRadioButton.isChecked
        launch.name = nameEditText.text.toString()
        launch.index = index
        launch.userUid = userUid

        updateFirebaseLaunch(index, launch as Object)

        message.showMessage("Lançamento salvo com sucesso")

        finish()
    }

    fun updateFirebaseLaunch(index: Int, launch: Object) {
        val childUpdates = HashMap<String, Object>()

        childUpdates["${FirebaseSettings.LAUNCHES_BAR}$index"] = launch

        mDatabase.updateChildren(childUpdates as Map<String, Any>)
    }
}