package com.avvillas.pjba_pagoqr

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.util.*
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

//import de diseño movil
import android.widget.ImageButton
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast

import com.avvillas.pjba_pagoqr.databinding.ActivityMainBinding
import android.view.animation.AnimationUtils
import com.google.zxing.NotFoundException


class MainActivity : AppCompatActivity() {

    private val PICK_IMAGE = 1
    private val PICK_PHOTO_REQUEST_CODE = 1

    private lateinit var contra1: EditText
    private lateinit var contra2: EditText
    private lateinit var contra3: EditText
    private lateinit var contra4: EditText


    private lateinit var botonQR: ImageButton // el lateinit sirve para inicializar la variable despues
    private lateinit var botonScanQR: ImageButton
    private lateinit var botonCargarQR: ImageButton
    private lateinit var botonPegarQR: ImageButton


    private lateinit var frameBotones: FrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        //para las contraeñas
        contra1 = findViewById(R.id.contra1)
        contra2 = findViewById(R.id.contra2)
        contra3 = findViewById(R.id.contra3)
        contra4 = findViewById(R.id.contra4)

        //para los botones
        botonQR = findViewById(R.id.botonQR)
        botonScanQR = findViewById(R.id.botonScanQR)
        botonCargarQR = findViewById(R.id.botonCargarQR)
        botonPegarQR = findViewById(R.id.botonPegarQR)
        // para el frame
        frameBotones = findViewById(R.id.frameBotones)



        botonQR.setOnClickListener {
            if (frameBotones.visibility == View.VISIBLE) {
                val slideDown = AnimationUtils.loadAnimation(this, R.anim.esconder_opciones)
                frameBotones.startAnimation(slideDown)
                frameBotones.visibility = View.GONE
            } else {
                frameBotones.visibility = View.VISIBLE
                val slideUp = AnimationUtils.loadAnimation(this, R.anim.monstrar_opciones)
                frameBotones.startAnimation(slideUp)
            }
        }

        botonScanQR.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)//Formato de codigo QR
            integrator.setPrompt("Por favor enfoca el codigo QR") //Texto que aparece en la pantalla de la camara
            integrator.setCameraId(0) //Usar camara trasera
            integrator.setBeepEnabled(false) //Sonido al escanear
            integrator.setBarcodeImageEnabled(true) //Imagen del codigo QR
            integrator.initiateScan()
        }

        botonCargarQR.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //Intent para seleccionar una imagen
            intent.type = "image/*" //Tipo de imagen
            startActivityForResult(intent, PICK_IMAGE) //Iniciar la actividad
        }

        botonPegarQR.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData: ClipData? = clipboard.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val valorQR = clipData.getItemAt(0).text.toString()
                if (valorQR.isNotEmpty()) {
                    val intent = Intent(this, clsMostrarDatos::class.java)
                    intent.putExtra("valorQR", valorQR)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "El portapapeles está vacío", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "No hay datos en el portapapeles", Toast.LENGTH_LONG).show()
            }
        }
    }

    // funcion para el resultado del codigo QR
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //para leer el codigo QR por medio de una imagen
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) { //Si se selecciono una imagen y se obtuvo un resultado correcto
            val uri: Uri = data.data!! //Obtener la URI de la imagen
            val imageBitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            //fredy: inicio escalar imagen

            // Redimensionar la imagen
            val scaledBitmap = Bitmap.createScaledBitmap(
                imageBitmap,
                imageBitmap.width / 2,
                imageBitmap.height / 2,
                true
            )

            // Convertir a escala de grises
            val grayBitmap = Bitmap.createBitmap(
                scaledBitmap.width,
                scaledBitmap.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(grayBitmap)
            val paint = Paint()
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(colorMatrix)
            paint.colorFilter = filter
            canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)
            //fredy end_grey

            // Convertir la imagen a un arreglo de píxeles
            /*val intArray = IntArray(imageBitmap.width * imageBitmap.height)
            imageBitmap.getPixels(intArray, 0, imageBitmap.width, 0, 0, imageBitmap.width, imageBitmap.height)*/
            val intArray = IntArray(grayBitmap.width * grayBitmap.height)
            grayBitmap.getPixels(
                intArray,
                0,
                grayBitmap.width,
                0,
                0,
                grayBitmap.width,
                grayBitmap.height
            )
            // Crear la fuente de luminancia
            //val source = RGBLuminanceSource(imageBitmap.width, imageBitmap.height, intArray)
            val source = RGBLuminanceSource(grayBitmap.width, grayBitmap.height, intArray)

            // Crear una imagen binaria a partir de la fuente de luminancia
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                // Decodificar la imagen binaria para obtener el resultado del código QR
                val reader = MultiFormatReader()
                val resultado = reader.decode(bitmap)
                val valorQR = resultado.text
                //Toast.makeText(this, "El codigo QR fue leido correctamente ${resultado.text}", Toast.LENGTH_LONG).show()
                val intent = Intent(this, clsMostrarDatos::class.java)
                intent.putExtra("valorQR", valorQR)
                startActivity(intent)
            } catch (e: NotFoundException) {
                Toast.makeText(this, "El codigo QR no pudo ser leido", Toast.LENGTH_LONG).show()
            }
        } else {
            val resultado: IntentResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (resultado != null) {
                if (resultado.contents == null) {
                    Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
                } else {
                    val valorQR = resultado.contents
                    //Toast.makeText(this, "El codigo QR fue leido correctamente ${resultado.contents}",Toast.LENGTH_LONG).show()
                    val intent = Intent(this, clsMostrarDatos::class.java)
                    intent.putExtra("valorQR", valorQR)
                    startActivity(intent)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }



}
