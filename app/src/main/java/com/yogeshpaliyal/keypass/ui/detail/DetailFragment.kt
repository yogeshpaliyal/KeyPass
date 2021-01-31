package com.yogeshpaliyal.keypass.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.data.AccountModel
import com.yogeshpaliyal.keypass.databinding.FragmentDetailBinding
import com.yogeshpaliyal.keypass.utils.initViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 10:38
*/
class DetailFragment : Fragment() {
    
    lateinit var binding : FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()

    private val mViewModel by lazy {
        initViewModel(DetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.loadAccount(args.accountId)
        mViewModel.accountModel.observe(viewLifecycleOwner, Observer {
            binding.accountData = it
        })


    }

    fun fabClicked(){
        lifecycleScope.launch(Dispatchers.IO) {
            val model = mViewModel.accountModel.value
            if (model != null) {
                AppDatabase.getInstance().getDao().insertOrUpdateAccount(model)
            }
            withContext(Dispatchers.Main) {
                findNavController().popBackStack()
            }
        }
    }
}