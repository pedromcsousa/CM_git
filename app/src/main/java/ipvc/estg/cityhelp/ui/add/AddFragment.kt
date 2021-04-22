package ipvc.estg.cityhelp.ui.add

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddFragment : Fragment() {

    private lateinit var dashboardViewModel: AddViewModel

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

        //ELEMENTOS FORMULÁRIO
        val titulo: TextView = root.findViewById(R.id.textTituloSituacao)
        val tipo: Spinner = root.findViewById(R.id.spinnerTipoSituacao)
        val conteudo: TextView = root.findViewById(R.id.textDescSituacao)

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
            if (formTitulo == "" || formTipo == 0 || formConteudo == "") {
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
                                "",
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