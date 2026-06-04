package com.example.assetstar.domain.model

data class LedgerEntry(
    val id: Long = 0L,
    val title: String = "",
    val amount: Double = 0.0,
    val type: LedgerType = LedgerType.EXPENSE,
    val category: LedgerCategory = LedgerCategory.LIVING,
    val account: LedgerAccount = LedgerAccount.BANK_CARD,
    val occurredAt: Long = 0L,
    val note: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)

enum class LedgerType(
    val storageValue: String,
    val displayName: String,
) {
    INCOME("income", "收入"),
    EXPENSE("expense", "支出");

    companion object {
        fun fromStorage(value: String): LedgerType {
            return entries.firstOrNull { it.storageValue == value } ?: EXPENSE
        }
    }
}

enum class LedgerAccount(
    val storageValue: String,
    val displayName: String,
    val shortName: String,
) {
    BANK_CARD("bank_card", "银行卡舱", "银行卡"),
    ALIPAY("alipay", "支付宝舱", "支付宝"),
    WECHAT("wechat", "微信舱", "微信");

    companion object {
        fun fromStorage(value: String): LedgerAccount {
            return entries.firstOrNull { it.storageValue == value } ?: BANK_CARD
        }
    }
}

enum class LedgerCategory(
    val storageValue: String,
    val displayName: String,
) {
    LIVING("living", "生活"),
    FOOD("food", "餐饮"),
    TRANSPORT("transport", "出行"),
    SHOPPING("shopping", "购物"),
    ENTERTAINMENT("entertainment", "娱乐"),
    SALARY("salary", "工资"),
    BONUS("bonus", "奖金"),
    OTHER("other", "其他");

    companion object {
        fun fromStorage(value: String): LedgerCategory {
            return entries.firstOrNull { it.storageValue == value } ?: OTHER
        }
    }
}
