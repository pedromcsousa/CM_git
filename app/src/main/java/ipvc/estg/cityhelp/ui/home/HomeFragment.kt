package ipvc.estg.cityhelp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ipvc.estg.cityhelp.MainActivity
import ipvc.estg.cityhelp.R


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (this.requireActivity() as MainActivity).mapa()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}