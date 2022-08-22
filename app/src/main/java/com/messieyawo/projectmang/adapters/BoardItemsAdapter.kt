package com.messieyawo.projectmang.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.messieyawo.projectmang.R
import com.messieyawo.projectmang.models.Board

class BoardItemsAdapter(
    private val context: Context,
    private var list:ArrayList<Board>
    ):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener:OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rootview = LayoutInflater.from(context)
            .inflate(R.layout.item_board,parent,false)
        return myViewHolder(rootview)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

          if (holder is myViewHolder){
              Glide
                  .with(context)
                  .load(model.image)
                  .centerCrop()
                  .placeholder(R.drawable.ic_launcher_background)
                  .into(holder.iv_borad)

              holder.tv_board_name.text = model.name
              holder.tv_created_by.text = "Created By ${model.createdBy}"

              holder.itemView.setOnClickListener {
                  if (onClickListener != null){
                      onClickListener!!.onClick(position,model)
                  }
              }
          }

    }

    interface OnClickListener {
        fun onClick(position:Int,model:Board)
    }


    override fun getItemCount(): Int {
       return list.size
    }

    class myViewHolder(view: View):RecyclerView.ViewHolder(view){
        val tv_board_name:TextView = view.findViewById(R.id.tv_name)
        val tv_created_by:TextView = view.findViewById(R.id.tv_created_by)

        val iv_borad:ImageView = view.findViewById(R.id.iv_board_adapter)
    }
}
