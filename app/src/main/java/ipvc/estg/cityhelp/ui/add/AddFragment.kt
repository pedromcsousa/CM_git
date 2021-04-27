package ipvc.estg.cityhelp.ui.add

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import cn.pedant.SweetAlert.SweetAlertDialog
import ipvc.estg.cityhelp.MainActivity
import ipvc.estg.cityhelp.R
import ipvc.estg.cityhelp.api.EndPoints
import ipvc.estg.cityhelp.api.OutputGeral
import ipvc.estg.cityhelp.api.ServiceBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


class AddFragment : Fragment() {

    private lateinit var dashboardViewModel: AddViewModel
    private lateinit var foto: ImageView
    private lateinit var fotoPick: MultipartBody.Part
    var cameraIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)


    private fun startGallery() {
        val cameraIntent = Intent(Intent.ACTION_GET_CONTENT)
        cameraIntent.type = "image/*"
        if (cameraIntent.resolveActivity(this.requireActivity().packageManager) != null) {
            startActivityForResult(cameraIntent, 1000)
        }
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(context?.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun buildImageBodyPart(fileName: String, bitmap: Bitmap): MultipartBody.Part {
        val leftImageFile = convertBitmapToFile(fileName, bitmap)
        val reqFile = RequestBody.create(MediaType.parse("image/*"), leftImageFile)
        return MultipartBody.Part.createFormData(fileName, leftImageFile.name, reqFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1000) {
            val returnUri: Uri? = data!!.data
            val bitmapImage =
                MediaStore.Images.Media.getBitmap(
                    this.requireActivity().contentResolver,
                    returnUri
                )
            foto.setImageBitmap(bitmapImage)
            fotoPick = buildImageBodyPart("foto", bitmapImage)
        }
        //Uri returnUri;
        //returnUri = data.getData();
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(AddViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_add, container, false)
        val textView: TextView = root.findViewById(R.id.addSituacaoTitulo)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        //ELEMENTOS GERAIS
        foto = root.findViewById(R.id.addSituacaoFoto);

        //ELEMENTOS FORMULÁRIO
        val titulo: TextView = root.findViewById(R.id.textTituloSituacao)
        val tipo: Spinner = root.findViewById(R.id.spinnerTipoSituacao)
        val conteudo: TextView = root.findViewById(R.id.textDescSituacao)

        foto.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this.requireActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else {
                startGallery();
            }
        }

        //SPINNER - CARREGAR TIPOS
        val spinner: Spinner = root.findViewById(R.id.spinnerTipoSituacao)
        ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.tiposSituacao,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        val btnAddSituacao: Button = root.findViewById(R.id.btnAddSituacao)
        btnAddSituacao.setOnClickListener {
            var formTitulo = titulo.text.toString()
            var formTipo = tipo.selectedItemId.toInt()
            var formConteudo = conteudo.text.toString()
            if (formTitulo == "" || formTipo == 0 || formConteudo == "" || !this::fotoPick.isInitialized) {
                SweetAlertDialog(this.activity)
                    .setTitleText(getString(R.string.empty))
                    .setConfirmText("Ok")
                    .show()
            } else {
                println(formTitulo + " - " + formTipo + " - " + formConteudo)

                val sharedPref: SharedPreferences = (this.activity as MainActivity).sharedPref
                val userLogado = sharedPref.getString(getString(R.string.user), "")

                val requireView = this.requireView()

                val request = ServiceBuilder.buildServer(EndPoints::class.java)
                if (ActivityCompat.checkSelfPermission(
                        this.requireActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this.requireActivity(),
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                } else {
                    (this.requireActivity() as MainActivity).fusedLocationClient.lastLocation.addOnSuccessListener(
                        this.requireActivity()
                    ) { location ->
                        if (location != null) {
                            (this.requireActivity() as MainActivity).lastLocation = location
                            val currentLocation = location
                            val call = request.addSituacao(
                                formTitulo,
                                formConteudo,
                                formTipo.toString(),
                                fotoPick,
                                userLogado,
                                location.latitude.toString(),
                                location.longitude.toString()
                            )

                            call.enqueue(object : Callback<OutputGeral> {

                                override fun onResponse(
                                    call: Call<OutputGeral>,
                                    response: Response<OutputGeral>
                                ) {
                                    println(response.toString())
                                    if (response.isSuccessful) {
                                        Navigation.findNavController(requireView)
                                            .navigate(R.id.navigation_home);
                                    }
                                }

                                override fun onFailure(call: Call<OutputGeral>, t: Throwable) {
                                    println(t.message)
                                    println(call)
                                    Toast.makeText(activity, "${t.message}", Toast.LENGTH_SHORT)
                                        .show()
                                }

                            })
                        }
                    }
                }
            }
        }

        return root
    }
}