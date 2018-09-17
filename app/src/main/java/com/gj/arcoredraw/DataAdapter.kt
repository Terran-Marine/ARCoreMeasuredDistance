package com.gj.arcoredraw

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DataAdapter(dataList: ArrayList<AnchorInfoBean>) : BaseQuickAdapter<AnchorInfoBean, BaseViewHolder>(R.layout.item_data_text, dataList) {
    override fun convert(helper: BaseViewHolder, item: AnchorInfoBean) {
        helper.setText(R.id.UI_ItemDataText, item.dataText)
    }
}