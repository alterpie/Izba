package com.yerastov.android.izba.data

import android.content.Context
import android.os.Environment
import com.bumptech.glide.Glide
import com.yerastov.android.izba.domain.Item
import com.yerastov.android.izba.domain.Person
import com.yerastov.android.izba.domain.Supplier
import java.io.File

/**
 * Created by yerastov on 28/09/17.
 */
object DataManager {

    private val PERSON_FILENAME = "Users.csv"
    private val SUPPLIER_FILENAME = "Suppliers.csv"
    private val ITEM_FILENAME = "Items.csv"

    fun savePerson(person: Person, context: Context) {
        val str = formatEntity(person.device, person.name)
        val file = File(getCacheDirectory(context), PERSON_FILENAME)
        file.writeToFile(str)
    }

    fun readPersons(context: Context): List<Person> {
        val list = ArrayList<Person>()
        val file = File(getCacheDirectory(context), PERSON_FILENAME)
        if (!file.exists()) file.createNewFile()
        file.forEachLine {
            val split = it.split(";")
            if (split.size > 1) {
                list.add(Person(split[1], split[0]))
            }
        }
        return list
    }

    fun saveSupplier(supplier: Supplier, context: Context) {
        val str = formatEntity(supplier.id, supplier.name, supplier.date, supplier.personName)
        val file = File(getCacheDirectory(context), SUPPLIER_FILENAME)
        file.writeToFile(str)
    }

    fun readSuppliers(context: Context): List<Supplier> {
        val list = ArrayList<Supplier>()
        val file = File(getCacheDirectory(context), SUPPLIER_FILENAME)
        with(file) {
            if (!exists()) createNewFile()
            forEachLine {
                val split = it.split(";")
                if (split.size > 1) {
                    list.add(Supplier(split[1], split[0], split[2], split[3]))
                }
            }
        }
        return list
    }

    fun saveItem(item: Item, context: Context) {
        val str = formatEntity(item.id, item.supplierId, item.moq, item.supplierItem,
                item.priceBasis, item.outerWeightGW, item.outerWeightNW, item.weightUnitNW,
                item.outerPackType, item.innerPackType, item.outerPackHeight, item.dateAndTime,
                item.outerPackLength, item.moqUnit, item.productUnit, item.quantityOuterPack,
                item.quantityInnerPack, item.comments, item.priceComments, item.material,
                item.productName, item.outerPackVolume, item.setDetails, item.productDescription,
                item.inputPerson, item.supplierName, item.size, item.moqLeadTime,
                item.unitPackingDetails, item.photo, item.price, item.outerPackWidth)
        val file = File(getCacheDirectory(context), ITEM_FILENAME)
        file.writeToFile(str)
    }

    private fun File.writeToFile(str: String) {
        if (!exists()) createNewFile()
        try {
            appendText(str)
        } catch (e: Exception) {

        }
    }

    private fun getCacheDirectory(context: Context): File {
        val cache: File = if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            File(Environment.getExternalStorageDirectory(), "IZBA")
        else context.cacheDir
        with(cache) {
            if (!exists()) mkdirs()
        }
        return cache
    }

    private fun formatEntity(vararg strings: String): String {
        val builder = StringBuilder()
        strings.forEach {
            builder.append(it).append(';')
        }
        builder.append('\r').append('\n')
        return builder.toString()
    }

    fun loadSupplierPhotos(supplierId: String, context: Context): List<String> {
        val list = ArrayList<String>()
        val path = getCacheDirectory(context).absolutePath + File.separator + "photos" + File.separator + "suppliers" + File.separator + supplierId
        val dir = File(path)
        with(dir) {
            if (exists() && isDirectory) {
                list().mapTo(list) { path + File.separator + it }
            }
        }
        return list
    }

    fun saveSupplierPhoto(path: String, supplierName: String, supplierId: String, context: Context): String {
        val dir = getCacheDirectory(context).absolutePath + File.separator + "photos" + File.separator + "suppliers" + File.separator + supplierId
        val supplierNumber = getSupplierPhotoNumber(dir)
        supplierNumber?.let {
            val file = File(dir, supplierId + "_" + it + "." + "jpg")
            val fileToCopy = File(path)
            with(fileToCopy) {
                copyTo(file)
                Glide.get(context).clearDiskCache()
                return file.absolutePath
            }
        }
        return ""
    }

    private fun getSupplierPhotoNumber(path: String): Int? {
        val dir = File(path)
        with(dir) {
            val arr = list()
            return if (exists() && isDirectory && !arr.isEmpty()) list().mapTo(ArrayList()) { it.split("_")[2].split(".")[0].toInt() }.max()?.plus(1)
            else 1
        }
    }

    private fun getItemPhotoNumber(path: String): Int? {
        val dir = File(path)
        with(dir) {
            val arr = list()
            return if (exists() && isDirectory && !arr.isEmpty()) list().mapTo(ArrayList()) { it.split("_")[3].split(".")[0].toInt() }.max()?.plus(1)
            else 1
        }
    }

    fun deletePhoto(path: String, context: Context) {
        val file = File(path)
        with(file) {
            if (exists()) delete()
        }
        Glide.get(context).clearDiskCache()
    }

    fun saveItemPhoto(path: String, itemId: String, context: Context): String {
        val dir = getCacheDirectory(context).absolutePath + File.separator + "photos" + File.separator + "items" + File.separator + itemId
        val parentDir = File(dir)
        if (!parentDir.exists()) parentDir.mkdirs()
        val itemNumber = getItemPhotoNumber(dir)
        itemNumber?.let {
            val file = File(dir, itemId + "_" + it + "." + "jpg")
            val fileToCopy = File(path)
            with(fileToCopy) {
                copyTo(file)
                com.bumptech.glide.Glide.get(context).clearDiskCache()
                return file.absolutePath
            }
        }
        return ""
    }
}