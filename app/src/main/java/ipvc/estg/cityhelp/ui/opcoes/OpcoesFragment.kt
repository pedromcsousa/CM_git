package ipvc.estg.cityhelp.ui.opcoes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ipvc.estg.cityhelp.R

class OpcoesFragment : Fragment() {

    private lateinit var notificationsViewModel: OpcoesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(OpcoesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_opcoes, container, false)
        return root
    }
}