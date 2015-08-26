package com.qualcomm.vuforia.samples.VuforiaSamples.app.ObjectRecognition;

import android.opengl.GLES20;

import com.qualcomm.vuforia.samples.SampleApplication.utils.LineShaders;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by abdallah on 25/08/15.
 */
public class ObjectTargetUtils {

    // Enumeration for masking button indices into single integer:
    public static final String MOVE_LEFT = "moveLeft";
    public static final String MOVE_RIGHT = "moveRight";
    public static String virtualButtonColors[] = {MOVE_LEFT, MOVE_RIGHT};

    public static Buffer fillBuffer(float[] array) {
        // Convert to floats because OpenGL doesnt work on doubles, and manually
        // casting each input value would take too much time.
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * array.length); // each
        // float
        // takes 4
        // bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for(float d : array)
            bb.putFloat(d);
        bb.rewind();

        return bb;

    }
    public static Buffer fillBuffer(double[] array) {
        // Convert to floats because OpenGL doesnt work on doubles, and manually
        // casting each input value would take too much time.
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * array.length); // each
        // float
        // takes 4
        // bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for(double d : array)
            bb.putFloat((float) d);
        bb.rewind();

        return bb;

    }


    public static void drawBorder(double[] vbVertices, float[] modelViewProjection) {

//         int vbShaderProgramID = 0;
//         int vbVertexHandle = 0;
//         int lineOpacityHandle = 0;
//         int lineColorHandle = 0;
//         int mvpMatrixButtonsHandle = 0;
//        vbShaderProgramID = SampleUtils.createProgramFromShaderSrc(LineShaders.LINE_VERTEX_SHADER, LineShaders.LINE_FRAGMENT_SHADER);
//        mvpMatrixButtonsHandle = GLES20.glGetUniformLocation(vbShaderProgramID, "modelViewProjectionMatrix");
//        vbVertexHandle = GLES20.glGetAttribLocation(vbShaderProgramID, "vertexPosition");
//        lineOpacityHandle = GLES20.glGetUniformLocation(vbShaderProgramID, "opacity");
//        lineColorHandle = GLES20.glGetUniformLocation(vbShaderProgramID, "color");
//        // Render frame around button
//        GLES20.glUseProgram(vbShaderProgramID);
//        GLES20.glVertexAttribPointer(vbVertexHandle, 3, GLES20.GL_FLOAT, false, 0, fillBuffer(vbVertices));
//        GLES20.glEnableVertexAttribArray(vbVertexHandle);
//        GLES20.glUniform1f(lineOpacityHandle, 1.0f);
//        GLES20.glUniform3f(lineColorHandle, 1.0f, 1.0f, 1.0f);
//        GLES20.glLineWidth(10);
//        GLES20.glUniformMatrix4fv(mvpMatrixButtonsHandle, 1, false, modelViewProjection, 0);
//        // We multiply by 8 because that's the number of vertices per
//        // button
//        // The reason is that GL_LINES considers only pairs. So some
//        // vertices
//        // must be repeated.
//        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 16);
//
//        SampleUtils.checkGLError("VirtualButtons drawButton");
//
//        GLES20.glDisableVertexAttribArray(vbVertexHandle);
//
//        GLES20.glUseProgram(0);
    }
}
