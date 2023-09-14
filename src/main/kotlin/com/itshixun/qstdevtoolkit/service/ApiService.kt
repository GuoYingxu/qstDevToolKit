package com.itshixun.qstdevtoolkit.service

import javax.swing.DefaultListModel

class ApiService {

    companion object {
        val apiUsages = DefaultListModel<ApiUsageModel>()
    }
}
data class ApiUsageModel(var clientName:String,var url:String,var position:String,var fileType:String)