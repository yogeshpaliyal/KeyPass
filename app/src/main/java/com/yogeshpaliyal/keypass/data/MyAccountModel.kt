package com.yogeshpaliyal.keypass.data

import com.google.gson.Gson
import com.yogeshpaliyal.common.constants.AccountType
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.universalAdapter.listener.UniversalViewType
import com.yogeshpaliyal.universalAdapter.model.BaseDiffUtil

class MyAccountModel : BaseDiffUtil, UniversalViewType {

    private var accountModel = AccountModel()

    fun getAccountModel(): AccountModel {
        return accountModel
    }

    override fun getDiffId(): Any? {
        return accountModel.id
    }

    override fun getDiffBody(): Any? {
        return if (accountModel.type == AccountType.TOTP) {
            super.getDiffBody()
        } else {
            Gson().toJson(this)
        }
    }

    override fun getLayoutId(): Int = if (accountModel.type == AccountType.TOTP) R.layout.item_totp else R.layout.item_accounts

    fun map(accountModel: AccountModel) {
        this.accountModel = accountModel
    }
}
