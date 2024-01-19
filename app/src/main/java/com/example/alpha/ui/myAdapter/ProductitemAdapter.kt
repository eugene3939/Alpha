import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.alpha.R
import com.example.alpha.ui.myObject.ProductItem
import com.example.alpha.ui.myObject.ShopCart

class ProductItemAdapter(private val dataList: List<ProductItem>, private val shoppingCart: ShopCart) : BaseAdapter() {

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.product_items, parent, false)
        val viewHolder = ViewHolder(view)
        val data = getItem(position) as ProductItem
        viewHolder.bind(data)

        // 檢查該商品是否在購物車中，並設置相應的背景顏色，且如果數量不為0更新選擇數量
        if (isProductInCart(data)) {
            // 商品在購物車中時的背景顏色
            view.setBackgroundResource(R.color.purple_200)

            // 獲取購物車中的商品對象
            val cartProduct = getCartProduct(data)

            // 設置商品數量到對應的 TextView
            viewHolder.shopNumber.text = "${getSelectNumber(cartProduct)}"
        } else {
            // 商品不在購物車中時的背景顏色
            view.setBackgroundResource(R.color.white)
        }

        return view
    }

    // 獲取購物車中的商品對象
    private fun getCartProduct(product: ProductItem): ProductItem? {
        return shoppingCart.selectedProducts.find { it.pId == product.pId }
    }

    // 取得該項商品的選擇數量
    private fun getSelectNumber(product: ProductItem?): Int {
        return product?.selectedQuantity ?: 0
    }

    class ViewHolder(itemView: View) {
        //請見product_item.xml
        private val productImg: ImageView = itemView.findViewById(R.id.img_productItem)
        private val productName: TextView = itemView.findViewById(R.id.txt_productName)
        private val productType: TextView = itemView.findViewById(R.id.txt_productType)
        private val productPrice: TextView = itemView.findViewById(R.id.txt_productPrice)
        private val productNumber: TextView = itemView.findViewById(R.id.txt_productNumber)

        val shopNumber: TextView = itemView.findViewById(R.id.txt_product_buy_number)
        @SuppressLint("SetTextI18n")
        fun bind(product: ProductItem) {
            // 使用 Glide 或其他圖片載入庫載入商品圖片
            Glide.with(productImg.context)
                .load(product.imageUrl)
                .into(productImg)

            productName.text = product.pName
            productType.text = product.pType
            productPrice.text = "${product.pPrice}元"
            productNumber.text = "${product.pNumber}個"
        }
    }

    private fun isProductInCart(product: ProductItem): Boolean {
        // 這裡實現你判斷商品是否在購物車中的邏輯，例如根據商品 ID 來判斷
        return shoppingCart.selectedProducts.any { it.pId == product.pId }
    }
}
