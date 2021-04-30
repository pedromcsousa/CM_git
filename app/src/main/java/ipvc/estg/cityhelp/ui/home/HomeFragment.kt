package ipvc.estg.cityhelp.ui.home

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import ipvc.estg.cityhelp.MainActivity
import ipvc.estg.cityhelp.R


class HomeFragment : Fragment(), SensorEventListener {

    //SENSORES
    private lateinit var sensorManager: SensorManager
    private var temperatura: Sensor? = null

    private lateinit var tempTXT : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val atividade = (this.requireActivity() as MainActivity)

        atividade.mapa()

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        //SENSORES
        tempTXT = root.findViewById(R.id.temperatura)
        sensorManager = atividade.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperatura = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        //SPINNER - CARREGAR TIPOS
        val tipo: Spinner = root.findViewById(R.id.spinnerTipoSituacao)
        ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.tiposSituacao,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tipo.adapter = adapter
        }

        tipo.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0)
                    atividade.filtroTipo = null
                else
                    atividade.filtroTipo = resources.getStringArray(R.array.tiposSituacao)[position]
                atividade.mapa()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                atividade.filtroTipo = null
            }
        })

        //DISTANCIA
        val distanciaFiltro: TextView = root.findViewById(R.id.distanciaFiltro)
        distanciaFiltro.text = atividade.filtroDist.toString() + "km"

        val sliderFilter: Slider = root.findViewById(R.id.sliderFiltro)
        sliderFilter.addOnChangeListener { rangeSlider, value, fromUser ->
            distanciaFiltro.text = value.toString() + "km"
            atividade.filtroDist = value
        }
        sliderFilter.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                //NADA
            }

            override fun onStopTrackingTouch(slider: Slider) {
                atividade.mapa()
            }
        })

        return root
    }

    override fun onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, temperatura, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //dddd
    }

    override fun onSensorChanged(event: SensorEvent) {
        if(event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE){
            var temperaturaAtual = event.values[0];
            tempTXT.text = temperaturaAtual.toInt().toString() + "ºC"
            if(temperaturaAtual < 5){
                tempTXT.setTextColor(Color.parseColor("#0000FF"))
            }else if(temperaturaAtual < 20){
                tempTXT.setTextColor(Color.parseColor("#00FF00"))
            }else{
                tempTXT.setTextColor(Color.parseColor("#FF0000"))
            }
        }
    }
}