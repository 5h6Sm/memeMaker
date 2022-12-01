package com.example.mememaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.recyclerview.widget.ListAdapter as ListAdapter


/*
@JsonDeserialize(using=ComplexJSONDataDeserializer::class)

//data class MemeData(val imageURL: String, val explanation: String)

data class Items(
    @SerializedName("url") var explanation: String = "",
    @SerializedName("explation") var imageURL: String = ""
) {}
data class ResultGetSearchNews (
    @SerializedName("items") var items: List<Items>
){}

data class ComplexJSONData(
    val imgURL: String
)

class ComplexJSONDataDeserializer  : StdDeserializer<ComplexJSONData>(
    ComplexJSONData::class.java
){
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): ComplexJSONData {
        val node = p?.codec?.readTree<JsonNode>(p)

        val nestedNode = node?.get("data")
        val innerDataValue = nestedNode?.get("memes")?.asText()

        // TODO : data1, data2 가져오기
        val innerNestedNode = nestedNode?.get("inner_nested")

        val list = mutableListOf<Int>()
        innerNestedNode?.get("list")?.elements()?.forEach {
            list.add(it.asInt())
        }

        return ComplexJSONData(
            innerDataValue!!
        )
    }
}
*/

class MemeListAdapter(var memeData: List<MemeData>) : RecyclerView.Adapter<MemeListAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.view.findViewById<TextView>(R.id.explanation).text = memeData[position].explation
        val imageView = holder.view.findViewById<ImageView>(R.id.meme_img)
        Glide.with(holder.view.context).load(memeData[position].url).into(imageView)

        val likes_btn = holder.view.findViewById<Button>(R.id.likes_btn)
        var likes_count = memeData[position].likes

        holder.view.setOnClickListener{
            val intent = Intent(holder.view.context, inputTextActivity::class.java)

            intent.putExtra("img", memeData[holder.adapterPosition].url)
            holder.view.context.startActivity(intent)
        }

        likes_btn.setOnClickListener {
            likes_count++
            memeData[holder.adapterPosition].likes = likes_count
            // Log.d("mytag", memeData[position].likes.toString())

            // Log.d("mytag", "before sort " + memeData.toString())
            memeData = memeData.sortedByDescending {
                it.likes
            }
            // Log.d("mytag", "after sort " + memeData.toString())

            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return memeData.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.meme_cell
    }
}



class SelectMemeActivity : AppCompatActivity() {

//    var memeDataList = listOf<MemeData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_meme)

        val retrofit: Retrofit = Retrofit.Builder().baseUrl("http://10.96.120.129:9000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(Api::class.java)
        api.getMemeData().enqueue(object: Callback<List<MemeData>> {
            override fun onResponse(
                call: Call<List<MemeData>>,
                response: Response<List<MemeData>>
            ) {
                var data = response.body()!!

                Log.d("mytag", response.toString())

                val memeList = findViewById<RecyclerView>(R.id.meme_select_list)
                memeList.setHasFixedSize(false)
                memeList.layoutManager = LinearLayoutManager(this@SelectMemeActivity)
                memeList.adapter = MemeListAdapter(data)

                Log.d("mytag", data.toString())
            }
            override fun onFailure(call: Call<List<MemeData>>, t: Throwable) {
                Log.d("mytag", t.message.toString())
            }
        })



        /*
        var mapper = jacksonObjectMapper()



        val memeList = findViewById<RecyclerView>(R.id.meme_select_list)
        memeList.setHasFixedSize(false)
        memeList.layoutManager = LinearLayoutManager(this)
        memeList.adapter = MemeListAdapter(memeDatas)
        */

    }
}