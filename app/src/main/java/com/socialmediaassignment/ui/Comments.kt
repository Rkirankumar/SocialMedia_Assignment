package com.socialmediaassignment.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.socialmediaassignment.R
import com.socialmediaassignment.adapter.CommentsRecyclerAdapter
import com.socialmediaassignment.model.CommentInfo
import com.socialmediaassignment.utils.ExoCommentPlayerRecyclerView

@Suppress("DEPRECATION")
class Comments : AppCompatActivity() {
    private val REQUEST_CODE = 101
    private var videouri: Uri? = null
    private var progressDialog: ProgressDialog? = null
    private var dbref : DatabaseReference? = null
    private var commentInfo: CommentInfo? = null
    private var mRecyclerView: ExoCommentPlayerRecyclerView? = null
    private lateinit var userArrayList : ArrayList<CommentInfo>
    private var mAdapter: CommentsRecyclerAdapter? = null
    private var userID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        progressDialog= ProgressDialog(this)
         userID=intent.getStringExtra("ID")
        val eName = findViewById<EditText>(R.id.idName)
        val btn_Record = findViewById<Button>(R.id.record_comm)
        val btn_Upload = findViewById<Button>(R.id.upload_comm)
        mRecyclerView = findViewById<ExoCommentPlayerRecyclerView>(R.id.exocommentsPlayerRecyclerView)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        userArrayList = arrayListOf<CommentInfo>()

        dbref = FirebaseDatabase.getInstance().getReference("UserInfo")
        commentInfo = CommentInfo()
        btn_Record.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(intent, REQUEST_CODE)
            }
        btn_Upload.setOnClickListener {

            val name = eName.getText().toString()
            progressDialog!!.setTitle("Uploading...");
            progressDialog!!.show();
            try {
                if (videouri != null) {
                    // save the selected video in Firebase storage
                    val reference = FirebaseStorage.getInstance().getReference("Files/" + System.currentTimeMillis() + "." + getfiletype(videouri!!))
                    reference.putFile(videouri!!).addOnSuccessListener { taskSnapshot ->
                        val uriTask = taskSnapshot.storage.downloadUrl
                        while (!uriTask.isSuccessful);
                        // get the link of video
                        val downloadUri = uriTask.result.toString()
                        commentInfo!!.name = name
                        commentInfo!!.url = downloadUri
                        commentInfo!!.coverUrl = "https://cdn.dribbble.com/users/411475/screenshots/13751544/media/8ec68093dddd294f4411bab4cfd66b21.png"

                        dbref!!.child(userID!!).setValue(commentInfo)
                       // Video uploaded successfully
                        // Dismiss dialog
                        progressDialog!!.dismiss();
                        Toast.makeText(this, "Video Uploaded!!", Toast.LENGTH_SHORT).show()
                     }.addOnFailureListener { e -> // Error, Image not uploaded
                        progressDialog!!.dismiss();
                        Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                    }.addOnProgressListener { taskSnapshot ->
                        // Progress Listener for loading
                        // percentage on the dialog box
                        // show the progress bar
                        val progress =
                            100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                        progressDialog!!.setMessage("Uploaded " + progress.toInt() + "%")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            }
        getUserData()

    }
    private fun getUserData() {

        dbref = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(userID!!).child("List")
        //userArrayListVideo
        dbref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userArrayList.clear()
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(CommentInfo::class.java)
                        userArrayList.add(user!!)
                    }

                    //set data object
                    mRecyclerView!!.setCommentsObjects(userArrayList)
                    mAdapter = initGlide()?.let { CommentsRecyclerAdapter(userArrayList, it,applicationContext) }

                    //Set Adapter

                    //Set Adapter
                    mRecyclerView!!.adapter = mAdapter
                    mRecyclerView!!.smoothScrollToPosition(1)

                }else{
                    Toast.makeText(applicationContext, "Empty", Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Erro", Toast.LENGTH_LONG).show()
            }
        })
    }
    // Receiver
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        videouri = data!!.getData();
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //    Toast.makeText(activity, """Video saved to:$videouri""".trimIndent(), Toast.LENGTH_LONG).show()
            } else if (requestCode == Activity.RESULT_CANCELED) {
                Toast.makeText(applicationContext, "Video recording cancelled.",
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Failed to record video",
                    Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun getfiletype(videouri: Uri): String? {
        val r: ContentResolver = this.applicationContext.contentResolver
        // get the file type ,in this case its mp4
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri))
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
    override fun onPause() {
        if (mRecyclerView != null) {
            mRecyclerView!!.releasePlayer()
        }
        super.onPause()
    }
}