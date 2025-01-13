package com.example.invytrack

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1 // Tentukan request code

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Setup greeting text
        setupGreeting()

        // Atur data RecyclerView
        aturDataRecyclerView()

        // Perbarui statistik
        updateStatistik()

        // Atur pencarian
        setupSearch()

        findViewById<Button>(R.id.btn_form_input).setOnClickListener {
            val intent = Intent(this, FormInputActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        // Setup Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.DashboardActivity

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.DashboardActivity -> return@setOnItemSelectedListener true
                R.id.inventory -> {
                    startActivity(Intent(this, InventoryActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
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

    private fun setupGreeting() {
        val greetingText = findViewById<TextView>(R.id.greeting_text)
        val subGreetingText = findViewById<TextView>(R.id.sub_greeting_text)

        // Get user email from SharedPreferences
        val sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("USER_EMAIL", null)

        // If we have a stored email, use it
        if (userEmail != null) {
            val userName = userEmail.split("@")[0].capitalize()
            greetingText.text = "Hi, $userName"
        } else {
            // If no stored email, try to get it from intent
            val intentEmail = intent.getStringExtra("USER_EMAIL")
            if (intentEmail != null) {
                val userName = intentEmail.split("@")[0].capitalize()
                greetingText.text = "Hi, $userName"

                // Store the email for future use
                with(sharedPref.edit()) {
                    putString("USER_EMAIL", intentEmail)
                    apply()
                }
            } else {
                greetingText.text = "Hi, User"
            }
        }

        // Set sub-greeting text
        subGreetingText.text = "Cek lagi persediaan inventaris kamu"
    }

    private fun aturDataRecyclerView() {
        aturRecyclerView(aksesItemData())
    }

    private fun aturRecyclerView(data: ArrayList<InventoryModelClass>) {
        val rvItemData = findViewById<RecyclerView>(R.id.rv_ItemData)
        val tvNoRecordsAvailable = findViewById<TextView>(R.id.tvNoRecordsAvailable)

        if (data.isNotEmpty()) {
            rvItemData.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE

            // Setup RecyclerView Adapter
            val adapter = ItemAdapter(this, data, true) // Sembunyikan ikon "ganti ke true untuk nampil icon sedangkan false untuk sebaliknya "
            rvItemData.layoutManager = LinearLayoutManager(this)
            rvItemData.adapter = adapter
        } else {
            rvItemData.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Data berhasil ditambahkan, update RecyclerView
            aturDataRecyclerView()
            updateStatistik()
        }
    }

    private fun tambahData() {
        val etNamaAlat = findViewById<EditText>(R.id.et_nama_alat)
        val etKondisi = findViewById<EditText>(R.id.et_kondisi)
        val etLokasi = findViewById<EditText>(R.id.et_lokasi)
        val etStock = findViewById<EditText>(R.id.et_stock)

        val namaAlat = etNamaAlat.text.toString()
        val kondisi = etKondisi.text.toString()
        val lokasi = etLokasi.text.toString()
        val stock = etStock.text.toString()

        val databaseHandler = DatabaseHandler(this)

        if (namaAlat.isNotEmpty() && kondisi.isNotEmpty() && lokasi.isNotEmpty() && stock.isNotEmpty()) {
            val status = databaseHandler.tambahInventaris(
                InventoryModelClass(0, namaAlat, kondisi, lokasi, stock)
            )

            if (status > -1) {
                Toast.makeText(applicationContext, "Data disimpan", Toast.LENGTH_LONG).show()

                // Bersihkan input setelah menambahkan data
                etNamaAlat.text.clear()
                etKondisi.text.clear()
                etLokasi.text.clear()
                etStock.text.clear()

                // Panggil fungsi untuk memperbarui data di RecyclerView
                aturDataRecyclerView()

                // Perbarui statistik
                updateStatistik()
            }
        } else {
            Toast.makeText(applicationContext, "Semua field harus diisi", Toast.LENGTH_LONG).show()
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
                    updateStatistik()
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
                updateStatistik()
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