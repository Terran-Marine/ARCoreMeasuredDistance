package com.gj.arcoredraw

import android.icu.text.DecimalFormat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
//    private val anchorArray = arrayListOf<Anchor>()
//    private val dataTextArray = arrayListOf<String>()

    private val dataArray = arrayListOf<AnchorInfoBean>()
    private val lineNodeArray = arrayListOf<Node>()
    private val startNodeArray = arrayListOf<Node>()
    lateinit var dataAdapter: DataAdapter
    private var totalLength = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) //hunta 219
        initView()
    }

    private fun initView() {
        val decimalFormat = DecimalFormat("0.00")

        dataAdapter = DataAdapter(dataArray)

        UI_Reset.setOnClickListener {
            startNodeArray.mapIndexed { index, startNode ->
                dataArray[index].anchor.detach()
                startNode.removeChild(lineNodeArray[index])
                (UI_ArSceneView as MyArFragment).arSceneView.scene.removeChild(startNode)

            }

            dataArray.clear()
            lineNodeArray.clear()
            startNodeArray.clear()
            dataAdapter.notifyDataSetChanged()
            totalLength = 0.0
            UI_TotalText.text = ""
        }

        UI_DataRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        UI_DataRecyclerView.adapter = dataAdapter

        (UI_ArSceneView as MyArFragment).setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchorInfoBean = AnchorInfoBean("", hitResult.createAnchor(), 0.0)
            dataArray.add(anchorInfoBean)
            ToastUtils.showLong("已记录第${dataArray.size}个点")
            if (dataArray.size > 1) {
                val endAnchor = dataArray[dataArray.size - 1].anchor
                val startAnchor = dataArray[dataArray.size - 2].anchor

                drawLine(startAnchor, endAnchor)

                val startPose = endAnchor.pose
                val endPose = startAnchor.pose
                val dx = startPose.tx() - endPose.tx()
                val dy = startPose.ty() - endPose.ty()
                val dz = startPose.tz() - endPose.tz()

                anchorInfoBean.length = Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble())

                anchorInfoBean.dataText = "第${dataArray.size - 1}段,距离为${decimalFormat.format(anchorInfoBean.length)}m"

                totalLength += anchorInfoBean.length

                UI_TotalText.text = "共${dataArray.size - 1}段,总共长度${decimalFormat.format(totalLength)}m"
            } else {
                anchorInfoBean.dataText = "起始"
            }

            dataAdapter.notifyDataSetChanged()

//            when (pageState) {
//                0 -> {
//                    firstAnchor = hitResult.createAnchor()
//
//                    pageState = 1
//
//                    ToastUtils.showLong("已记录第一个点,点击屏幕 记录第二个点")
//                }
//
//                1 -> {
//                    val secondAnchor = hitResult.createAnchor()
//                    drawLine(firstAnchor, secondAnchor)
//
//                    val startPose = firstAnchor.pose
//                    val endPose = hitResult.hitPose
//
//                    val dx = startPose.tx() - endPose.tx()
//                    val dy = startPose.ty() - endPose.ty()
//                    val dz = startPose.tz() - endPose.tz()
//
//                    UI_distanceMeters.text = decimalFormat.format(Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble())).toString()
//
//                    ToastUtils.showLong("点击屏幕 清除当前测距")
//
//                    pageState = 2
//                }
//
//                2 -> {
//                    ToastUtils.showLong("清除成功")
//
//                    firstAnchor.detach()
//                    firstAnchorNode.removeChild(lineNode)
//                    (UI_ArSceneView as MyArFragment).arSceneView.scene.removeChild(firstAnchorNode)
//                    pageState = 0
//                }
//            }
        }
    }

//    private lateinit var firstAnchorNode: Node
//    private lateinit var lineNode: Node

    private fun drawLine(firstAnchor: Anchor, secondAnchor: Anchor) {
        val firstAnchorNode = AnchorNode(firstAnchor)
        startNodeArray.add(firstAnchorNode)

        val secondAnchorNode = AnchorNode(secondAnchor)

        firstAnchorNode.setParent((UI_ArSceneView as MyArFragment).arSceneView.scene)

        val firstWorldPosition = firstAnchorNode.worldPosition
        val secondWorldPosition = secondAnchorNode.worldPosition

        val difference = Vector3.subtract(firstWorldPosition, secondWorldPosition)
        val directionFromTopToBottom = difference.normalized()
        val rotationFromAToB = Quaternion.lookRotation(directionFromTopToBottom, Vector3.up())

        MaterialFactory.makeOpaqueWithColor(this@MainActivity, com.google.ar.sceneform.rendering.Color(0f, 191f, 255f))
                .thenAccept { material ->
                    val lineMode = ShapeFactory.makeCube(Vector3(0.01f, 0.01f, difference.length()), Vector3.zero(), material)
                    val lineNode = Node()
                    lineNode.setParent(firstAnchorNode)
                    lineNode.renderable = lineMode
                    lineNode.worldPosition = Vector3.add(firstWorldPosition, secondWorldPosition).scaled(0.5f)
                    lineNode.worldRotation = rotationFromAToB
                    lineNodeArray.add(lineNode)
                }
    }
}