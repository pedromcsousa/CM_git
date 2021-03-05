package ipvc.estg.cityhelp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.cityhelp.R
import ipvc.estg.cityhelp.data.Nota

class NotaAdapter(val list: ArrayList<Nota>) : RecyclerView.Adapter<LineViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recycle_line_nota, parent, false)
        return LineViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val current = list[position]

        holder.titulo.text = current.titulo
        holder.conteudo.text = current.conteudo
    }
}

class LineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titulo = itemView.findViewById<TextView>(R.id.titulo)
    val conteudo = itemView.findViewById<TextView>(R.id.conteudo)
}