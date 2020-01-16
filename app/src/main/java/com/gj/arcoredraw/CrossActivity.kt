package com.gj.arcoredraw

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.blankj.utilcode.util.ToastUtils
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import kotlinx.android.synthetic.main.activity_cross.*

class CrossActivity : AppCompatActivity() {
    private val dataArray = arrayListOf<AnchorInfoBean>()
    private val sphereNodeArray = arrayListOf<Node>()

    private lateinit var firstAnchor: AnchorNode


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)// 设置全屏
        setContentView(R.layout.activity_cross)
        initView()
    }

    private fun initView() {
        initAr()
    }

    private fun initAr() {
        (UI_ArSceneView as MyArFragment).setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchorInfoBean = AnchorInfoBean("", hitResult.createAnchor(), 0.0)
            dataArray.add(anchorInfoBean)

            when (dataArray.size) {
                1 -> {
                    //第一个点
                    firstAnchor = AnchorNode(hitResult.createAnchor())
                    firstAnchor.setParent((UI_ArSceneView as MyArFragment).arSceneView.scene)
                    MaterialFactory.makeOpaqueWithColor(this@CrossActivity, com.google.ar.sceneform.rendering.Color(0.33f, 0.87f, 0f))
                            .thenAccept { material ->
                                val sphere = ShapeFactory.makeSphere(0.02f, Vector3.zero(), material)
                                sphereNodeArray.add(Node().apply {
                                    setParent(firstAnchor)
                                    localPosition = Vector3.zero()
                                    renderable = sphere
                                })
                            }
                }
                2 -> {
                    //第二个点
                    val endAnchor = dataArray[dataArray.size - 1].anchor
                    val startAnchor = dataArray[dataArray.size - 2].anchor


                    val startPose = endAnchor.pose
                    val endPose = startAnchor.pose
                    val dx = startPose.tx() - endPose.tx()
                    val dy = startPose.ty() - endPose.ty()
                    val dz = startPose.tz() - endPose.tz()

                    anchorInfoBean.length = Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble())

                    drawLine(startAnchor, endAnchor, anchorInfoBean.length)


                }
                else -> {
                    ToastUtils.showShort("目前就只能点两个点")
                }
            }
        }
    }

    private fun drawLine(firstAnchor: Anchor, secondAnchor: Anchor, length: Double) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val firstAnchorNode = AnchorNode(firstAnchor)

            val secondAnchorNode = AnchorNode(secondAnchor)

            firstAnchorNode.setParent((UI_ArSceneView as MyArFragment).arSceneView.scene)
            secondAnchorNode.setParent((UI_ArSceneView as MyArFragment).arSceneView.scene)

            MaterialFactory.makeOpaqueWithColor(this@CrossActivity, com.google.ar.sceneform.rendering.Color(0.53f, 0.92f, 0f))
                    .thenAccept { material ->
                        val sphere = ShapeFactory.makeSphere(0.02f, Vector3(0.0f, 0.0f, 0.0f), material)
                        sphereNodeArray.add(Node().apply {
                            setParent(secondAnchorNode)
                            localPosition = Vector3.zero()
                            renderable = sphere
                        })
                    }

            val firstWorldPosition = firstAnchorNode.worldPosition
            val secondWorldPosition = secondAnchorNode.worldPosition

            val difference = Vector3.subtract(firstWorldPosition, secondWorldPosition)
            val directionFromTopToBottom = difference.normalized()
            val rotationFromAToB = Quaternion.lookRotation(directionFromTopToBottom, Vector3.up())

            //中间的竖线
            MaterialFactory.makeOpaqueWithColor(this@CrossActivity, com.google.ar.sceneform.rendering.Color(0.33f, 0.87f, 0f))
                    .thenAccept { material ->
                        val lineMode = ShapeFactory.makeCube(Vector3(0.01f, 0.01f, difference.length()), Vector3.zero(), material)
                        Node().apply {
                            setParent(firstAnchorNode)
                            renderable = lineMode
                            worldPosition = Vector3.add(firstWorldPosition, secondWorldPosition).scaled(0.5f)
                            worldRotation = rotationFromAToB
                        }
                    }


            //两条垂直于中间竖线的 平行线
            val crossDifference = Vector3.cross(firstWorldPosition, secondWorldPosition)
            val crossDifferenceVector3 = crossDifference.normalized()

            val rotationFromCross = Quaternion.lookRotation(crossDifferenceVector3, Vector3.up())

            MaterialFactory.makeOpaqueWithColor(this@CrossActivity, com.google.ar.sceneform.rendering.Color(0.33f, 0.57f, 0.35f))
                    .thenAccept { material ->
                        val lineMode = ShapeFactory.makeCube(Vector3(0.01f, 0.01f, crossDifference.length() * 3), Vector3.zero(), material)
                        //过第一个点平行线
                        Node().apply {
                            setParent(firstAnchorNode)
                            renderable = lineMode
                            worldRotation = rotationFromCross
                        }
                        //过第二个点的平行线
                        Node().apply {
                            setParent(secondAnchorNode)
                            renderable = lineMode
                            worldRotation = rotationFromCross
                        }
                    }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (UI_ArSceneView as MyArFragment).onDestroy()
    }
}