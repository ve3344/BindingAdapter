package me.lwb.adapter.demo.ui.activity.wheel

import me.lwb.adapter.wheel.LinkageWheelView
import me.lwb.adapter.wheel.setData
import java.util.*

/**
 * Created by ve3344@qq.com.
 */
open class CalendarChooseModule(
    private val linkageWheelView: LinkageWheelView,
    chooseOptions: Options
) {

    var options = chooseOptions
        set(value) {
            field = value
            refresh()
        }

    init {
        //不允许出现，year,month,day=(true,false,true) 的情况
        require(!(options.year && !options.month && options.day)) { "Invalid mode" }
        refresh()
    }

    /**
     * 当前
     */
    var current: Calendar
        get() {
            val values = linkageWheelView.currentItems.map { (it as ValueWrapper).realValue }
            val calendar = Calendar.getInstance()
            var index = 0
            if (options.year && index < values.size) {
                calendar.set(Calendar.YEAR, values[index++])
            }
            if (options.month && index < values.size) {
                calendar.set(Calendar.MONTH, values[index++])
            }
            if (options.day && index < values.size) {
                calendar.set(Calendar.DAY_OF_MONTH, values[index])
            } else {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
            }
            return calendar
        }
        set(value) {
            val values: List<List<Int>> =
                linkageWheelView.wheels.map { it.items.map { (it as ValueWrapper).realValue } }

            val list: MutableList<Int> = ArrayList(3)
            var index = 0
            if (options.year) {
                list.add(values[index++].indexOf(value.get(Calendar.YEAR)).coerceAtLeast(0))
            }
            if (options.month) {
                list.add(values[index++].indexOf(value.get(Calendar.MONTH)).coerceAtLeast(0))
            }
            if (options.day) {
                list.add(values[index].indexOf(value.get(Calendar.DAY_OF_MONTH)).coerceAtLeast(0))
            }
            linkageWheelView.currentPositions = list
        }


    private fun refresh() {

        linkageWheelView.setData {
            val (maxYear, maxMonth, maxDay) = options.maxValue.flattenYmd()
            val (minYear, minMonth, minDay) = options.minValue.flattenYmd()

            if (options.year) {
                //年份
                provideData {
                    (maxYear downTo minYear).formatYear()
                }
            }
            if (options.month) {
                provideData {
                    //月份
                    if (it.size < 1) {
                        //没有年份的情况，默认12个月
                        return@provideData MONTH_DATA.formatMonth()
                    }
                    val currentYear = it[0].realValue//当前年份
                    when (currentYear) {
                        maxYear -> (0..maxMonth).formatMonth()//年份相同，不能大于最大月
                        minYear -> (minMonth..MONTH_DATA.last).formatMonth()//年份相同，不能小于最小月
                        else -> MONTH_DATA.formatMonth()
                    }
                }
            }
            if (options.day) {
                provideData {
                    if (it.size < 2) {
                        //默认31天
                        return@provideData (1..31).formatDay()
                    }
                    val currentYear = it[0].realValue//当前年份
                    val currentMonth = it[1].realValue//当前年份

                    val current = Calendar.getInstance().apply {
                        set(currentYear, currentMonth, 1)
                    }
                    when {
                        currentYear == maxYear && currentMonth == maxMonth -> (1..maxDay).formatDay()
                        currentYear == minYear && currentMonth == minMonth -> {
                            val max = current.getActualMaximum(Calendar.DAY_OF_MONTH)
                            (minDay..max).formatDay()
                        }
                        else -> {
                            val max = current.getActualMaximum(Calendar.DAY_OF_MONTH)
                            (1..max).formatDay()
                        }
                    }
                }
            }
        }
    }

    //格式化显示-----------
    //   值   -> 显示
    //年 2013 -> 2013
    //月 0~11 -> 1~12
    //日 1~31 -> 1~31
    private fun IntProgression.formatYear(): List<ValueWrapper> =
        map { ValueWrapper(options.yearFormatter.format(it), it) }

    private fun IntProgression.formatMonth(): List<ValueWrapper> =
        map { ValueWrapper(options.monthFormatter.format(it), it) }

    private fun IntProgression.formatDay(): List<ValueWrapper> =
        map { ValueWrapper(options.dayFormatter.format(it), it) }


    private fun Calendar.flattenYmd() =
        Triple(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH))

    private val LinkageWheelView.WheelWrapper.realValue get() = (selectedItem as ValueWrapper).realValue

    private class ValueWrapper(private val delegate: CharSequence, val realValue: Int) :
        CharSequence by delegate {
        override fun toString() = delegate.toString()
    }

    fun interface Formatter {
        fun format(value: Int): CharSequence
    }
    //----------

    data class Options(
        val year: Boolean = true,
        val month: Boolean = true,
        val day: Boolean = true,
        val maxValue: Calendar = Calendar.getInstance(),
        val minValue: Calendar = Calendar.getInstance().apply { add(Calendar.YEAR, -10) },
        var yearFormatter: Formatter = Formatter { it.toString() },
        var monthFormatter: Formatter = Formatter { (it + 1).toString() },
        var dayFormatter: Formatter = Formatter { it.toString() }
    )

    companion object {
        val MONTH_DATA: IntRange = (0..11)
    }
}

fun LinkageWheelView.setupCalendarChooseModule(options: CalendarChooseModule.Options = CalendarChooseModule.Options()) =
    CalendarChooseModule(this, options)