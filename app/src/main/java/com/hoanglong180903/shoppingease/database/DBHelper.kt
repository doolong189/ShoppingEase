package com.hoanglong180903.shoppingease.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hoanglong180903.shoppingease.model.Converters
import com.hoanglong180903.shoppingease.model.Product

@TypeConverters(Converters::class)
@Database(entities = [Product::class] , version = 2 , exportSchema = true)
abstract class DBHelper : RoomDatabase(){

    abstract fun getDao() : DAO

    companion object{
        @Volatile
        private var INSTANCE : DBHelper? = null
        fun getDatabase(context : Context) : DBHelper{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DBHelper::class.java,
                    "shopping_database"
                ).allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}