package com.avvillas.pjba_pagoqr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button
import android.util.Log

class ComprobantePagoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprobante_pago)

        // Recuperar los datos del Intent (agregando un valor por defecto en caso de nulo)
        val estado = intent.getStringExtra("estado") ?: "No disponible"
        val fechaHora = intent.getStringExtra("fechaHora") ?: "Fecha desconocida"
        val montoRecibido = intent.getStringExtra("valor") ?: "0.00"
        val referencia = intent.getStringExtra("referencia") ?: "Referencia desconocida"
        val codigoNura = intent.getStringExtra("codigoNura") ?: "No disponible"
        val nit = intent.getStringExtra("nit") ?: "NIT desconocido"
        val banco = intent.getStringExtra("banco") ?: "Banco desconocido"
        val medioPago = intent.getStringExtra("medioPago") ?: "Medio de pago desconocido"
        val autorizacion = intent.getStringExtra("autorizacion") ?: "Autorización desconocida"
        val datosUsuario = intent.getStringExtra("datosUsuario") ?: "Usuario desconocido"



        Log.d("ComprobantePagoActivity", "Monto recibido: $montoRecibido")


        // Asignar los datos a los TextViews
        findViewById<TextView>(R.id.txtEstado).text = "Estado: $estado"
        findViewById<TextView>(R.id.txtFechaHora).text = "Fecha y hora: $fechaHora"
        findViewById<TextView>(R.id.txtValor).text = "Valor pagado: $montoRecibido"
        findViewById<TextView>(R.id.txtReferencia).text = "Referencia: $referencia"
        findViewById<TextView>(R.id.txtCodigoNura).text = "Código Nura: $codigoNura"
        findViewById<TextView>(R.id.txtNit).text = "NIT: $nit"
        findViewById<TextView>(R.id.txtBanco).text = "Banco: $banco"
        findViewById<TextView>(R.id.txtMedioPago).text = "Medio de pago: $medioPago"
        findViewById<TextView>(R.id.txtAutorizacion).text = "Autorización: $autorizacion"
        findViewById<TextView>(R.id.txtDatosUsuario).text = "Usuario: $datosUsuario"

        // Configurar el botón para finalizar
        findViewById<Button>(R.id.btnFinalizar).setOnClickListener {
            finish()  // Finaliza la actividad y vuelve a la anterior
        }
    }
}