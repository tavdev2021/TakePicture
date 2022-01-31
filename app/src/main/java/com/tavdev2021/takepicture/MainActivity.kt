package com.tavdev2021.takepicture

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.content.ContextCompat
import com.tavdev2021.takepicture.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private val responseLauncher = registerForActivityResult(StartActivityForResult()){ imageBitmap ->
        val bitmap = imageBitmap?.data?.extras?.get("data") as Bitmap
        binding.shapeableImageView.setImageBitmap(bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()
        abrirGaleria()
    }

    private fun setupListener() {
        binding.btnTakePhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun abrirGaleria(){
        binding.btnAbrirGaleria.setOnClickListener {
            requestPermissiongallery()
        }
    }

    private fun requestPermissiongallery() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        pickPhotoFromGallery()
                    }

                else-> requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                pickPhotoFromGallery()
            }
        }

    private val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ){ isGranted ->
        if (isGranted){
            pickPhotoFromGallery()
        }else{
           Toast.makeText(this,"Necesita permitir el acceso a la galeria",Toast.LENGTH_SHORT).show()
        }

    }

    private val startForActivityGallery = registerForActivityResult(
        StartActivityForResult()
    ){ result->
      if (result.resultCode == Activity.RESULT_OK){
          val data = result.data?.data
          binding.shapeableImageView.setImageURI(data)
      }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityGallery.launch(intent)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePicture ->
            takePicture.resolveActivity(packageManager)?.also {
                // startActivityForResult(takePicture, REQUEST_CODE_TAKE_PHOTO)
                responseLauncher.launch(takePicture)
            }
        }
    }

    //override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)

        //if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            //data?.extras?.let { bundle -> {

                    //val imageBitmap = bundle.get("data") as Bitmap
                    //binding.shapeableImageView.setImageBitmap(null)
                    //binding.shapeableImageView.setImageBitmap(imageBitmap)
                //} }
        //}else{
            //Toast.makeText(this,"No se pudo guardar la imagen",Toast.LENGTH_SHORT).show()
        //}
    //}
}