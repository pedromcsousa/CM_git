package ipvc.estg.cityhelp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.cityhelp.ClickListenerNota
import ipvc.estg.cityhelp.R
import ipvc.estg.cityhelp.entities.Nota

class NotaAdapter internal constructor(
    context: Context,
    private val clickListenerNota: ClickListenerNota
) : RecyclerView.Adapter<NotaAdapter.NotaViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notas = emptyList<Nota>()

    class NotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val titulo: TextView = itemView.findViewById(R.id.titulo)
        val conteudo: TextView = itemView.findViewById(R.id.conteudo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val itemView = inflater.inflate(R.layout.recycle_line_nota, parent, false)
        return NotaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val current = notas[position]
        holder.titulo.text = current.titulo
        holder.conteudo.text = current.conteudo

        holder?.itemView?.setOnClickListener{ clickListenerNota.clickListenerNota(current) }
        holder?.itemView?.setOnLongClickListener(object: View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                clickListenerNota.longClickListenerNota(current)
                return true;
            }
        })

    }

    internal fun setNotas(notas: List<Nota>){
        this.notas = notas
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return notas.size
    }

}