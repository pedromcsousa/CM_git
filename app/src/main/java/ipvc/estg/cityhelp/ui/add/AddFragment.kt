package ipvc.estg.cityhelp.ui.add

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import cn.pedant.SweetAlert.SweetAlertDialog
import com.squareup.picasso.Picasso
import ipvc.estg.cityhelp.MainActivity
import ipvc.estg.cityhelp.R
import ipvc.estg.cityhelp.api.EndPoints
import ipvc.estg.cityhelp.api.OutputGeral
import ipvc.estg.cityhelp.api.ServiceBuilder
import ipvc.estg.cityhelp.api.Situacao
import ipvc.estg.cityhelp.ui.home.HomeFragment
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


class AddFragment : Fragment() {

    private lateinit var foto: ImageView
    private lateinit var fotoPick: MultipartBody.Part
    var cameraIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

    private var situacaoEditar: Situacao? = null

    fun setSituacaoEditar(sit: Situacao) {
        situacaoEditar = sit;
    }

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
        val root = inflater.inflate(R.layout.fragment_add, container, false)

        //ELEMENTOS FORMULÁRIO
        val titulo: TextView = root.findViewById(R.id.textTituloSituacao)
        val tipo: Spinner = root.findViewById(R.id.spinnerTipoSituacao)
        val conteudo: TextView = root.findViewById(R.id.textDescSituacao)

        //SPINNER - CARREGAR TIPOS
        ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.tiposSituacao,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tipo.adapter = adapter
        }

        //ELEMENTOS GERAIS
        foto = root.findViewById(R.id.addSituacaoFoto);
        val tituloGeral: TextView = root.findViewById(R.id.addSituacaoTitulo)
        val botaoClose: ImageView = root.findViewById(R.id.closeBTN)

        //VER SE É ADD OU EDIT
        if (situacaoEditar == null) {
            tituloGeral.text = getString(R.string.add_situacao)
        } else {
            Picasso.get()
                .load(situacaoEditar!!.foto)
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_image_not_supported_24)
                .into(foto)
            tituloGeral.text = getString(R.string.edit_situacao)
            titulo.text = situacaoEditar!!.titulo
            tipo.setSelection(
                resources.getStringArray(R.array.tiposSituacao).indexOf(situacaoEditar!!.tipo)
            )
            conteudo.text = situacaoEditar!!.descricao
            botaoClose.visibility = View.VISIBLE
            botaoClose.setOnClickListener {
                val fragment: HomeFragment = HomeFragment()
                val transaction: FragmentTransaction =
                    this.requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment, fragment).commit()
            }
        }

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

        val btnAddSituacao: Button = root.findViewById(R.id.btnAddSituacao)
        btnAddSituacao.setOnClickListener {
            var formTitulo = titulo.text.toString()
            var formTipo = tipo.selectedItemId.toInt()
            var formConteudo = conteudo.text.toString()
            if (formTitulo == "" || formTipo == 0 || formConteudo == "" || (!this::fotoPick.isInitialized && situacaoEditar == null)) {
                SweetAlertDialog(this.activity)
                    .setTitleText(getString(R.string.empty))
                    .setConfirmText("Ok")
                    .show()
            } else {
                println(formTitulo + " - " + formTipo + " - " + formConteudo)

                val sharedPref: SharedPreferences = (this.activity as MainActivity).sharedPref
                val userLogado = sharedPref.getString(getString(R.string.user), "")

                val requireView = this.requireView()
                val actividade = this.requireActivity()

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
                            val call = request.editSituacao(
                                situacaoEditar!!.id.toInt(),
                                formTitulo,
                                formConteudo,
                                formTipo.toString()
                            )
                            if (situacaoEditar == null) {
                                val call = request.addSituacao(
                                    formTitulo,
                                    formConteudo,
                                    formTipo.toString(),
                                    fotoPick,
                                    userLogado,
                                    location.latitude.toString(),
                                    location.longitude.toString()
                                )
                            }

                            call.enqueue(object : Callback<OutputGeral> {

                                override fun onResponse(
                                    call: Call<OutputGeral>,
                                    response: Response<OutputGeral>
                                ) {
                                    if (response.isSuccessful && situacaoEditar == null) {
                                        Navigation.findNavController(requireView)
                                            .navigate(R.id.navigation_home);
                                    }else if(response.isSuccessful && situacaoEditar != null){
                                        val fragment: HomeFragment = HomeFragment()
                                        val transaction: FragmentTransaction =
                                            actividade.supportFragmentManager.beginTransaction()
                                        transaction.replace(R.id.nav_host_fragment, fragment).commit()
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