package com.yogeshpaliyal.keypass.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.constants.AccountType
import com.yogeshpaliyal.keypass.data.MyAccountModel
import com.yogeshpaliyal.keypass.databinding.FragmentHomeBinding
import com.yogeshpaliyal.keypass.listener.AccountsClickListener
import com.yogeshpaliyal.keypass.ui.addTOTP.AddTOTPActivity
import com.yogeshpaliyal.keypass.ui.detail.DetailActivity
import com.yogeshpaliyal.universalAdapter.adapter.UniversalAdapterViewType
import com.yogeshpaliyal.universalAdapter.adapter.UniversalRecyclerAdapter
import com.yogeshpaliyal.universalAdapter.utils.Resource
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
        UniversalRecyclerAdapter.Builder<MyAccountModel>(
            this,
            content = UniversalAdapterViewType.Content(
                R.layout.item_accounts,
                listener = mListener
            ),
            noData = UniversalAdapterViewType.NoData(R.layout.layout_no_accounts)
        ).build()
    }

    val mListener = object : AccountsClickListener<AccountModel> {
        override fun onItemClick(view: View, model: AccountModel) {
            if (model.type == AccountType.TOTP) {
                AddTOTPActivity.start(context, model.uniqueId)
            } else {
                DetailActivity.start(context, model.id)
            }
        }

        override fun onCopyClicked(model: AccountModel) {
            val clipboard =
                ContextCompat.getSystemService(
                    requireContext(),
                    ClipboardManager::class.java
                )
            val clip = ClipData.newPlainText("KeyPass", model.password)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(context, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT)
                .show()
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

    private val observer = Observer<List<AccountModel>> {
        val newList = it.map {accountModel ->
            MyAccountModel().also {
                it.map(accountModel)
            }
        }
        mAdapter.updateData(Resource.success(ArrayList(newList)))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = mAdapter.getAdapter()

        mViewModel.mediator.observe(
            viewLifecycleOwner,
            Observer {
                it.removeObserver(observer)
                it.observe(viewLifecycleOwner, observer)
            }
        )

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
