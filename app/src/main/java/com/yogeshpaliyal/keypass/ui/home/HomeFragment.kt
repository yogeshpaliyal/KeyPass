package com.yogeshpaliyal.keypass.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.data.AccountModel
import com.yogeshpaliyal.keypass.databinding.FragmentHomeBinding
import com.yogeshpaliyal.keypass.listener.UniversalClickListener
import com.yogeshpaliyal.keypass.ui.detail.DetailActivity
import com.yogeshpaliyal.keypass.utils.initViewModel
import com.yogeshpaliyal.universal_adapter.adapter.UniversalAdapterViewType
import com.yogeshpaliyal.universal_adapter.adapter.UniversalRecyclerAdapter
import com.yogeshpaliyal.universal_adapter.utils.Resource
import com.yogeshpaliyal.universal_adapter.utils.UniversalAdapterOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 09:25
*/
class HomeFragment : Fragment() {
    private  lateinit var binding : FragmentHomeBinding

    private val mViewModel by lazy {
        initViewModel(HomeViewModel::class.java)
    }

    private val args: HomeFragmentArgs by navArgs()


    private val mAdapter by lazy {
        val adapterOptions = UniversalAdapterOptions<AccountModel>(this,
            content = UniversalAdapterViewType.Content(R.layout.item_accounts,mListener = mListener),
        noData = UniversalAdapterViewType.NoData(R.layout.layout_no_accounts))

        UniversalRecyclerAdapter<AccountModel>(adapterOptions)
    }

    val mListener = object : UniversalClickListener<AccountModel>{
        override fun onItemClick(view: View, model: AccountModel) {
            DetailActivity.start(context, model.id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = mAdapter
        lifecycleScope.launch(Dispatchers.IO){
            mViewModel.loadData(args.tag).collect {
                withContext(Dispatchers.Main) {
                    mAdapter.updateData(Resource.success(ArrayList(it)))
                }
            }
        }

    }



}