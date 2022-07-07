package com.socialmediaassignment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.socialmediaassignment.R
import com.socialmediaassignment.model.CommentInfo
import java.util.*

/**
 * Created on : July 05, 2022
 * Author     : Kiran
 */
class CommentsRecyclerAdapter(
    private val mediaObjects: ArrayList<CommentInfo>,
    private val requestManager: RequestManager, private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return CommentsPlayerViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.layout_comments_list_item, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        (viewHolder as CommentsPlayerViewHolder).onBind(mediaObjects[i], requestManager, context)
    }

    override fun getItemCount(): Int {
        return mediaObjects.size
    }

}