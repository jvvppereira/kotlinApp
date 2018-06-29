package com.example.user.kotlinapp.util.recyclerView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import com.example.user.kotlinapp.R
import com.example.user.kotlinapp.activity.LaunchActivity
import com.example.user.kotlinapp.dto.Launch
import kotlinx.android.synthetic.main.launch.view.*
import java.text.SimpleDateFormat

class ItemListAdapter(
        private val items: MutableList<Launch>,
        private val context: Context) : Adapter<ItemListAdapter.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val event = items[position]
        holder?.let{
            it.bindView(event)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.launch, parent, false)

        return ViewHolder(view)
    }

    fun removeAt(position: Int) : Launch {
        val launch = items.removeAt(position)
        notifyItemRemoved(position)
        return launch
    }

    class ViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        fun bindView(launch: Launch) {
            val name = itemView.nameTextView
            val value = itemView.valueTextView
            val dueDate = itemView.paymentDateTextView
            val payOrReceive = itemView.payReceiveTextView

            name.text = launch.name
            value.text = launch.value.toString()
            dueDate.text =
                    if (launch.paymentDate !== 0L)
                        SimpleDateFormat("dd/MM/yyyy").format(launch.getPaymentDateAsDate())
                    else ""
            payOrReceive.text = if (launch.receivement) "Recebimento" else "Pagamento"

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, LaunchActivity::class.java)
                intent.putExtra("index", launch.index)
                intent.putExtra("operation", "UPDATE")
                intent.putExtra("userUid", launch.userUid)
                intent.putExtra("launch", launch)
                itemView.context.startActivity(intent)
            }
        }
    }
}

