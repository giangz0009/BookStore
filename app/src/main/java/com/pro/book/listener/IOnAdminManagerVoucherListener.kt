package com.pro.book.listener

import com.pro.book.model.Voucher

interface IOnAdminManagerVoucherListener {
    fun onClickUpdateVoucher(voucher: Voucher)
    fun onClickDeleteVoucher(voucher: Voucher)
}
