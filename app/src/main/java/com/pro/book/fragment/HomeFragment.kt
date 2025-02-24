package com.pro.book.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pro.book.MyApplication.Companion.get
import com.pro.book.R
import com.pro.book.activity.ProductDetailActivity
import com.pro.book.adapter.BannerViewPagerAdapter
import com.pro.book.adapter.CategoryPagerAdapter
import com.pro.book.event.SearchKeywordEvent
import com.pro.book.model.Category
import com.pro.book.model.Product
import com.pro.book.utils.Constant
import com.pro.book.utils.GlobalFunction.hideSoftKeyboard
import com.pro.book.utils.GlobalFunction.startActivity
import me.relex.circleindicator.CircleIndicator3
import org.greenrobot.eventbus.EventBus
import java.util.Locale

class HomeFragment : Fragment() {
    private var mView: View? = null
    private var viewPagerProductFeatured: ViewPager2? = null
    private var indicatorProductFeatured: CircleIndicator3? = null
    private var viewPagerCategory: ViewPager2? = null
    private var tabCategory: TabLayout? = null
    private var edtSearchName: EditText? = null
    private var imgSearch: ImageView? = null

    private var listProductFeatured: MutableList<Product>? = null
    private var listCategory: MutableList<Category>? = null
    private var mCategoryValueEventListener: ValueEventListener? = null
    private var mProductValueEventListener: ValueEventListener? = null

    private val mHandlerBanner = Handler(Looper.getMainLooper())
    private val mRunnableBanner = Runnable {
        if (viewPagerProductFeatured == null || listProductFeatured == null || listProductFeatured!!.isEmpty()) {
            return@Runnable
        }
        if (viewPagerProductFeatured!!.currentItem == listProductFeatured!!.size - 1) {
            viewPagerProductFeatured!!.currentItem = 0
            return@Runnable
        }
        viewPagerProductFeatured!!.currentItem += 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_home, container, false)

        initUi()
        initListener()

        listProductBanner
        getListCategory()

        return mView
    }

    private fun initUi() {
        viewPagerProductFeatured = mView!!.findViewById(R.id.view_pager_product_featured)
        indicatorProductFeatured = mView!!.findViewById(R.id.indicator_product_featured)
        viewPagerCategory = mView!!.findViewById(R.id.view_pager_category)
        viewPagerCategory?.setUserInputEnabled(false)
        tabCategory = mView!!.findViewById(R.id.tab_category)
        edtSearchName = mView!!.findViewById(R.id.edt_search_name)
        imgSearch = mView!!.findViewById(R.id.img_search)
    }

    private fun initListener() {
        edtSearchName!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val strKey = s.toString().trim { it <= ' ' }
                if (strKey.isEmpty()) {
                    searchProduct()
                }
            }
        })

        imgSearch!!.setOnClickListener { searchProduct() }

        edtSearchName!!.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchProduct()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private val listProductBanner: Unit
        get() {
            if (activity == null) return
            mProductValueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (listProductFeatured != null) {
                        listProductFeatured!!.clear()
                    } else {
                        listProductFeatured = ArrayList()
                    }
                    for (dataSnapshot in snapshot.children) {
                        val product = dataSnapshot.getValue(
                            Product::class.java
                        )
                        if (product != null && product.isFeatured) {
                            listProductFeatured!!.add(product)
                        }
                    }
                    displayListBanner()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            get(requireActivity()).productDatabaseReference
                .addValueEventListener(mProductValueEventListener!!)
        }

    private fun displayListBanner() {
        val adapter =
            BannerViewPagerAdapter(listProductFeatured
            ) { product ->
                val bundle = Bundle()
                bundle.putLong(Constant.PRODUCT_ID, product.id)
                startActivity(requireActivity(), ProductDetailActivity::class.java, bundle)
            }
        viewPagerProductFeatured!!.adapter = adapter
        indicatorProductFeatured!!.setViewPager(viewPagerProductFeatured)

        viewPagerProductFeatured!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mHandlerBanner.removeCallbacks(mRunnableBanner)
                mHandlerBanner.postDelayed(mRunnableBanner, 3000)
            }
        })
    }

    private fun getListCategory() {
        if (activity == null) return
        mCategoryValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (listCategory != null) {
                    listCategory!!.clear()
                } else {
                    listCategory = ArrayList()
                }
                for (dataSnapshot in snapshot.children) {
                    val category = dataSnapshot.getValue(
                        Category::class.java
                    )
                    if (category != null) {
                        listCategory!!.add(category)
                    }
                }
                displayTabsCategory()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        get(requireActivity()).categoryDatabaseReference
            .addValueEventListener(mCategoryValueEventListener!!)
    }

    private fun displayTabsCategory() {
        if (activity == null || listCategory == null || listCategory!!.isEmpty()) return
        viewPagerCategory!!.offscreenPageLimit = listCategory!!.size
        val adapter = CategoryPagerAdapter(requireActivity(), listCategory)
        viewPagerCategory!!.adapter = adapter
        TabLayoutMediator(
            tabCategory!!, viewPagerCategory!!
        ) { tab: TabLayout.Tab, position: Int ->
            tab.setText(
                listCategory!![position].name!!.lowercase(Locale.getDefault())
            )
        }
            .attach()
    }

    private fun searchProduct() {
        val strKey = edtSearchName!!.text.toString().trim { it <= ' ' }
        EventBus.getDefault().post(SearchKeywordEvent(strKey))
        hideSoftKeyboard(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity != null && mCategoryValueEventListener != null) {
            get(requireActivity()).categoryDatabaseReference
                .removeEventListener(mCategoryValueEventListener!!)
        }
        if (activity != null && mProductValueEventListener != null) {
            get(requireActivity()).productDatabaseReference
                .removeEventListener(mProductValueEventListener!!)
        }
    }
}
