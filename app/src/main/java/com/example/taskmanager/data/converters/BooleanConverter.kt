package com.example.taskmanager.data.converters

import androidx.room.TypeConverter

class BooleanConverter {

    /**
     * Converter for Room.
     * @param b [Boolean] to convert.
     * @return 1 if [b] is true. 0 if [b] is false.
     */
    @TypeConverter
    fun booleanToInt(b: Boolean): Int {
        return if (b) {
            1
        } else {
            0
        }
    }

    /**
     * Converter for Room.
     * @param i [Int] to convert.
     * @return false if [i] == 0, true otherwise
     */
    @TypeConverter
    fun intToBoolean(i: Int): Boolean {
        return i != 0
    }

}