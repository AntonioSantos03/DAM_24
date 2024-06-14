import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.confconnect.ArticleDetails
import com.example.confconnect.Articles
import com.example.confconnect.ShowMap
import com.example.confconnect.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var articlesList: ArrayList<Articles>
    private lateinit var rvArticlesAdapter: RvArticlesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        articlesList = ArrayList()
        rvArticlesAdapter = RvArticlesAdapter(articlesList)

        binding.rvArticles.layoutManager = LinearLayoutManager(context)
        binding.rvArticles.adapter = rvArticlesAdapter

        rvArticlesAdapter.setOnItemClickListener { article ->
            val intent = Intent(activity, ArticleDetails::class.java).apply {
                putExtra("articleId", article.articleId)
                putExtra("title", article.title)
                putExtra("author", article.author)
                putExtra("date", article.date)
                putExtra("description", article.description)
            }
            startActivity(intent)
        }

        binding.btnOpenMapActivity.setOnClickListener {
            val intent = Intent(activity, ShowMap::class.java)
            startActivity(intent)
        }

        fetchArticlesData()
    }

    private fun fetchArticlesData() {
        val database = FirebaseDatabase.getInstance().getReference("Articles")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                articlesList.clear()
                Log.d("HomeFragment", "Snapshot count: ${snapshot.childrenCount}")
                for (dataSnapshot in snapshot.children) {
                    val article = dataSnapshot.getValue(Articles::class.java)
                    if (article != null) {
                        articlesList.add(article)
                        Log.d("HomeFragment", "Article added: ${article.title}")
                    } else {
                        Log.d("HomeFragment", "Article is null for snapshot: ${dataSnapshot.key}")
                    }
                }
                rvArticlesAdapter.notifyDataSetChanged()
                Log.d("HomeFragment", "Total articles: ${articlesList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load articles", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
