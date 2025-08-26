package com.avvillas.pjba_pagoqr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import com.google.gson.Gson
import com.avvillas.pjba_pagoqr.model.PagoQR
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDateTime


class clsMostrarDatos : AppCompatActivity() {

    private lateinit var txtDatos: TextView
    private lateinit var btnPagar: Button
    private lateinit var txtEstado: TextView

    private var datosQR: PagoQR? = null
    private var qrVigente: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_qr)

        txtDatos = findViewById(R.id.txtDatos)
        btnPagar = findViewById(R.id.btnPagar)
        txtEstado = findViewById(R.id.txtEstado)

        val valorQR = intent.getStringExtra("valorQR")
        if (valorQR != null) {
            procesarQR(valorQR)
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
        txtDatos.text = """
        Comercio: ${p.client_id} (NIT: ${p.nit_compania})
        Referencia: ${p.referencia_pago}
        Monto: $${p.monto}
        IVA: $${p.iva}
        Base: $${p.base}
    """.trimIndent()
        txtEstado.text = "Estado: Vigente"
        btnPagar.isEnabled = true
    }

    // Usar SimpleDateFormat para compatibilidad con API 24+
    private fun validarVigencia(fecha: String?): Boolean {
        return try {
            val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val fechaExp = formato.parse(fecha)
            val ahora = Date()
            fechaExp != null && ahora.before(fechaExp)
        } catch (e: Exception) {
            false
        }
    }

    private fun mostrarDatos(p: PagoQR, vigente: Boolean) {
        txtDatos.text = """
            Comercio: ${p.client_id} (NIT: ${p.nit_compania})
            Referencia: ${p.referencia_pago}
            Monto: $${p.monto}
            IVA: $${p.iva}
            Base: $${p.base}
        """.trimIndent()
        txtEstado.text = if (vigente) "Estado: Vigente" else "Estado: Expirado"
        btnPagar.isEnabled = vigente
    }

    private fun mostrarError(msg: String) {
        txtDatos.text = ""
        txtEstado.text = msg
        btnPagar.isEnabled = false
    }

    private fun simularPago() {
        datosQR?.let {
            Toast.makeText(this, "¡Pago exitoso!\nReferencia: ${it.referencia_pago}\nMonto: $${it.monto}", Toast.LENGTH_LONG).show()
        }
    }
}