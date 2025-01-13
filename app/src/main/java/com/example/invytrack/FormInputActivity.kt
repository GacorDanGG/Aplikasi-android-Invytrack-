package com.example.invytrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FormInputActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_input)

        val etNamaAlat = findViewById<EditText>(R.id.et_nama_alat)
        val etKondisi = findViewById<EditText>(R.id.et_kondisi)
        val etLokasi = findViewById<EditText>(R.id.et_lokasi)
        val etStock = findViewById<EditText>(R.id.et_stock)
        val btnSimpan = findViewById<Button>(R.id.btn_simpan)

        btnSimpan.setOnClickListener {
            val namaAlat = etNamaAlat.text.toString()
            val kondisi = etKondisi.text.toString()
            val lokasi = etLokasi.text.toString()
            val stock = etStock.text.toString()

            if (namaAlat.isNotEmpty() && kondisi.isNotEmpty() && lokasi.isNotEmpty() && stock.isNotEmpty()) {
                val databaseHandler = DatabaseHandler(this)
                val status = databaseHandler.tambahInventaris(
                    InventoryModelClass(0, namaAlat, kondisi, lokasi, stock)
                )

                if (status > -1) {
                    Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_LONG).show()

                    // Kirim hasil kembali ke MainActivity
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_LONG).show()
            }
        }

    }

}