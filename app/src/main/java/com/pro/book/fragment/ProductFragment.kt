package com.pro.book.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.activity.ProductDetailActivity
import com.pro.book.adapter.FilterAdapter
import com.pro.book.adapter.FilterAdapter.IClickFilterListener
import com.pro.book.adapter.ProductAdapter
import com.pro.book.event.SearchKeywordEvent
import com.pro.book.model.Filter
import com.pro.book.model.Product
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.startActivity
import com.pro.book.utils.StringUtil.isEmpty
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.Normalizer
import java.util.Locale
import java.util.regex.Pattern

class ProductFragment : Fragment() {
    private var mView: View? = null
    private var rcvFilter: RecyclerView? = null
    private var rcvProduct: RecyclerView? = null

    private var listProduct: MutableList<Product>? = null
    private var listProductDisplay: MutableList<Product>? = null
    private var listProductKeyWord: MutableList<Product>? = null
    private var listFilter: MutableList<Filter>? = null
    private var productAdapter: ProductAdapter? = null
    private var filterAdapter: FilterAdapter? = null
    private var categoryId: Long = 0
    private var currentFilter: Filter? = null
    private var keyword = ""
    private var mValueEventListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_product, container, false)

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        dataArguments
        initUi()
        initListener()

        getListFilter()
        getListProduct()

        return mView
    }

    private val dataArguments: Unit
        get() {
            val bundle = arguments ?: return
            categoryId = bundle.getLong(Constant.CATEGORY_ID)
        }

    private fun initUi() {
        rcvFilter = mView!!.findViewById(R.id.rcv_filter)
        rcvProduct = mView!!.findViewById(R.id.rcv_product)
        displayListProduct()
    }

    private fun initListener() {
    }

    private fun getListFilter() {
        listFilter = ArrayList()
        listFilter?.add(Filter(Filter.TYPE_FILTER_ALL, getString(R.string.filter_all)))
        listFilter?.add(Filter(Filter.TYPE_FILTER_RATE, getString(R.string.filter_rate)))
        listFilter?.add(Filter(Filter.TYPE_FILTER_PRICE, getString(R.string.filter_price)))
        listFilter?.add(Filter(Filter.TYPE_FILTER_PROMOTION, getString(R.string.filter_promotion)))

        val linearLayoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL, false
        )
        rcvFilter!!.layoutManager = linearLayoutManager
        currentFilter = listFilter?.get(0)
        currentFilter!!.isSelected = true
        filterAdapter = FilterAdapter(
            activity,
            listFilter,
            object : IClickFilterListener {
                override fun onClickFilterItem(filter: Filter?) {
                    filter?.let { handleClickFilter(it) }
                }
            })

        rcvFilter!!.adapter = filterAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleClickFilter(filter: Filter) {
        for (filterEntity in listFilter!!) {
            if (filterEntity.id == filter.id) {
                filterEntity.isSelected = true
                setListProductDisplay(filterEntity, keyword)
                currentFilter = filterEntity
            } else {
                filterEntity.isSelected = false
            }
        }
        if (filterAdapter != null) filterAdapter!!.notifyDataSetChanged()
    }

    private fun getListProduct() {
        if (activity == null) return
        mValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (listProduct != null) {
                    listProduct!!.clear()
                } else {
                    listProduct = ArrayList()
                }
                for (dataSnapshot in snapshot.children) {
                    val product = dataSnapshot.getValue(
                        Product::class.java
                    )
                    if (product != null) {
                        listProduct!!.add(0, product)
                    }
                }
                setListProductDisplay(
                    Filter(
                        Filter.TYPE_FILTER_ALL,
                        getString(R.string.filter_all)
                    ), keyword
                )
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        get(requireActivity()).productDatabaseReference
            .orderByChild(Constant.CATEGORY_ID).equalTo(categoryId.toDouble())
            .addValueEventListener(mValueEventListener!!)
    }

    private fun displayListProduct() {
        if (activity == null) return
        listProductDisplay = ArrayList()
        val linearLayoutManager = LinearLayoutManager(activity)
        rcvProduct!!.layoutManager = linearLayoutManager
        productAdapter =
            ProductAdapter(listProductDisplay) { product: Product ->
                val bundle = Bundle()
                bundle.putLong(Constant.PRODUCT_ID, product.id)
                startActivity(requireActivity(), ProductDetailActivity::class.java, bundle)
            }
        rcvProduct!!.adapter = productAdapter
    }

    private fun setListProductDisplay(filter: Filter, keyword: String?) {
        if (listProduct == null || listProduct!!.isEmpty()) return

        if (listProductKeyWord != null) {
            listProductKeyWord!!.clear()
        } else {
            listProductKeyWord = ArrayList()
        }

        if (listProductDisplay != null) {
            listProductDisplay!!.clear()
        } else {
            listProductDisplay = ArrayList()
        }

        if (!isEmpty(keyword)) {
            for (product in listProduct!!) {
                if (getTextSearch(product.name).lowercase(Locale.getDefault()).trim { it <= ' ' }
                        .contains(
                            getTextSearch(keyword).lowercase(Locale.getDefault())
                                .trim { it <= ' ' })) {
                    listProductKeyWord!!.add(product)
                }
            }
            when (filter.id) {
                Filter.TYPE_FILTER_ALL -> listProductDisplay!!.addAll(
                    listProductKeyWord!!
                )

                Filter.TYPE_FILTER_RATE -> {
                    listProductDisplay!!.addAll(listProductKeyWord!!)
                    listProductDisplay!!.sortWith { product1: Product, product2: Product ->
                        product2.rate.compareTo(product1.rate)
                    }
                }

                Filter.TYPE_FILTER_PRICE -> {
                    listProductDisplay!!.addAll(listProductKeyWord!!)
                    listProductDisplay!!.sortWith { product1: Product, product2: Product ->
                        product1.realPrice.compareTo(product2.realPrice)
                    }
                }

                Filter.TYPE_FILTER_PROMOTION -> for (product in listProductKeyWord!!) {
                    if (product.sale > 0) listProductDisplay!!.add(product)
                }
            }
        } else {
            when (filter.id) {
                Filter.TYPE_FILTER_ALL -> listProductDisplay!!.addAll(
                    listProduct!!
                )

                Filter.TYPE_FILTER_RATE -> {
                    listProductDisplay!!.addAll(listProduct!!)
                    listProductDisplay!!.sortWith { product1: Product, product2: Product ->
                        product2.rate.compareTo(product1.rate)
                    }
                }

                Filter.TYPE_FILTER_PRICE -> {
                    listProductDisplay!!.addAll(listProduct!!)
                    listProductDisplay!!.sortWith { product1: Product, product2: Product ->
                        product1.realPrice.compareTo(product2.realPrice)
                    }
                }

                Filter.TYPE_FILTER_PROMOTION -> for (product in listProduct!!) {
                    if (product.sale > 0) listProductDisplay!!.add(product)
                }
            }
        }
        reloadListProduct()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun reloadListProduct() {
        if (productAdapter != null) productAdapter!!.notifyDataSetChanged()
    }

    private fun getTextSearch(input: String?): String {
        val nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchKeywordEvent(event: SearchKeywordEvent) {
        keyword = event.keyword
        setListProductDisplay(currentFilter!!, keyword)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (filterAdapter != null) filterAdapter!!.release()
        if (activity != null && mValueEventListener != null) {
            get(requireActivity()).productDatabaseReference
                .removeEventListener(mValueEventListener!!)
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    companion object {
        fun newInstance(categoryId: Long): ProductFragment {
            val productFragment = ProductFragment()
            val bundle = Bundle()
            bundle.putLong(Constant.CATEGORY_ID, categoryId)
            productFragment.arguments = bundle
            return productFragment
        }
    }
}
