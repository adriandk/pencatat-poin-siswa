package com.catatpelanggaran.orangtua

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.adriandery.catatpelanggaran.LoginActivity
import com.adriandery.catatpelanggaran.SharedPreferences
import com.catatpelanggaran.orangtua.dashboard.DashboardFragment
import com.catatpelanggaran.orangtua.profile.EditFragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_ortu.*

class OrtuActivity : AppCompatActivity() {

    lateinit var nis: String
    lateinit var popUpMenu: Dialog
    lateinit var cancelButton: MaterialButton
    lateinit var exitButtton: MaterialButton

    private val homeFrag = DashboardFragment()
    private val profileFrag = EditFragment()
    private val fragmentManager = supportFragmentManager
    var fragment: Fragment = homeFrag
    var fragmentName = "home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ortu)
        setSupportActionBar(toolbar_ortu)

        popUpMenu = Dialog(this)

        nis = intent.getStringExtra("NIS").toString()
        getData(nis)

        fragmentManager.beginTransaction().add(R.id.frame_layout_ortu, profileFrag, "2")
            .hide(profileFrag).commit()
        fragmentManager.beginTransaction().add(R.id.frame_layout_ortu, homeFrag, "1").commit()

        ortu_bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    fragmentName = "home"
                    fragmentManager.beginTransaction().hide(fragment).show(homeFrag).commit()
                    fragment = homeFrag
                    setting_ortu.visibility = View.GONE
                }
                R.id.profile -> {
                    fragmentName = "profile"
                    fragmentManager.beginTransaction().hide(fragment).show(profileFrag).commit()
                    fragment = profileFrag
                    setting_ortu.visibility = View.VISIBLE
                    setting_ortu.setOnClickListener { createPopUpExit() }
                }
            }
            true
        }

    }

    private fun createPopUpExit() {
        popUpMenu.setContentView(R.layout.popup_ortu)
        cancelButton = popUpMenu.findViewById(R.id.cancel_ortu)
        exitButtton = popUpMenu.findViewById(R.id.exit_ortu)

        cancelButton.setOnClickListener {
            popUpMenu.dismiss()
        }
        exitButtton.setOnClickListener {
            popUpMenu.dismiss()
            val intent = Intent(this@OrtuActivity, LoginActivity::class.java)
            startActivity(intent)
            SharedPreferences.clearData(this@OrtuActivity)
            finish()
        }

        popUpMenu.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popUpMenu.show()

    }

    private fun getData(nis: String) {
        val database = FirebaseDatabase.getInstance().reference

        database.child("Orang_Tua").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OrtuActivity, "Error", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(nis).exists()) {
                    val nama = snapshot.child(nis).child("nama").value.toString()
                    nama_orangtua.text = nama
                } else {
                    val intent = Intent(this@OrtuActivity, LoginActivity::class.java)
                    startActivity(intent)
                    SharedPreferences.clearData(this@OrtuActivity)
                    finish()
                }
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}