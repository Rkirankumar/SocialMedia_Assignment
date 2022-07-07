package com.socialmediaassignment.ui



import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.socialmediaassignment.R
import com.socialmediaassignment.model.UserInfo


/**
 * Created on : July 05, 2022
 * Author     : Kiran
 */
@Suppress("DEPRECATION")
class SecondFragment : Fragment() {

    var firebaseDatabase: FirebaseDatabase? = null
    // creating a variable for our Database
    // Reference for Firebase.
    var databaseReference: DatabaseReference? = null
    // creating a variable for
    // our object class
    var userInfo: UserInfo? = null
    // creating variables for
    // EditText and buttons.
    private var eNameEdt: EditText? = null
    private var eTitleEdt: EditText? = null
    // creating variables for
    // EditText and buttons.
    var progressDialog: ProgressDialog? = null

    private var videouri: Uri? = null
    private val REQUEST_CODE = 101
    var autoinc: String? = null
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initializing our edittext and button
        eNameEdt =  view.findViewById<EditText>(R.id.idEdtName)
        eTitleEdt = view.findViewById<EditText>(R.id.idEdtTitle)
        progressDialog= ProgressDialog(activity)


        // below line is used to get the
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance()
        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase!!.getReference("UserInfo")

        // initializing our object
        // class variable.
        userInfo = UserInfo()

        view.findViewById<Button>(R.id.record).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(intent, REQUEST_CODE)
        }
        view.findViewById<Button>(R.id.upload).setOnClickListener {
            val name = view.findViewById<EditText>(R.id.idEdtName).getText().toString()
            val title = view.findViewById<EditText>(R.id.idEdtTitle).getText().toString()
            progressDialog!!.setTitle("Uploading...");
            progressDialog!!.show();
            try {
            if (videouri != null) {
               // save the selected video in Firebase storage
                val reference = FirebaseStorage.getInstance().getReference("Files/" + System.currentTimeMillis() + "." + getfiletype(videouri!!))
                reference.putFile(videouri!!).addOnSuccessListener { taskSnapshot ->
                    autoinc = databaseReference!!.push().getKey()
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    // get the link of video
                    val downloadUri = uriTask.result.toString()
                    val reference1 = FirebaseDatabase.getInstance().getReference("UserInfo")
                    val map: HashMap<String, String> = HashMap()
                    map["videolink"] = downloadUri
                    userInfo!!.id = autoinc
                    userInfo!!.userTitle = title
                    userInfo!!.url = downloadUri
                    userInfo!!.coverUrl = "https://cdn.dribbble.com/users/411475/screenshots/13751544/media/8ec68093dddd294f4411bab4cfd66b21.png"
                    userInfo!!.name = name
                    userInfo!!.like = "0"
                    reference1.child(autoinc!!).setValue(userInfo)
                    // Video uploaded successfully
                    // Dismiss dialog
                    progressDialog!!.dismiss();
                    Toast.makeText(activity, "Video Uploaded!!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                }.addOnFailureListener { e -> // Error, Image not uploaded
                    progressDialog!!.dismiss();
                    Toast.makeText(activity, "Failed " + e.message, Toast.LENGTH_SHORT).show()
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
    }
    // Receiver
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        videouri = data!!.getData();
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
            //    Toast.makeText(activity, """Video saved to:$videouri""".trimIndent(), Toast.LENGTH_LONG).show()
            } else if (requestCode == RESULT_CANCELED) {
                Toast.makeText(activity, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity, "Failed to record video",
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getfiletype(videouri: Uri): String? {
        val r: ContentResolver = requireActivity().applicationContext.contentResolver
        // get the file type ,in this case its mp4
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri))
    }
}