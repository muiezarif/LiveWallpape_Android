package com.muiezarif.livewallpaper.services

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.muiezarif.livewallpaper.R
import com.muiezarif.livewallpaper.views.MyAnalogClock

import java.util.*

class AnalogClockWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return AnalogClockWallpaperEngine()
    }

    private inner class AnalogClockWallpaperEngine : WallpaperService.Engine() {
        private val handler = Handler()
        private val drawRunner = Runnable { draw() }
        private var paint: Paint? = null
        private var colors = getString(R.string.string_color_black)
        private var width = 0
        private var height = 0
        private var visible = true
        private var clock: MyAnalogClock? = null

        init {
            paint = Paint(Paint.ANTI_ALIAS_FLAG)
            clock = MyAnalogClock(applicationContext)
            handler.post(drawRunner)
        }

        private fun draw() {
            val holder = surfaceHolder
            val canvas: Canvas?
            try { // IllegalArgument Exception
                canvas = holder.lockCanvas()
                canvas?.let { draw(it) }
                if (canvas != null) holder.unlockCanvasAndPost(canvas)
            } catch (e: Exception) {
                e.message
            }


            handler.removeCallbacks(drawRunner)

            if (visible) {
                handler.postDelayed(drawRunner, 200)
            }
        }

        private fun draw(canvas: Canvas) {
            clock?.config((width / 2).toFloat(), (height / 2).toFloat(), (width), Date(), paint, colors)
            clock?.draw(canvas)

        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            if (width>height){
                this.width = height
                this.height = width/2
            }else{
                this.width = width
                this.height = height
            }
            super.onSurfaceChanged(holder, format, width, height)

        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                handler.post(drawRunner)
            } else {
                handler.removeCallbacks(drawRunner)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            this.visible = false
            handler.removeCallbacks(drawRunner)
        }
    }
}