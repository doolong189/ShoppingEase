package com.hoanglong180903.shoppingease.utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions
import com.hoanglong180903.shoppingease.R
import kotlin.math.cos
import kotlin.math.sin


internal object CustomGoogleMap {
    val LA_LOCATION: LatLng = LatLng(21.138512, 105.912810)
    /**
     * In kilometers.
     */
    private const val EARTH_RADIUS = 6371

    fun createPolygonWithCircle(context: Context?, center: LatLng, radius: Int): PolygonOptions {
        return PolygonOptions()
//            .fillColor(ContextCompat.getColor(context!!, R.color.grey_500_transparent))
            .addAll(createOuterBounds())
            .addHole(createHole(center, radius))
            .strokeWidth(2f)
            .strokeColor(Color.BLUE)
    }

    private fun createOuterBounds(): ArrayList<LatLng?> {
        val delta = 0.01f

        return object : ArrayList<LatLng?>() {
            init {
                add(LatLng((90 - delta).toDouble(), (-180 + delta).toDouble()))
                add(LatLng(0.0, (-180 + delta).toDouble()))
                add(LatLng((-90 + delta).toDouble(), (-180 + delta).toDouble()))
                add(LatLng((-90 + delta).toDouble(), 0.0))
                add(LatLng((-90 + delta).toDouble(), (180 - delta).toDouble()))
                add(LatLng(0.0, (180 - delta).toDouble()))
                add(LatLng((90 - delta).toDouble(), (180 - delta).toDouble()))
                add(LatLng((90 - delta).toDouble(), 0.0))
                add(LatLng((90 - delta).toDouble(), (-180 + delta).toDouble()))
            }
        }
    }

    private fun createHole(center: LatLng, radius: Int): Iterable<LatLng> {
        val points = 50 // number of corners of inscribed polygon

        val radiusLatitude = Math.toDegrees((radius / EARTH_RADIUS.toFloat()).toDouble())
        val radiusLongitude = radiusLatitude / cos(Math.toRadians(center.latitude))

        val result: MutableList<LatLng> = ArrayList(points)

        val anglePerCircleRegion = 2 * Math.PI / points

        for (i in 0 until points) {
            val theta = i * anglePerCircleRegion
            val latitude = center.latitude + (radiusLatitude * sin(theta))
            val longitude = center.longitude + (radiusLongitude * cos(theta))

            result.add(LatLng(latitude, longitude))
        }

        return result
    }
}