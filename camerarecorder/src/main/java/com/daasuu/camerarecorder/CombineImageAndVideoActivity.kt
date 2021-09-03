package com.daasuu.camerarecorder

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.jaiselrahman.filepicker.model.MediaFile
import com.daasuu.camerarecorder.BaseActivity
import com.kalasa.library.R
import com.simform.videooperations.*
import java.util.concurrent.CompletableFuture.runAsync
import kotlinx.android.synthetic.main.activity_merge_image_and_video.btnCombine
import kotlinx.android.synthetic.main.activity_merge_image_and_video.btnImagePath
import kotlinx.android.synthetic.main.activity_merge_image_and_video.btnVideoPath
import kotlinx.android.synthetic.main.activity_merge_image_and_video.edtSecond
import kotlinx.android.synthetic.main.activity_merge_image_and_video.mProgressView
import kotlinx.android.synthetic.main.activity_merge_image_and_video.tvInputPathImage
import kotlinx.android.synthetic.main.activity_merge_image_and_video.tvInputPathVideo
import kotlinx.android.synthetic.main.activity_merge_image_and_video.tvOutputPath

class CombineImageAndVideoActivity : BaseActivity(R.layout.activity_merge_image_and_video, R.string.merge_image_and_video) {
    private var isInputVideoSelected = false
    private var isWaterMarkImageSelected = false

    override fun initialization() {
        btnVideoPath.setOnClickListener(this)
        btnImagePath.setOnClickListener(this)
        btnCombine.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVideoPath -> {
                Common.selectFile(
                    this,
                    maxSelection = 1,
                    isImageSelection = false,
                    isAudioSelection = false
                )
            }
            R.id.btnImagePath -> {
                Common.selectFile(
                    this,
                    maxSelection = 1,
                    isImageSelection = true,
                    isAudioSelection = false
                )
            }
            R.id.btnCombine -> {
                when {
                    !isInputVideoSelected -> {
                        Toast.makeText(
                            this,
                            getString(R.string.input_video_validate_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    !isWaterMarkImageSelected -> {
                        Toast.makeText(
                            this,
                            getString(R.string.input_image_validate_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    TextUtils.isEmpty(edtSecond.text.toString().trim()) || edtSecond.text.toString()
                        .trim().toInt() == 0 -> {
                        Toast.makeText(
                            this,
                            getString(R.string.please_enter_second),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        processStart()
                        combineImageAndVideoProcess()
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    override fun selectedFiles(mediaFiles: List<MediaFile>?, fileRequestCode: Int) {
        when (fileRequestCode) {
            Common.VIDEO_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    tvInputPathVideo.text = mediaFiles[0].path
                    isInputVideoSelected = true
                    runAsync {
                        retriever = MediaMetadataRetriever()
                        retriever?.setDataSource(tvInputPathVideo.text.toString())
                        val bit = retriever?.frameAtTime
                        width = bit?.width
                        height = bit?.height
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.video_not_selected_toast_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            Common.IMAGE_FILE_REQUEST_CODE -> {
                if (mediaFiles != null && mediaFiles.isNotEmpty()) {
                    tvInputPathImage.text = mediaFiles[0].path
                    isWaterMarkImageSelected = true
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.image_not_selected_toast_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun combineImageAndVideoProcess() {
        val outputPath = Common.getFilePath(this, Common.VIDEO)
        val paths = ArrayList<Paths>()
        val videoPaths1 = Paths()
        videoPaths1.filePath = tvInputPathImage.text.toString()
        videoPaths1.isImageFile = true
        val videoPaths2 = Paths()
        videoPaths2.filePath = tvInputPathVideo.text.toString()
        videoPaths2.isImageFile = false
        paths.add(videoPaths1)
        paths.add(videoPaths2)
        val query = combineImagesAndVideos(paths,540,720,"2",outputPath)
        CallBackOfQuery().callQuery(this, query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                tvOutputPath.text = logMessage.text
            }
            override fun statisticsProcess(statistics: Statistics) {
                super.statisticsProcess(statistics)
            }
            override fun success() {
                tvOutputPath.text = String.format(getString(R.string.output_path), outputPath)
                processStop()
            }
            override fun cancel() {
                processStop()
            }
            override fun failed() {
                tvOutputPath.text = "unsuccess"
                processStop()
            }
        })
    }
            private fun processStop() {
                runOnUiThread {
                    btnVideoPath.isEnabled = true
                    btnImagePath.isEnabled = true
                    btnCombine.isEnabled = true
                    mProgressView.visibility = View.GONE
                }
            }
            private fun processStart() {
                btnVideoPath.isEnabled = false
                btnImagePath.isEnabled = false
                btnCombine.isEnabled = false
                mProgressView.visibility = View.VISIBLE
            }
        }
fun combineImagesAndVideos(paths: ArrayList<Paths>, width: Int?, height: Int?, second: String, output: String): Array<String> {
    val inputs: ArrayList<String> = ArrayList()
    for (i in 0 until paths.size) {
        //for input
        if (paths[i].isImageFile) {
            inputs.add("-loop")
            inputs.add("1")
            inputs.add("-framerate")
            inputs.add("24")
            inputs.add("-t")
            inputs.add(second)
            inputs.add("-i")
            inputs.add(paths[i].filePath)
        } else {
            inputs.add("-i")
            inputs.add(paths[i].filePath)
        }
    }

    var query: String? = ""
    var queryAudio: String? = ""
    for (i in 0 until paths.size) {
        query = query?.trim()
        query += "[" + i + ":v]scale=${width}x${height},setdar=$width/$height[" + i + "v];"

        queryAudio = queryAudio?.trim()
        queryAudio += if (paths[i].isImageFile) {
            "[" + i + "v][" + paths.size + ":a]"
        } else {
            "[" + i + "v][" + i + ":a]"
        }
    }
    return getResult(inputs, query, queryAudio, paths, output)
}
private fun getResult(inputs: java.util.ArrayList<String>, query: String?, queryAudio: String?, paths: ArrayList<Paths>, output: String): Array<String> {
    inputs.apply {
        add("-f")
        add("lavfi")
        add("-t")
        add("-r")
        add("24")
        add("0.1")
        add("-i")
        add("anullsrc")
        add("-filter_complex")
        add(query + queryAudio + "concat=n=" + paths.size + ":v=1:a=1 [v][a]")
        add("-map")
        add("[v]")
        add("-map")
        add("[a]")
        add("-preset")
        add("ultrafast")
        add(output)
    }
    return inputs.toArray(arrayOfNulls<String>(inputs.size))
}