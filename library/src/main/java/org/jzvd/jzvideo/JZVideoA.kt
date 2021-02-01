package org.jzvd.jzvideo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import cn.jzvd.R
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

const val TAG = "JZVideo"


enum class State {
    IDLE, NORMAL, PREPARING, PREPARING_CHANGE_URL, PREPARING_PLAYING,
    PREPARED, PLAYING, PAUSE, COMPLETE, ERROR
}

enum class Screen {
    NORMAL, FULLSCREEN, TINY
}

class JZVideoA : RelativeLayout, View.OnClickListener {

    var state: State = State.IDLE

    lateinit var url: String
    lateinit var mediaInterfaceClass: KClass<*>
    var mediaInterface: JZMediaInterface? = null

    lateinit var surfaeView: JZSurfaceView
    var startBtn: ImageView? = null
    var progressTimer: Timer? = null

    constructor(ctx: Context) : super(ctx) {
        inflate(context, getLayout(), this)
        init()
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        inflate(context, getLayout(), this)
        init()
    }

    fun init() {
        surfaeView = findViewById(R.id.surface)
        startBtn = findViewById(R.id.start)
        startBtn?.setOnClickListener(this)

        startBtn?.setOnClickListener {
            prepare()
        }

    }

    override fun onClick(v: View?) {

    }

    fun setUp(
        url: String,
        mediaInterfaceClass: KClass<*>,
    ) {
        this.url = url
        this.mediaInterfaceClass = mediaInterfaceClass

        val mediaRef = Class.forName(mediaInterfaceClass.java.name).kotlin
        mediaInterface =
            mediaRef.createInstance() as JZMediaInterface//初始化和interface里面的MediaPlayer没有任何关系。
        surfaeView.prepareSurface(mediaInterface?)


    }

    /**
     * 就是startVideo()，和mediaPlayer的函数名一样,prepare表示开始
     */
    fun prepare() {
        mediaInterface?.prepare()//MediaPlayer开始工作


    }


    fun onProgress(progress: Int, position: Long, duration: Long) {

    }

    fun getLayout(): Int {
        return R.layout.jz_video_a
    }

    fun startGetProgressTimer() {
        progressTimer?.cancel()
        progressTimer = Timer()
        progressTimer?.schedule(object : TimerTask() {
            override fun run() {
                //取得播放进度，总时间，当前时间
                post {
                    val position: Long? = mediaInterface?.currentPosition
                    val duration: Long? = mediaInterface?.duration
                    if (position != null && duration != null) {//算个加减乘除 这么复杂吗
                        var pro: Int
                        if (duration?.equals(0L)) {
                            pro = 0
                        } else {
                            pro = position?.times(100)?.div(duration!!)?.toInt()!!
                        }
                        onProgress(pro, position, duration)
                    }
                }
            }
        }, 0, 300)
    }

}



