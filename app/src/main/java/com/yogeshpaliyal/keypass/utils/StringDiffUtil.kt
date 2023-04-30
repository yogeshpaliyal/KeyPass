package com.yogeshpaliyal.keypass.utils

import androidx.recyclerview.widget.DiffUtil

/**
 * Alias to represent a folder (a String title) into which emails can be placed.
 */

object StringDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
}
