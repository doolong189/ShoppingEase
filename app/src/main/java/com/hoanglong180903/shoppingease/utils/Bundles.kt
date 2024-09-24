package com.hoanglong180903.shoppingease.utils

import android.os.Bundle
import com.hoanglong180903.shoppingease.model.User

class Bundles {
    companion object {
        fun bundleData(model: User, bundle: Bundle) {
            bundle.putString("name", model.name)
            bundle.putString("email", model.email)
            bundle.putString("password",model.password)
            bundle.putString("userId", model._id)
            bundle.putString("profileImage", model.image)
        }
    }
}