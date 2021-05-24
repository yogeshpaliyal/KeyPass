package com.yogeshpaliyal.keypass.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.data.AccountModel
import com.yogeshpaliyal.keypass.databinding.FragmentHomeBinding
import com.yogeshpaliyal.keypass.listener.UniversalClickListener
import com.yogeshpaliyal.keypass.ui.detail.DetailActivity
import com.yogeshpaliyal.universal_adapter.adapter.UniversalAdapterViewType
import com.yogeshpaliyal.universal_adapter.adapter.UniversalRecyclerAdapter
import com.yogeshpaliyal.universal_adapter.utils.Resource
import dagger.hilt.android.AndroidEntryPoint


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 09:25
*/
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val mViewModel by lazy {
        requireActivity().viewModels<DashboardViewModel>().value
    }

    private val mAdapter by lazy {
        UniversalRecyclerAdapter.Builder<AccountModel>(
            this,
            content = UniversalAdapterViewType.Content(
                R.layout.item_accounts,
                listener = mListener
            ),
            noData = UniversalAdapterViewType.NoData(R.layout.layout_no_accounts)
        ).build()
    }

    val mListener = object : UniversalClickListener<AccountModel> {
        override fun onItemClick(view: View, model: AccountModel) {
            DetailActivity.start(context, model.id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    val observer = Observer<List<AccountModel>> {
        mAdapter.updateData(Resource.success(it))

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = mAdapter.getAdapter()

        mViewModel.mediator.observe(viewLifecycleOwner, Observer {
            it.removeObserver(observer)
            it.observe(viewLifecycleOwner, observer)
        })

        /* lifecycleScope.launch() {
             mViewModel.result
             mViewModel.loadData(args.tag).collect {
                 withContext(Dispatchers.Main) {
                     mAdapter.updateData(Resource.success(ArrayList(it)))
                 }
             }
         }*/

    }


}