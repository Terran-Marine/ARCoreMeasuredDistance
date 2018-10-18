package com.gj.arcoredraw

import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3

/**
 *Created by GJ
 *on 2018/10/18 -上午 9:33
 */
class FaceToCameraNode : Node() {
    override fun onUpdate(p0: FrameTime?) {
        scene?.let { scene ->
            val cameraPosition = scene.camera.worldPosition
            val nodePosition = this@FaceToCameraNode.worldPosition
            val direction = Vector3.subtract(cameraPosition, nodePosition)
            this@FaceToCameraNode.worldRotation = Quaternion.lookRotation(direction, Vector3.up())
        }
    }
}