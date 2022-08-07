package com.ala158.magicpantry.arrayAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ala158.magicpantry.R
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.NotificationWithIngredients
import java.util.*

class NotificationsListArrayAdapter(
    private val context: Context,
    private var notificationList: List<NotificationWithIngredients>
) : BaseAdapter() {

    override fun getCount(): Int {
        return notificationList.size
    }

    override fun getItem(position: Int): Any {
        return notificationList[position]
    }

    override fun getItemId(position: Int): Long {
        return notificationList[position].notification.notificationId
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.list_item_notification, null)

        val descriptionTextView = view.findViewById<TextView>(R.id.notification_description)
        val timestampTextView = view.findViewById<TextView>(R.id.notification_timestamp)
        val iconImageView = view.findViewById<ImageView>(R.id.notification_icon)

        val selectedNotification = notificationList[position]

        if (!selectedNotification.notification.isRead) {
            iconImageView.visibility = View.VISIBLE
        } else {
            iconImageView.visibility = View.INVISIBLE
        }

        descriptionTextView.text = selectedNotification.notification.description
        timestampTextView.text = setupDateAge(selectedNotification.notification.date)

        return view
    }

    fun replace(newNotifications: List<NotificationWithIngredients>) {
        notificationList = newNotifications
    }

    private fun setupDateAge(prevDate: Calendar): String {
        val currDate = Calendar.getInstance()
        val numericValue: Int
        var timeUnit: String
        val months = currDate.get(Calendar.MONTH) - prevDate.get(Calendar.MONTH)
        val weeks = currDate.get(Calendar.WEEK_OF_MONTH) - prevDate.get(Calendar.WEEK_OF_MONTH)
        val days = currDate.get(Calendar.DAY_OF_MONTH) - prevDate.get(Calendar.DAY_OF_MONTH)
        val hours = currDate.get(Calendar.HOUR_OF_DAY) - prevDate.get(Calendar.HOUR_OF_DAY)
        val minutes = currDate.get(Calendar.MINUTE) - prevDate.get(Calendar.MINUTE)

        // Take the largest amount of time to display timestamp
        if (months > 0) {
            numericValue = months
            timeUnit = "month"
        } else if (weeks > 0) {
            numericValue = weeks
            timeUnit = "week"
        } else if (days > 0) {
            numericValue = days
            timeUnit = "day"
        } else if (hours > 0) {
            numericValue = hours
            timeUnit = "hour"
        } else if (minutes > 0) {
            numericValue = minutes
            timeUnit = "minute"
        } else {
            numericValue = 1
            timeUnit = "minute"
        }

        if (numericValue > 1) {
            timeUnit += "s"
        }

        return "$numericValue $timeUnit ago"
    }

}