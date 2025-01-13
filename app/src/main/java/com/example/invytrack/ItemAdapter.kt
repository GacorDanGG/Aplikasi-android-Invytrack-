package com.example.invytrack

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(
    private val context: Context,
    private val items: ArrayList<InventoryModelClass>,
    private val showIcons: Boolean // Parameter baru untuk menentukan visibilitas ikon
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.items_row, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvNamaAlat.text = item.namaAlat
        holder.tvKondisi.text = item.kondisi
        holder.tvLokasi.text = item.lokasi
        holder.tvStock.text = item.stock

        if (position % 2 == 0) {
            holder.llMain.setBackgroundColor(
                ContextCompat.getColor(context, R.color.colorLightGray)
            )
        } else {
            holder.llMain.setBackgroundColor(
                ContextCompat.getColor(context, R.color.white)
            )
        }

        // Tampilkan atau sembunyikan ikon berdasarkan parameter showIcons
        if (showIcons) {
            holder.ivEdit.visibility = View.VISIBLE
            holder.ivDelete.visibility = View.VISIBLE
        } else {
            holder.ivEdit.visibility = View.GONE
            holder.ivDelete.visibility = View.GONE
        }

        holder.ivEdit.setOnClickListener {
            when (context) {
                is DashboardActivity -> context.updateRecordDialog(item)
                is InventoryActivity -> context.updateRecordDialog(item)
            }
        }

        holder.ivDelete.setOnClickListener {
            when (context) {
                is DashboardActivity -> context.deleteRecordAlertDialog(item)
                is InventoryActivity -> context.deleteRecordAlertDialog(item)
            }
        }
        holder.ivEdit.setOnClickListener {
            when (context) {
                is DashboardActivity -> context.updateRecordDialog(item)
                is InventoryActivity -> context.updateRecordDialog(item)
            }
        }

        holder.ivDelete.setOnClickListener {
            when (context) {
                is DashboardActivity -> context.deleteRecordAlertDialog(item)
                is InventoryActivity -> context.deleteRecordAlertDialog(item)
            }
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llMain: LinearLayout = view.findViewById(R.id.llMain)
        val tvNamaAlat: TextView = view.findViewById(R.id.tv_nama_alat)
        val tvKondisi: TextView = view.findViewById(R.id.tv_kondisi)
        val tvLokasi: TextView = view.findViewById(R.id.tv_lokasi)
        val tvStock: TextView = view.findViewById(R.id.tv_stock)
        val ivEdit: ImageView = view.findViewById(R.id.iv_edit)
        val ivDelete: ImageView = view.findViewById(R.id.iv_delete)
    }
}
