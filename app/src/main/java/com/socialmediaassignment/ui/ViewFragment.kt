package com.socialmediaassignment.ui




import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.socialmediaassignment.R
import com.socialmediaassignment.adapter.MediaRecyclerAdapter
import com.socialmediaassignment.model.UserInfo
import com.socialmediaassignment.utils.ExoPlayerRecyclerView


/**
 * Created on : July 05, 2022
 * Author     : Kiran
 */
class ViewFragment : Fragment() {


    var mRecyclerView: ExoPlayerRecyclerView? = null
    private lateinit var userArrayList : ArrayList<UserInfo>
    private var mAdapter: MediaRecyclerAdapter? = null
    private lateinit var dbref : DatabaseReference

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.findViewById<ExoPlayerRecyclerView>(R.id.exoPlayerRecyclerView)
        mRecyclerView!!.layoutManager = LinearLayoutManager(activity)
        userArrayList = arrayListOf<UserInfo>()

        getUserData()
        //set data object
   }


    private fun initGlide(): RequestManager? {
        val options = RequestOptions()
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

     override fun onDestroy() {
        if (mRecyclerView != null) {
            mRecyclerView!!.releasePlayer()
        }
        super.onDestroy()
    }
    private fun getUserData() {

        dbref = FirebaseDatabase.getInstance().getReference().child("UserInfo")
        //userArrayListVideo
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userArrayList.clear()
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(UserInfo::class.java)
                        userArrayList.add(user!!)
                    }
                    //set data object
                    mRecyclerView!!.setMediaObjects(userArrayList)
                    mAdapter = initGlide()?.let { activity?.let { it1 ->
                        MediaRecyclerAdapter(userArrayList, it,
                            it1
                        )
                    } }
                    mRecyclerView!!.adapter = mAdapter
                        mRecyclerView!!.smoothScrollToPosition(1)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    override fun onResume() {
        if (mRecyclerView != null) {
            mRecyclerView!!.releasePlayer()
        }
        super.onResume()
    }
    override fun onPause() {
        if (mRecyclerView != null) {
            mRecyclerView!!.releasePlayer()
        }
        super.onPause()
    }
}