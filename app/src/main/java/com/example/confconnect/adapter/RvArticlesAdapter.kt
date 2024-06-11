import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.confconnect.Articles
import com.example.confconnect.databinding.RvArticlesBinding

class RvArticlesAdapter(private val articlesList: ArrayList<Articles>) :
    RecyclerView.Adapter<RvArticlesAdapter.ViewHolder>() {

    private var onItemClicked: (Articles) -> Unit = {}

    inner class ViewHolder(val binding: RvArticlesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvArticlesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return articlesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = articlesList[position]
        holder.apply {
            binding.apply {
                tvTitle.text = currentItem.title
                tvAuthor.text = currentItem.author
                tvDate.text = currentItem.date
                tvDescription.text = currentItem.description

                root.setOnClickListener {
                    onItemClicked(currentItem)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (Articles) -> Unit) {
        onItemClicked = listener
    }
}
