package com.example.chattingzone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ViewFullImageActivity : AppCompatActivity() {
    private var imageView:ImageView?=null
    private var imageUrl:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_image)
        imageView=findViewById(R.id.fullImage)
        imageUrl=intent.getStringExtra("url")

        Picasso.get().load(imageUrl).into(imageView)
    }
}