package fr.epita.hellogames

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_second.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/* Second activity class */

class SecondActivity : AppCompatActivity(), View.OnClickListener {

    var gameDetails:GameDetails? = null

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.moreButton -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(gameDetails!!.url)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        moreButton.setOnClickListener(this@SecondActivity)
        val baseURL = "https://androidlessonsapi.herokuapp.com/api/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
            .build()
        val service: WebServiceInterface = retrofit.create(WebServiceInterface::class.java)

        val callback = object : Callback<GameDetails> {
            override fun onFailure(call: Call<GameDetails>?, t: Throwable?) {
                Log.d("TAG", "WebService call failed")
            }
            override fun onResponse(call: Call<GameDetails>?,
                                    response: Response<GameDetails>?) {
                if (response != null) {
                    if (response.code() == 200) {
                        val responseData = response.body()
                        if (responseData != null) {
                            setInfo(responseData)
                            gameDetails = responseData
                        }
                    }
                }
            }
        }
        val intent = intent
        val CODE_GAMEID = "CODE_ID"
        val CODE_IMAGEID = "CODE_IMAGEID"
        val codeId = intent.getIntExtra(CODE_GAMEID, 0)
        val imageId = intent.getIntExtra(CODE_IMAGEID, 0)

        service.listofGameDetails(codeId).enqueue(callback)
        findViewById<ImageView>(R.id.gameSelected).setImageResource(imageId)
    }

    fun setInfo(gameDetails: GameDetails) {
        findViewById<TextView>(R.id.nameCustom).setText(gameDetails.name)
        findViewById<TextView>(R.id.playerNbCustom).setText(gameDetails.players.toString())
        findViewById<TextView>(R.id.typeCustom).setText(gameDetails.type)
        findViewById<TextView>(R.id.descriptionCustom).setText(gameDetails.description_en)
        findViewById<TextView>(R.id.yearCustom).setText(gameDetails.year.toString())
    }
}