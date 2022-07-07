package com.socialmediaassignment.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.socialmediaassignment.R
import com.socialmediaassignment.model.UserInfo
import com.socialmediaassignment.ui.Comments
import com.socialmediaassignment.ui.SecondFragment
import java.lang.reflect.Array.newInstance


/**
 * Created on : July 05, 2022
 * Author     : Kiran
 */
class PlayerViewHolder(private val parent: View) :
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
    var like: ImageView
    var share: ImageView
    var comment: ImageView

    @JvmField
    var progressBar: ProgressBar
    @JvmField
    var requestManager: RequestManager? = null
    private val usertitle: TextView
    private val name: TextView
        var dbref : DatabaseReference? = null

    fun onBind(
        mediaObject: UserInfo,
        requestManager: RequestManager?,
        mContext: Context
    ) {
        this.requestManager = requestManager
        parent.tag = this
        usertitle.text = mediaObject.userTitle
        name.text = mediaObject.name
        dbref = FirebaseDatabase.getInstance().getReference("UserInfo")
        if(mediaObject.like=="0"){
            like.setImageResource(R.drawable.ic_unlike)
        }else if(mediaObject.like=="1"){
            like.setImageResource(R.drawable.ic_like)
        }
        like.setOnClickListener {
            try {
            if (mediaObject.like=="0") {
                like.setImageResource(R.drawable.ic_like)
                dbref!!.child(mediaObject.id!!).child("like").setValue("1")
            } else  if(mediaObject.like=="1"){
                like.setImageResource(R.drawable.ic_unlike)
                dbref!!.child(mediaObject.id!!).child("like").setValue("0")
            }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        share.setOnClickListener { // Construct a ShareIntent with link to image
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Application Name")
                var shareMessage =
                    "\nWe can Provide message here\".\n\n"
                shareMessage =
                    shareMessage + "https://play.google.com/" + "\n\n"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                mContext.startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        comment.setOnClickListener { // Construct a ShareIntent with link to image
            try {
                val intent = Intent(mContext,Comments::class.java)
                intent.putExtra("ID",mediaObject.id!!)
                mContext.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        this.requestManager
            ?.load(mediaObject.coverUrl)
            ?.into(mediaCoverImage)
    }

    init {
        mediaContainer = parent.findViewById(R.id.mediaContainer)
        mediaCoverImage = parent.findViewById(R.id.ivMediaCoverImage)
        usertitle = parent.findViewById(R.id.tvUserTitle)
        name = parent.findViewById(R.id.tvName)
        progressBar = parent.findViewById(R.id.progressBar)
        volumeControl = parent.findViewById(R.id.ivVolumeControl)
        like = parent.findViewById(R.id.like)
        share = parent.findViewById(R.id.share)
        comment = parent.findViewById(R.id.comment)
    }

}