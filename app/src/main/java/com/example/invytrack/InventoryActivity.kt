package com.example.invytrack

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class InventoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        // Atur data RecyclerView
        aturDataRecyclerView()

        // Atur pencarian
        setupSearch()

        // Setup Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.inventory

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.DashboardActivity -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.inventory -> return@setOnItemSelectedListener true
                R.id.setting -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun aturDataRecyclerView() {
        val inventoryData = aksesItemData()
        aturRecyclerView(inventoryData)
    }

    private fun aturRecyclerView(data: ArrayList<InventoryModelClass>) {
        val rvInventoryList = findViewById<RecyclerView>(R.id.rv_inventory_list)
        val emptyStateMessage = findViewById<TextView>(R.id.empty_state_message)

        if (data.isNotEmpty()) {
            rvInventoryList.visibility = View.VISIBLE
            emptyStateMessage.visibility = View.GONE
            rvInventoryList.layoutManager = LinearLayoutManager(this)
            rvInventoryList.adapter = ItemAdapter(this, data, showIcons = true) // Sembunyikan ikon "ganti ke true untuk nampil icon sedangkan false untuk sebaliknya "
        } else {
            rvInventoryList.visibility = View.GONE
            emptyStateMessage.visibility = View.VISIBLE
        }
    }


    private fun aksesItemData(): ArrayList<InventoryModelClass> {
        val databaseHandler = DatabaseHandler(this)
        return databaseHandler.tampilInventaris()
    }

    private fun filterItemData(query: String): ArrayList<InventoryModelClass> {
        val allItems = aksesItemData()
        return if (query.isEmpty()) {
            allItems
        } else {
            allItems.filter { it.namaAlat.contains(query, ignoreCase = true) } as ArrayList<InventoryModelClass>
        }
    }

    private fun setupSearch() {
        val searchView = findViewById<SearchView>(R.id.search_view)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredData = filterItemData(newText ?: "")
                aturRecyclerView(filteredData)
                return true
            }
        })
    }

    fun updateRecordDialog(item: InventoryModelClass) {
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.dialog_update)

        val etUpdateNamaAlat = updateDialog.findViewById<EditText>(R.id.etUpdateNamaAlat)
        val etUpdateKondisi = updateDialog.findViewById<EditText>(R.id.etUpdateKondisi)
        val etUpdateLokasi = updateDialog.findViewById<EditText>(R.id.etUpdateLokasi)
        val etUpdateStock = updateDialog.findViewById<EditText>(R.id.etUpdateStock)
        val tvUpdate = updateDialog.findViewById<TextView>(R.id.tvUpdate)
        val tvCancel = updateDialog.findViewById<TextView>(R.id.tvCancel)

        etUpdateNamaAlat.setText(item.namaAlat)
        etUpdateKondisi.setText(item.kondisi)
        etUpdateLokasi.setText(item.lokasi)
        etUpdateStock.setText(item.stock)

        tvUpdate.setOnClickListener {
            val namaAlat = etUpdateNamaAlat.text.toString()
            val kondisi = etUpdateKondisi.text.toString()
            val lokasi = etUpdateLokasi.text.toString()
            val stock = etUpdateStock.text.toString()

            val databaseHandler = DatabaseHandler(this)

            if (namaAlat.isNotEmpty() && kondisi.isNotEmpty() && lokasi.isNotEmpty() && stock.isNotEmpty()) {
                val status = databaseHandler.updateInventaris(
                    InventoryModelClass(item.id, namaAlat, kondisi, lokasi, stock)
                )
                if (status > -1) {
                    Toast.makeText(applicationContext, "Data diperbarui.", Toast.LENGTH_LONG).show()

                    aturDataRecyclerView()
                    updateDialog.dismiss()
                }
            } else {
                Toast.makeText(applicationContext, "Semua field harus diisi", Toast.LENGTH_LONG).show()
            }
        }

        tvCancel.setOnClickListener {
            updateDialog.dismiss()
        }
        updateDialog.show()
    }

    fun deleteRecordAlertDialog(item: InventoryModelClass) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Hapus Data")
        builder.setMessage("Apakah Anda yakin ingin menghapus ${item.namaAlat}?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Ya") { dialogInterface, _ ->
            val databaseHandler = DatabaseHandler(this)
            val status = databaseHandler.hapusInventaris(item.id)
            if (status > -1) {
                Toast.makeText(
                    applicationContext,
                    "Data berhasil dihapus.",
                    Toast.LENGTH_LONG
                ).show()
                aturDataRecyclerView()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Gagal menghapus data.",
                    Toast.LENGTH_LONG
                ).show()
            }
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("Tidak") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: android.app.AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    private fun hitungTotalBarang(): Int {
        val databaseHandler = DatabaseHandler(this)
        return databaseHandler.tampilInventaris().size
    }

    private fun hitungStokMenipis(): Int {
        val databaseHandler = DatabaseHandler(this)
        return databaseHandler.tampilInventaris().count { it.stock.toInt() < 5 }
    }

    private fun updateStatistik() {
        val totalBarang = hitungTotalBarang()
        val stokMenipis = hitungStokMenipis()

        findViewById<TextView>(R.id.total_items).text = "Total Barang: $totalBarang"
        findViewById<TextView>(R.id.low_stock).text = "Stok Menipis: $stokMenipis"
    }
}
