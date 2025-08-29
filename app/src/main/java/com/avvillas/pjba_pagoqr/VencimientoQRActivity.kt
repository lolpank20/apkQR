package com.avvillas.pjba_pagoqr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class VencimientoQRActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vencimiento_qr)

        findViewById<Button>(R.id.btnVencidoCerrar).setOnClickListener {
            finish()
        }
    }
}
