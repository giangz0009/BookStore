package com.pro.book.model

class TabOrder(@JvmField var type: Int, @JvmField var name: String) {
    companion object {
        const val TAB_ORDER_PROCESS: Int = 1
        const val TAB_ORDER_DONE: Int = 2
    }
}
