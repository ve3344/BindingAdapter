package me.lwb.adapter.demo.ui.activity.wheel

import android.content.Context
import android.location.Address
import me.lwb.adapter.wheel.LinkageWheelView
import me.lwb.adapter.wheel.setData
import org.json.JSONArray
import java.io.Serializable

/**
 * Created by ve3344@qq.com.
 */
open class AddressChooseModule(
    private val linkageWheelView: LinkageWheelView,
    currentData: List<Province>
) {

    var addressData: List<Province> = currentData
        set(value) {
            field = value
            refresh()
        }

    init {
        refresh()
    }

    private fun refresh() {
        val provinces: List<Province> = addressData
        if (provinces.isEmpty()) {
            return
        }
        linkageWheelView.setData {
            provideData { provinces.map { it.name } }
            provideData { provinces[it[0].selectedPosition].cities.map { it.name } }
            provideData { provinces[it[0].selectedPosition].cities[it[1].selectedPosition].counties }
        }
    }

    var current: AddressBean?
        get() {
            val positions = linkageWheelView.currentPositions
            if (positions.size < 3) {
                return null
            }
            val province = addressData[positions[0]]
            val city = province.cities[positions[1]]
            val county = city.counties[positions[2]]
            return AddressBean(province.name, city.name, county)
        }
        set(value) {
            if (addressData.isEmpty()) {
                return
            }
            value ?: return
            val provinceIndex = addressData.indexOfFirst { value.provinceName == it.name }
            val province = addressData.getOrNull(provinceIndex) ?: return
            val cityIndex = province.cities.indexOfFirst { value.cityName == it.name }
            val city = province.cities.getOrNull(cityIndex) ?: return
            val countyIndex =
                city.counties.indexOf(value.countyName).takeIf { it in city.counties.indices } ?: 0
            linkageWheelView.currentPositions = listOf(provinceIndex, cityIndex, countyIndex)
        }


    companion object {
        private fun JSONArray.objects() = (0 until length()).map { getJSONObject(it) }
        private fun JSONArray.strings() = (0 until length()).map { getString(it) }

        @JvmStatic
        fun loadLocalData(context: Context, fileName: String = "address.json"): List<Province> {
            val text = context.assets.open(fileName).use { it.bufferedReader().readText() }
            return JSONArray(text)
                .objects()
                .map { province ->
                    val cities = province.getJSONArray("city").objects().map { city ->
                        val counties = city.getJSONArray("area").strings()
                        City(city.getString("name"), counties)
                    }
                    Province(province.getString("name"), cities)
                }

        }
    }

    data class AddressBean(
        val provinceName: String,
        val cityName: String,
        val countyName: String,
    ) : Serializable {
        constructor(address: Address) : this(
            address.adminArea ?: "",
            address.locality ?: "",
            address.subLocality ?: ""
        )

        override fun toString() = "${provinceName}${cityName}${countyName}"

    }

    data class Province(
        val name: String,
        val cities: List<City>,
    ) : Serializable

    data class City(
        val name: String,
        val counties: List<String>,
    ) : Serializable

}

fun LinkageWheelView.setupAddressChooseModule(currentData: List<AddressChooseModule.Province>) =
    AddressChooseModule(this, currentData)


