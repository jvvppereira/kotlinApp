package com.example.user.kotlinapp.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import com.example.user.kotlinapp.util.FirebaseSettings
import com.example.user.kotlinapp.util.recyclerView.ItemListAdapter
import com.example.user.kotlinapp.R
import com.example.user.kotlinapp.dto.Launch
import com.example.user.kotlinapp.util.recyclerView.SwipeToDeleteCallback
import com.example.user.kotlinapp.dto.User
import com.example.user.kotlinapp.util.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val mDatabase = FirebaseSettings().getDatabaseInstance()
    val message = Message(this)
    var list: ArrayList<Launch> = arrayListOf()
    var lastIndex = 1
    var totalReceived = 0F
    var totalPayment = 0F
    var needToReceived = 0F
    var needToPay = 0F
    var userLogin = User()
    var selectedDate = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadDateOpenField()

        userLogin = intent.extras["userLogin"] as User

        loadLaunches()

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = launchesRecylerView.adapter as ItemListAdapter
                val launch = adapter.removeAt(viewHolder.adapterPosition)

                openLaunchDetails(launch.index, "DELETE")
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)

        itemTouchHelper.attachToRecyclerView(launchesRecylerView)

        addLaunchFloatActionButton.setOnClickListener({
            openLaunchDetails(lastIndex, "INSERT")
        })
    }

    fun openLaunchDetails(index: Int, operation: String) {
        openLaunchDetails(index, operation, userLogin.uid)
    }

    private fun openLaunchDetails(index: Int, operation: String, userUid: String) {
        val intent = Intent(this, LaunchActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("operation", operation)
        intent.putExtra("userUid", userUid)
        startActivity(intent)
    }

    private fun loadDateOpenField() {
        val myFormat = "MMMM, yyyy"

        var mFormatoData = SimpleDateFormat(myFormat)

        val editData = referenceDateEditText

        editData.setText(mFormatoData.format(System.currentTimeMillis()))

        editData.isFocusable = false
        editData.isClickable = true

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener {
            view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            selectedDate = cal.time

            val sdf = SimpleDateFormat(myFormat, Locale("pt", "BR"))
            editData.setText(sdf.format(cal.time))

            loadLaunches()
        }

        editData.setOnClickListener {
            DatePickerDialog(this, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }

    }

    private fun loadLaunches() {
        loadLaunches(this)
    }

    private fun loadLaunches(context: MainActivity) {
        val query = mDatabase.child(FirebaseSettings.LAUNCHES)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    list = arrayListOf()
                    totalReceived = 0F
                    totalPayment = 0F
                    needToReceived = 0F
                    needToPay = 0F

                    for (launch in dataSnapshot.children) {
                        var currentLaunch = launch.getValue(Launch::class.java)!!

                        if (userLogin.uid == currentLaunch.userUid) {
                            val launchByFirebase = launch.getValue(Launch::class.java)!!

                            if (launchByFirebase.getDueDateAsDate().month == selectedDate.month) {
                                list.add(launchByFirebase)

                                if (launchByFirebase.receivement) {
                                    totalReceived += launchByFirebase.value
                                    if (launchByFirebase.paymentDate !== 0L) {
                                        needToReceived += launchByFirebase.value
                                    }
                                } else {
                                    totalPayment += launchByFirebase.value
                                    if (launchByFirebase.paymentDate !== 0L) {
                                        needToPay += launchByFirebase.value
                                    }
                                }
                            }

                            totalReceivedTextView.text = "$totalReceived"
                            totalPaymentTextView.text = "$totalPayment"
                            totalMonthTextView.text = "${totalReceived - totalPayment}"

                            needToReceivedTextView.text = "$needToReceived"
                            needToPayTextView.text = "$needToPay"
                            balanceOfMonthTextView.text = "${needToReceived - needToPay}"

                            launchesRecylerView.adapter = ItemListAdapter(list.toMutableList(), context)
                            launchesRecylerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                        }
                        lastIndex = launch.key!!.toInt() + 1
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                message.showMessage("Problema de conexão.")
            }
        })
    }
}
