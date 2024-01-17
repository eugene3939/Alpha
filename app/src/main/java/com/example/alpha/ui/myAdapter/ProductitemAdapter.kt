import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.alpha.R
import com.example.alpha.ui.myObject.ProductItem

class ProductitemAdapter(private val dataList: List<ProductItem>, private val selectedPositions: Set<Int>) : BaseAdapter() {
    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.product_items, parent, false)
        val viewHolder = ViewHolder(view)
        val data = getItem(position) as ProductItem
        viewHolder.bind(data)

        // 檢查該位置是否被選擇，並設置相應的背景顏色
        if (selectedPositions.contains(position)) {
            // 被選擇時的背景顏色
            view.setBackgroundResource(R.color.purple_200)
        } else {
            // 正常狀態的背景顏色
            view.setBackgroundResource(R.color.white)
        }

        return view
    }

    class ViewHolder(itemView: View) {
        private val productImg: ImageView = itemView.findViewById(R.id.img_productItem)
        private val productName: TextView = itemView.findViewById(R.id.txt_productName)
        private val productType: TextView = itemView.findViewById(R.id.txt_productType)
        private val productPrice: TextView = itemView.findViewById(R.id.txt_productPrice)
        private val productNumber: TextView = itemView.findViewById(R.id.txt_productNumber)

        fun bind(product: ProductItem) {
            // 使用 Glide 或其他圖片載入庫載入商品圖片
            Glide.with(productImg.context)
                .load(product.imageUrl)
                .into(productImg)

            productName.text = product.pName
            productType.text = product.pType
            productPrice.text = product.pPrice.toString()
            productNumber.text = product.pNumber.toString()
        }
    }
}
