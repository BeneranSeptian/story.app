package com.seftian.storyapp.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.seftian.storyapp.R
import com.seftian.storyapp.data.local.UserDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class StoryWidget : AppWidgetProvider() {
    @Inject
    lateinit var localDb: UserDatabase
    private var timer: Timer? = null

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        startImageUpdateTimer(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        startImageUpdateTimer(context, null, null)
    }

    override fun onDisabled(context: Context) {
        stopImageUpdateTimer()
    }

    private fun startImageUpdateTimer(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        stopImageUpdateTimer()

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                updateWidgetImages(context, appWidgetManager, appWidgetIds)
            }
        }, 0, 5000)
    }

    private fun stopImageUpdateTimer() {
        timer?.cancel()
        timer = null
    }

    private fun updateWidgetImages(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        appWidgetIds?.let {
            for (appWidgetId in it) {
                CoroutineScope(Dispatchers.IO).launch {
                    val randomStory = localDb.dao. getRandomStory()
                    val views = RemoteViews(context.packageName, R.layout.story_widget)

                    views.setTextViewText(R.id.tv_widget, context.resources.getString(R.string.story_by, randomStory.name))

                    val requestOptions = RequestOptions()
                        .fitCenter()
                        .override(300, 300)
                        .error(R.drawable.ic_person)


                    withContext(Dispatchers.IO) {

                        Glide.with(context)
                            .asBitmap()
                            .load(randomStory.photoUrl)
                            .apply(requestOptions)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    views.setImageViewBitmap(R.id.img_widget, resource)
                                    appWidgetManager?.updateAppWidget(appWidgetId, views)
                                }

                                override fun onLoadFailed(errorDrawable: Drawable?) {
                                    val placeholderBitmap = BitmapFactory.decodeResource(
                                        context.resources,
                                        R.drawable.ic_person
                                    )
                                    views.setImageViewBitmap(R.id.img_widget, placeholderBitmap)
                                    appWidgetManager?.updateAppWidget(appWidgetId, views)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                }
                            })


                    }
                }
            }
        }
    }

}




