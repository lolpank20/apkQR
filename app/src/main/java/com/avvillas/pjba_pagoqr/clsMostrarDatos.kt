package com.avvillas.pjba_pagoqr

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import com.google.gson.Gson
import com.avvillas.pjba_pagoqr.model.PagoQR
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.util.Log
import java.time.LocalDateTime
import kotlin.text.format
import com.google.gson.JsonObject

class clsMostrarDatos : AppCompatActivity() {

    private lateinit var btnPagar: Button
    private lateinit var txtEstado: TextView
    private lateinit var txtClientId: TextView
    private lateinit var txtReferenciaPago: TextView
    private lateinit var txtMonto: TextView
    private lateinit var txtIva: TextView
    private lateinit var txtBase: TextView

    private var datosQR: PagoQR? = null
    private var qrVigente: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_qr)

        Log.d("clsMostrarDatos", "onCreate: valorQR recibido: ${intent.getStringExtra("valorQR")}")

        btnPagar = findViewById(R.id.btnPagar)
        txtEstado = findViewById(R.id.txtEstado)
        txtClientId = findViewById(R.id.txtClientId)
        txtReferenciaPago = findViewById(R.id.txtReferenciaPago)
        txtMonto = findViewById(R.id.txtMonto)
        txtIva = findViewById(R.id.txtIva)
        txtBase = findViewById(R.id.txtBase)

        val valorQR = intent.getStringExtra("valorQR")
        if (valorQR != null) {
            procesarQR(valorQR)
            // Iniciar temporizador de 1 minuto para QR válido
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, VencimientoQRActivity::class.java)
                startActivity(intent)
                finish()
            }, 60_000) // 60,000 ms = 1 minuto
        } else {
            mostrarError("No se recibió ningún valor QR")
        }

        btnPagar.setOnClickListener { simularPago() }
    }

    private fun procesarQR(json: String) {
        try {
            val pagoQR = Gson().fromJson(json, PagoQR::class.java)
            if (!validarEstructura(pagoQR)) {
                mostrarError("El QR no contiene todos los campos requeridos.")
                return
            }
            datosQR = pagoQR
            mostrarDatos(pagoQR)


            // Temporizador local de 30 segundos desde el escaneo
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, VencimientoQRActivity::class.java)
                startActivity(intent)
                finish()
            }, 30_000)

        } catch (e: Exception) {
            mostrarError("El contenido del QR no es válido.")
        }
    }



    private fun validarEstructura(p: PagoQR): Boolean {
        return !p.client_id.isNullOrEmpty() &&
                !p.nit_compania.isNullOrEmpty() &&
                !p.referencia_pago.isNullOrEmpty() &&
                p.monto != null &&
                p.iva != null &&
                p.base != null
    }

    private fun mostrarDatos(p: PagoQR) {
        txtClientId.text = "Comercio: ${p.client_id} (NIT: ${p.nit_compania})"
        txtReferenciaPago.text = "Referencia: ${p.referencia_pago}"
        txtMonto.text = "Monto: $${p.monto}"
        txtIva.text = "IVA: $${p.iva}"
        txtBase.text = "Base: $${p.base}"
        txtEstado.text = "Estado: Vigente"
        btnPagar.isEnabled = true
    }



    private fun mostrarDatos(p: PagoQR, vigente: Boolean) {
        txtClientId.text = "Comercio: ${p.client_id} (NIT: ${p.nit_compania})"
        txtReferenciaPago.text = "Referencia: ${p.referencia_pago}"
        txtMonto.text = "Monto: $${p.monto}"
        txtIva.text = "IVA: $${p.iva}"
        txtBase.text = "Base: $${p.base}"
        txtEstado.text = if (vigente) "Estado: Vigente" else "Estado: Expirado"
        btnPagar.isEnabled = vigente
    }

    private fun mostrarError(msg: String) {
        txtClientId.text = ""
        txtReferenciaPago.text = ""
        txtMonto.text = ""
        txtIva.text = ""
        txtBase.text = ""
        txtEstado.text = msg
        btnPagar.isEnabled = false
    }

    private fun simularPago() {
        datosQR?.let {
            val monto = it.monto?.toString() ?: "0.00"  // Si es nulo, usar "0.00"
            Log.d("simularPago", "Monto a pasar al Intent: $monto")
            val intent = Intent(this, ComprobantePagoActivity::class.java)
            intent.putExtra("estado", "Aprobado")
            intent.putExtra("fechaHora", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
            intent.putExtra("valor", monto)
            intent.putExtra("referencia", it.referencia_pago)
            intent.putExtra("codigoNura", it.codigo_nura)
            intent.putExtra("nit", it.nit_compania)
            intent.putExtra("banco", it.banco)
            intent.putExtra("medioPago", it.medio_pago)
            intent.putExtra("autorizacion", it.autorizacion)
            intent.putExtra("datosUsuario", it.client_id)

            Log.d("simularPago", "Intent extras: ${intent.extras}")
            startActivity(intent)

        } ?: Log.e("simularPago", "datosQR es null")
    }
}