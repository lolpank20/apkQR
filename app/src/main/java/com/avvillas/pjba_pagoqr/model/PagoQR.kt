package com.avvillas.pjba_pagoqr.model

data class PagoQR(
    val client_id: String?,
    val client_secret: String?,
    val scope: String?,
    val codigo_nura: String?,
    val nit_compania: String?,
    val id_banco: String?,
    val referencia_pago: String?,
    val monto: Double?,
    val iva: Double?,
    val base: Double?,
    val tipo_canal: String?,
)