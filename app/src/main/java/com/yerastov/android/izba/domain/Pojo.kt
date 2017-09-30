package com.yerastov.android.izba.domain

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.IntDef
import android.support.annotation.StringDef
import com.yerastov.android.izba.R

/**
 * Created by yerastov on 26/09/17.
 */

data class Person(val name: String,
                  val device: String) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(device)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Person> = object : Parcelable.Creator<Person> {
            override fun createFromParcel(source: Parcel): Person = Person(source)
            override fun newArray(size: Int): Array<Person?> = arrayOfNulls(size)
        }
    }
}

data class Supplier(val name: String,
                    val id: String) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(id)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Supplier> = object : Parcelable.Creator<Supplier> {
            override fun createFromParcel(source: Parcel): Supplier = Supplier(source)
            override fun newArray(size: Int): Array<Supplier?> = arrayOfNulls(size)
        }
    }
}

data class Item(val id: String,
                val supplierId: String,
                val moq: String,
                val supplierItem: String,
                val priceBasis: String,
                val outerWeightGW: String,
                val outerWeightNW: String,
                val weightUnitNW: String,
                val outerPackType: String,
                val innerPackType: String,
                val outerPackHeight: String,
                val dateAndTime: String,
                val outerPackLength: String,
                val moqUnit: String,
                val productUnit: String,
                val quantityOuterPack: String,
                val quantityInnerPack: String,
                val comments: String,
                val priceComments: String,
                val material: String,
                val productName: String,
                val outerPackVolume: String,
                val setDetails: String,
                val productDescription: String,
                val inputPerson: String,
                val supplierName: String,
                val size: String,
                val moqLeadTime: String,
                val unitPackingDetails: String,
                val photo: String,
                val price: String,
                val outerPackWidth: String)

class PriceBasis private constructor(val name: String) {
    @IntDef(RMB_EXW_WO_TAX, RMB_FOB_WO_TAX, RMB_EXW_INCL_VAT, RMB_FOB_INCL_VAT, USD_EXW, USD_FOB)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type

    companion object {
        const val RMB_EXW_WO_TAX = 0L
        const val RMB_FOB_WO_TAX = 1L
        const val RMB_EXW_INCL_VAT = 2L
        const val RMB_FOB_INCL_VAT = 3L
        const val USD_EXW = 4L
        const val USD_FOB = 5L

        fun getName(@Type type: Long, context: Context): String {
            return when (type) {
                RMB_EXW_WO_TAX -> context.getString(R.string.rmb_exw_wo_tax)
                RMB_FOB_WO_TAX -> context.getString(R.string.rmb_fob_wo_tax)
                RMB_EXW_INCL_VAT -> context.getString(R.string.rmb_exw_incl_val)
                RMB_FOB_INCL_VAT -> context.getString(R.string.rmb_fob_incl_vat)
                USD_EXW -> context.getString(R.string.usd_exw)
                USD_FOB -> context.getString(R.string.usd_fob)

                else -> throw RuntimeException()
            }
        }

        fun createAvailableList(context: Context) = listOf(
                PriceBasis(getName(RMB_EXW_WO_TAX, context)),
                PriceBasis(getName(RMB_FOB_WO_TAX, context)),
                PriceBasis(getName(RMB_EXW_INCL_VAT, context)),
                PriceBasis(getName(RMB_FOB_INCL_VAT, context)),
                PriceBasis(getName(USD_EXW, context)),
                PriceBasis(getName(USD_FOB, context)))
    }
}

class ProductUnit private constructor(val name: String) {
    @IntDef(PC, PAIR, SET, DOZEN, PACK_BOX)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type

    companion object {

        const val PC = 0L
        const val PAIR = 1L
        const val SET = 2L
        const val DOZEN = 3L
        const val PACK_BOX = 4L

        fun getName(@Type type: Long, context: Context): String {
            return when (type) {
                PC -> context.getString(R.string.pc)
                PAIR -> context.getString(R.string.pair)
                SET -> context.getString(R.string.set)
                DOZEN -> context.getString(R.string.dozen)
                PACK_BOX -> context.getString(R.string.pack_box)
                else -> throw RuntimeException()
            }
        }

        fun createAvailableList(context: Context) = listOf(
                ProductUnit(getName(PC, context)),
                ProductUnit(getName(PAIR, context)),
                ProductUnit(getName(SET, context)),
                ProductUnit(getName(DOZEN, context)),
                ProductUnit(getName(PACK_BOX, context))
        )
    }
}

class UnitPacking private constructor(val name: String) {
    @IntDef(BULK, PP_BAG, PP_BAG_PAPER_HEADER, PAPER_HEADER,
            PAPER_CARD_WITHOUT_PP_BAG, HANG_TAG, BLISTER, COLOR_BOX,
            BROWN_BOX, PVC_BOX)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type

    companion object {

        const val BULK = 0L
        const val PP_BAG = 1L
        const val PP_BAG_PAPER_HEADER = 2L
        const val PAPER_HEADER = 3L
        const val PAPER_CARD_WITHOUT_PP_BAG = 4L
        const val HANG_TAG = 5L
        const val BLISTER = 6L
        const val COLOR_BOX = 7L
        const val BROWN_BOX = 8L
        const val PVC_BOX = 9L

        fun getName(@Type type: Long, context: Context): String {
            return when (type) {
                BULK -> context.getString(R.string.bulk)
                PP_BAG -> context.getString(R.string.pp_bag)
                PP_BAG_PAPER_HEADER -> context.getString(R.string.pp_bag_paper_header)
                PAPER_HEADER -> context.getString(R.string.paper_header)
                PAPER_CARD_WITHOUT_PP_BAG -> context.getString(R.string.paper_card_without_pp_bag)
                HANG_TAG -> context.getString(R.string.hang_tag)
                BLISTER -> context.getString(R.string.blister)
                COLOR_BOX -> context.getString(R.string.color_box)
                BROWN_BOX -> context.getString(R.string.brown_box)
                PVC_BOX -> context.getString(R.string.pvc_box)
                else -> throw RuntimeException()
            }
        }

        fun createAvailableList(context: Context): List<UnitPacking>
                = listOf(
                UnitPacking(getName(BULK, context)),
                UnitPacking(getName(PP_BAG, context)),
                UnitPacking(getName(PP_BAG_PAPER_HEADER, context)),
                UnitPacking(getName(PAPER_HEADER, context)),
                UnitPacking(getName(PAPER_CARD_WITHOUT_PP_BAG, context)),
                UnitPacking(getName(HANG_TAG, context)),
                UnitPacking(getName(BLISTER, context)),
                UnitPacking(getName(COLOR_BOX, context)),
                UnitPacking(getName(BROWN_BOX, context)),
                UnitPacking(getName(PVC_BOX, context)))
    }
}

class InnerPackType(val name: String) {
    @IntDef(BULK, PP_BAG, BROWN_BOX, DISPLAY_BOX, STRING_BUNDLE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type

    companion object {

        const val BULK = 0L
        const val PP_BAG = 1L
        const val BROWN_BOX = 2L
        const val DISPLAY_BOX = 3L
        const val STRING_BUNDLE = 4L

        fun getName(@Type type: Long, context: Context): String {
            return when (type) {
                BULK -> context.getString(R.string.bulk)
                PP_BAG -> context.getString(R.string.pp_bag)
                BROWN_BOX -> context.getString(R.string.brown_box)
                DISPLAY_BOX -> context.getString(R.string.display_box)
                STRING_BUNDLE -> context.getString(R.string.string_bundle)
                else -> throw RuntimeException()
            }
        }

        fun createAvailableList(context: Context) = listOf(
                InnerPackType(getName(BULK, context)),
                InnerPackType(getName(PP_BAG, context)),
                InnerPackType(getName(BROWN_BOX, context)),
                InnerPackType(getName(DISPLAY_BOX, context)),
                InnerPackType(getName(STRING_BUNDLE, context)))
    }
}

class OuterPackType(val name: String) {
    @IntDef(BULK, WOVEN_PP_BAG, CARTON)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type

    companion object {

        const val CARTON = 0L
        const val WOVEN_PP_BAG = 1L
        const val BULK = 2L

        fun getName(@Type type: Long, context: Context): String {
            return when (type) {
                BULK -> context.getString(R.string.bulk)
                CARTON -> context.getString(R.string.carton)
                WOVEN_PP_BAG -> context.getString(R.string.woven_pp_bag)
                else -> throw RuntimeException()
            }
        }

        fun createAvailableList(context: Context) = listOf(
                OuterPackType(getName(CARTON, context)),
                OuterPackType(getName(WOVEN_PP_BAG, context)),
                OuterPackType(getName(BULK, context)))
    }
}

class MoqUnit private constructor(val name: String) {
    @IntDef(PC, CARTON, DOZEN, PAIR, SET)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type

    companion object {

        const val PC = 0L
        const val CARTON = 1L
        const val DOZEN = 2L
        const val PAIR = 3L
        const val SET = 4L

        fun getName(@Type type: Long, context: Context): String {
            return when (type) {
                PC -> context.getString(R.string.pc)
                CARTON -> context.getString(R.string.carton)
                DOZEN -> context.getString(R.string.dozen)
                PAIR -> context.getString(R.string.pair)
                SET -> context.getString(R.string.set)
                else -> throw RuntimeException()
            }
        }

        fun createAvailableList(context: Context) = listOf(
                MoqUnit(getName(PC, context)),
                MoqUnit(getName(CARTON, context)),
                MoqUnit(getName(DOZEN, context)),
                MoqUnit(getName(PAIR, context)),
                MoqUnit(getName(SET, context)))
    }
}

class Language private constructor() {
    companion object {
        const val CHINESE = "cn"
        const val RUSSIAN = "ru"
        const val ENGLISH = "en"
    }

    @StringDef(CHINESE, RUSSIAN, ENGLISH)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Locale

}