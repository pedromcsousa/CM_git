package ipvc.estg.cityhelp.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import cn.pedant.SweetAlert.SweetAlertDialog
import ipvc.estg.cityhelp.R
import ipvc.estg.cityhelp.ui.home.HomeFragment


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
        val titulo : TextView = root.findViewById(R.id.textTituloSituacao)
        val tipo : Spinner = root.findViewById(R.id.spinnerTipoSituacao)
        val conteudo : TextView = root.findViewById(R.id.textDescSituacao)

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

        val btnAddSituacao : Button = root.findViewById(R.id.btnAddSituacao)
        btnAddSituacao.setOnClickListener{
            var formTitulo = titulo.text.toString()
            var formTipo = tipo.selectedItemId.toInt()
            var formConteudo = conteudo.text.toString()
            if(formTitulo == "" || formTipo == 0 || formConteudo == "") {
                SweetAlertDialog(this.activity)
                    .setTitleText(getString(R.string.empty))
                    .setConfirmText("Ok")
                    .show()
            }else {
                println(formTitulo + " - " + formTipo + " - " + formConteudo)
                Navigation.findNavController(this.requireView()).navigate(R.id.navigation_home);
            }
        }

        return root
    }
}