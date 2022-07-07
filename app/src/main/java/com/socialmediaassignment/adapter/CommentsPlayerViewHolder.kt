package com.socialmediaassignment.adapter

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.socialmediaassignment.R
import com.socialmediaassignment.model.CommentInfo


/**
 * Created on : July 05, 2022f
 * Author     : Kiran
 */
class CommentsPlayerViewHolder(private val parent: View) :
    RecyclerView.ViewHolder(parent) {
    /**
     * below view have public modifier because
     * we have access PlayerViewHolder inside the ExoPlayerRecyclerView
     */
    @JvmField
    var mediaContainer: FrameLayout
    @JvmField
    var mediaCoverImage: ImageView
    @JvmField
    var volumeControl: ImageView
    @JvmField
    var progressBar: ProgressBar
    @JvmField
    var requestManager: RequestManager? = null
    val comments: TextView

    fun onBind(
        mediaObject: CommentInfo,
        requestManager: RequestManager?,
        mContext: Context
    ) {
        this.requestManager = requestManager
        parent.tag = this
        comments.text = mediaObject.name
        this.requestManager
            ?.load(mediaObject.coverUrl)
            ?.into(mediaCoverImage)
    }

    init {
        mediaContainer = parent.findViewById(R.id.commentmediaContainer)
        mediaCoverImage = parent.findViewById(R.id.ivcommentMediaCoverImage)
        comments = parent.findViewById(R.id.tvComments)
        progressBar = parent.findViewById(R.id.commentprogressBar)
        volumeControl = parent.findViewById(R.id.ivcommentVolumeControl)
      }

}