package com.yogeshpaliyal.keypass.data

import com.google.gson.Gson
import com.yogeshpaliyal.common.constants.AccountType
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.universalAdapter.listener.UniversalViewType
import com.yogeshpaliyal.universalAdapter.model.BaseDiffUtil

class MyAccountModel : AccountModel(), BaseDiffUtil, UniversalViewType {
    override fun getDiffId(): Any? {
        return id
    }

    override fun getDiffBody(): Any? {
        return if (type == AccountType.TOTP) {
            super.getDiffBody()
        } else {
            Gson().toJson(this)
        }
    }

    override fun getLayoutId(): Int = if (type == AccountType.TOTP) R.layout.item_totp else R.layout.item_accounts

    fun map(accountModel: AccountModel) {
        this.id = accountModel.id
        this.title = accountModel.title
        this.uniqueId = accountModel.uniqueId
        this.username = accountModel.username
        this.password = accountModel.password
        this.site = accountModel.site
        this.notes = accountModel.notes
        this.tags = accountModel.tags
        this.type = accountModel.type
    }
}
