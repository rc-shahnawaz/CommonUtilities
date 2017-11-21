package com.shaizy.utilities

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_empty_screen.view.*

abstract class BaseRecyclerAdapter<T, K : RecyclerView.ViewHolder>() :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    constructor(list: List<T>? = null, progress: Boolean = false, click: ((K, T) -> Unit)? = null) : this() {
        list?.let { mList.addAll(it) }
        click?.let { clickListener = it }
        this.mProgressBarDisplayable = progress
    }


    protected val mList = mutableListOf<T>()

    private var mText1: String? = null
    private var mText2: String? = null
    private var mIcon: Int? = null
    private var mButtonImp: (() -> Unit)? = null
    private var mButtonText: String? = null
    private var mFullWidth: Boolean = true
    private var mProgressBarDisplayable = false

    open fun replace(list: List<T>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun addAll(list: List<T>) {
        val old = mList.size
        if (mList.addAll(old, list))
            notifyItemRangeInserted(old, list.size)
    }

    fun clear() {
        val old = mList.size
        if (old > 0) {
            mList.clear()
            notifyItemRangeRemoved(0, old)
        }
    }

    fun add(item: T) {
        val pos = mList.indexOf(item)
        if (pos == -1) {
            mList.add(item)
            notifyItemInserted(mList.size)
        } else notifyItemChanged(pos)
    }

    fun addFirst(item: T) {
        if (mList.indexOf(item) == -1) {
            mList.add(0, item)
            notifyItemInserted(0)
        } else notifyDataSetChanged()
    }

    fun remove(item: T) {
        val position = mList.indexOf(item)
        if (position != -1) {
            remove(position)
        }
    }

    fun remove(index: Int) {
        mList.removeAt(index)
        notifyItemRemoved(index)
    }

    fun setEmpty(text1: String = "", text2: String, icon: Int, button: (() -> Unit)? = null, buttonText: String? = null,
                 fullWidth: Boolean = true) {
        clear()
        mText1 = text1
        mText2 = text2
        mIcon = icon
        mButtonImp = button
        mButtonText = buttonText
        mFullWidth = fullWidth

        notifyDataSetChanged()
    }

    fun listSize() = mList.size

    fun isEmpty() = mList.isEmpty()

    fun isEmptyNeeded(): Boolean = mList.isEmpty() && !mText2.isNullOrEmpty()

    abstract fun bind(item: T, position: Int, holder: K)

    abstract fun viewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): K

    var clickListener: ((K, T) -> Unit)? = null

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            holder is BaseRecyclerAdapter<*, *>.EmptyViewHolder -> {
                holder.text1.text = mText1
                holder.text2.text = mText2
                mIcon?.let { holder.image.setBackgroundResource(it) }

                holder.text1.visibility = if (mText1.isNullOrEmpty()) View.GONE else View.VISIBLE
                holder.button.visibility = if (mButtonText.isNullOrEmpty()) View.GONE else {
                    holder.button.text = mButtonText
                    holder.button.setOnClickListener { mButtonImp?.invoke() }
                    View.VISIBLE
                }

            }
            holder is BaseRecyclerAdapter<*, *>.ProgressViewHolder -> {

            }
            position < mList.size -> bind(mList[position], position, holder as K)
            else -> {
                // Do Nothing
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                1 -> EmptyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_empty_screen, parent, false))
                2 -> ProgressViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_large_progress_bar, parent, false))
                else -> {
                    val holder = viewHolder(LayoutInflater.from(parent.context), parent, viewType)

                    if (viewType == 0) {
                        holder.itemView.setOnClickListener {
                            clickListener?.invoke(holder, mList[holder.layoutPosition])
                        }
                    }
                    holder
                }
            }

    override fun getItemCount(): Int =
            if (isEmptyNeeded() || (mProgressBarDisplayable && isEmpty())) 1
            else mList.size

    override fun getItemViewType(position: Int): Int =
            if (mProgressBarDisplayable && isEmpty()) 2
            else if (isEmptyNeeded()) 1 else 0

    operator fun get(i: Int) = mList[i]

    fun list() = mList

    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text1 = itemView.text1 as TextView
        val text2 = itemView.text2 as TextView
        val image = itemView.icon as ImageView
        val button = itemView.button1 as Button

        init {
            if (!mFullWidth) {
                itemView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                itemView.requestLayout()
            } else {
                itemView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                itemView.requestLayout()
            }
        }
    }

    inner class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progress: ProgressBar = itemView as ProgressBar
    }
}