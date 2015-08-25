/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.qualcomm.vuforia.samples.VuforiaSamples.app.ObjectRecognition;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import com.qualcomm.vuforia.ImageTargetResult;
import com.qualcomm.vuforia.ObjectTarget;
import com.qualcomm.vuforia.Rectangle;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.qualcomm.vuforia.VirtualButton;
import com.qualcomm.vuforia.VirtualButtonResult;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.qualcomm.vuforia.samples.SampleApplication.utils.CubeObject;
import com.qualcomm.vuforia.samples.SampleApplication.utils.CubeShaders;
import com.qualcomm.vuforia.samples.SampleApplication.utils.LineShaders;
import com.qualcomm.vuforia.samples.SampleApplication.utils.LoadingDialogHandler;
import com.qualcomm.vuforia.samples.SampleApplication.utils.MeshObject;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleUtils;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Texture;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


// The renderer class for the ImageTargets sample. 
public class ObjectTargetRenderer implements GLSurfaceView.Renderer {
    private static final String LOGTAG = "ObjectTargetRenderer";

    private SampleApplicationSession vuforiaAppSession;
    private ObjectTargets mActivity;
    private MeshObject mMeshObject;
    private Vector<Texture> mTextures;
    boolean mIsActive = false;

    // // OpenGL ES 2.0 specific (3D Model):
    private int shaderProgramID;
    private int vertexHandle;
    private int textureCoordHandle;
    private int texSampler2DHandle;
    private int normalHandle;
    private int mvpMatrixHandle;

    // OpenGL ES 2.0 specific (Virtual Buttons):
    private int vbShaderProgramID = 0;
    private int vbVertexHandle = 0;
    private int lineOpacityHandle = 0;
    private int lineColorHandle = 0;
    private int mvpMatrixButtonsHandle = 0;
    private int mCurrentTextureIndex = 0;
    private int mPreviousTextureIndex;

    // -------------------------------------->

    public ObjectTargetRenderer(ObjectTargets activity, SampleApplicationSession session) {
        mActivity = activity;
        vuforiaAppSession = session;
    }


    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        if(!mIsActive) return;

        // Call our function to render content
        renderFrame();
    }


    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");

        initRendering();

        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();
    }


    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);
    }


    // --------------------------------------> Init

    // Function for initializing the renderer.
    private void initRendering() {
        mMeshObject = new CubeObject();

        // Define clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f : 1.0f);

        // Now generate the OpenGL texture objects and add settings
        for(Texture t : mTextures) {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, t.mWidth, t.mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, t.mData);
        }

        SampleUtils.checkGLError("ObjectTarget GLInitRendering");

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f : 1.0f);

        //  OpenGL setup for 3D Model
        shaderProgramID = SampleUtils.createProgramFromShaderSrc(CubeShaders.CUBE_MESH_VERTEX_SHADER, CubeShaders.CUBE_MESH_FRAGMENT_SHADER);
        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexPosition");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexTexCoord");
        normalHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexNormal");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID, "modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID, "texSampler2D");

        // OpenGL setup for Virtual Buttons
        vbShaderProgramID = SampleUtils.createProgramFromShaderSrc(LineShaders.LINE_VERTEX_SHADER, LineShaders.LINE_FRAGMENT_SHADER);
        mvpMatrixButtonsHandle = GLES20.glGetUniformLocation(vbShaderProgramID, "modelViewProjectionMatrix");
        vbVertexHandle = GLES20.glGetAttribLocation(vbShaderProgramID, "vertexPosition");
        lineOpacityHandle = GLES20.glGetUniformLocation(vbShaderProgramID, "opacity");
        lineColorHandle = GLES20.glGetUniformLocation(vbShaderProgramID, "color");

        // Hide the Loading Dialog
        mActivity.loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);

    }

    // --------------------------------------> Render Frame

    // The render function.
    private void renderFrame() {
        // Clear color and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Get the state from Vuforia and mark the beginning of a rendering
        // section
        State state = Renderer.getInstance().begin();

        // Explicitly render the Video Background
        Renderer.getInstance().drawVideoBackground();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // We must detect if background reflection is active and adjust the
        // culling direction.
        // If the reflection is active, this means the post matrix has been
        // reflected as well,
        // therefore counter standard clockwise face culling will result in
        // "inside out" models.
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        if(Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON) {
            GLES20.glFrontFace(GLES20.GL_CW); // Front camera
        }
        else {
            GLES20.glFrontFace(GLES20.GL_CCW); // Back camera
        }

        // Did we find any trackables this frame?

        if(state.getNumTrackableResults() > 0) {
            Log.i(LOGTAG, "Num of Trackables : " + state.getNumTrackableResults());
            for(int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
                TrackableResult trackableResult = state.getTrackableResult(tIdx);

                printUserData(trackableResult.getTrackable());

                // Render Virtual Btns
                renderVirtualBtns(trackableResult);

                Texture thisTexture = mTextures.get(0);//mCurrentTextureIndex

                if(mPreviousTextureIndex != mCurrentTextureIndex) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, "Button Pressed " + mCurrentTextureIndex, 1000).show();
                        }
                    });
                }
                mPreviousTextureIndex = mCurrentTextureIndex;

                // Render 3D model
                render3DModel(trackableResult, thisTexture);
            }
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        Renderer.getInstance().end();
    }

    // ------>

    private void renderVirtualBtns(TrackableResult trackableResult) {
        float[] modelViewMatrix = Tool.convertPose2GLMatrix(trackableResult.getPose()).getData();
        // Set transformations:
        float[] modelViewProjection = new float[16];
        Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession.getProjectionMatrix().getData(), 0, modelViewMatrix, 0);

        ImageTargetResult imageTargetResult = (ImageTargetResult) trackableResult;

        int numVirtualButtons = imageTargetResult.getNumVirtualButtons();
        Log.i(LOGTAG, "Num Of Btns : " + numVirtualButtons);

        float vbVertices[] = new float[numVirtualButtons * 24];
        short vbCounter = 0;

        // Iterate through this targets virtual buttons:
        for(int i = 0; i < numVirtualButtons; ++i) {
            VirtualButtonResult buttonResult = imageTargetResult.getVirtualButtonResult(i);
            VirtualButton button = buttonResult.getVirtualButton();

            int buttonIndex = 0;
            // Run through button name array to find button index
            for(int j = 0; j < numVirtualButtons; ++j) {
                if(button.getName().compareTo(VirtualButtonsUtils.virtualButtonColors[j]) == 0) {
                    buttonIndex = j;
                    break;
                }
            }

            // If the button is pressed, than use this texture:
            if(buttonResult.isPressed()) {
                mCurrentTextureIndex = buttonIndex + 1;
            }

            // If the button is pressed, than use this texture:
            if(buttonResult.isPressed()) {
                mCurrentTextureIndex = buttonIndex + 1;
                Log.i(LOGTAG, "Button Pressed " + mCurrentTextureIndex);
            }

            Rectangle vbRectangle = (Rectangle) button.getArea();

            // We add the vertices to a common array in order to have one
            // single
            // draw call. This is more efficient than having multiple
            // glDrawArray calls
            vbVertices[vbCounter] = vbRectangle.getLeftTopX();
            vbVertices[vbCounter + 1] = vbRectangle.getLeftTopY();
            vbVertices[vbCounter + 2] = 0.0f;
            vbVertices[vbCounter + 3] = vbRectangle.getRightBottomX();
            vbVertices[vbCounter + 4] = vbRectangle.getLeftTopY();
            vbVertices[vbCounter + 5] = 0.0f;
            vbVertices[vbCounter + 6] = vbRectangle.getRightBottomX();
            vbVertices[vbCounter + 7] = vbRectangle.getLeftTopY();
            vbVertices[vbCounter + 8] = 0.0f;
            vbVertices[vbCounter + 9] = vbRectangle.getRightBottomX();
            vbVertices[vbCounter + 10] = vbRectangle.getRightBottomY();
            vbVertices[vbCounter + 11] = 0.0f;
            vbVertices[vbCounter + 12] = vbRectangle.getRightBottomX();
            vbVertices[vbCounter + 13] = vbRectangle.getRightBottomY();
            vbVertices[vbCounter + 14] = 0.0f;
            vbVertices[vbCounter + 15] = vbRectangle.getLeftTopX();
            vbVertices[vbCounter + 16] = vbRectangle.getRightBottomY();
            vbVertices[vbCounter + 17] = 0.0f;
            vbVertices[vbCounter + 18] = vbRectangle.getLeftTopX();
            vbVertices[vbCounter + 19] = vbRectangle.getRightBottomY();
            vbVertices[vbCounter + 20] = 0.0f;
            vbVertices[vbCounter + 21] = vbRectangle.getLeftTopX();
            vbVertices[vbCounter + 22] = vbRectangle.getLeftTopY();
            vbVertices[vbCounter + 23] = 0.0f;
            vbCounter += 24;

        }


        // We only render if there is something on the array
        if(vbCounter > 0) {
            // Render frame around button
            GLES20.glUseProgram(vbShaderProgramID);
            GLES20.glVertexAttribPointer(vbVertexHandle, 3, GLES20.GL_FLOAT, false, 0, VirtualButtonsUtils.fillBuffer(vbVertices));
            GLES20.glEnableVertexAttribArray(vbVertexHandle);
            GLES20.glUniform1f(lineOpacityHandle, 1.0f);
            GLES20.glUniform3f(lineColorHandle, 1.0f, 1.0f, 1.0f);
            GLES20.glUniformMatrix4fv(mvpMatrixButtonsHandle, 1, false, modelViewProjection, 0);
            // We multiply by 8 because that's the number of vertices per
            // button
            // The reason is that GL_LINES considers only pairs. So some
            // vertices
            // must be repeated.
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, numVirtualButtons * 8);

            SampleUtils.checkGLError("VirtualButtons drawButton");

            GLES20.glDisableVertexAttribArray(vbVertexHandle);
        }
    }


    private void render3DModel(TrackableResult trackableResult, Texture thisTexture) {
        float[] modelViewMatrix = Tool.convertPose2GLMatrix(trackableResult.getPose()).getData();

        Trackable trackable = trackableResult.getTrackable();
        ObjectTarget objectTarget = (ObjectTarget) trackable;
        float[] objectSize = objectTarget.getSize().getData();

        // Matrix
        float[] modelViewProjection = new float[16];
        Matrix.scaleM(modelViewMatrix, 0, objectSize[0] / 2, objectSize[1] / 4, objectSize[2] / 2);
        Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession.getProjectionMatrix().getData(), 0, modelViewMatrix, 0);

        // Render 3D model
        GLES20.glUseProgram(shaderProgramID);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, mMeshObject.getVertices());
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, mMeshObject.getNormals());
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mMeshObject.getTexCoords());

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glEnableVertexAttribArray(textureCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, thisTexture.mTextureID[0]);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
        GLES20.glUniform1i(texSampler2DHandle, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mMeshObject.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT, mMeshObject.getIndices());

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glDisableVertexAttribArray(textureCoordHandle);
        SampleUtils.checkGLError("VirtualButtons renderFrame");
    }


    // -------------------------------------->

    private void printUserData(Trackable trackable) {
        String userData = (String) trackable.getUserData();
        Log.d(LOGTAG, "UserData:Retreived User Data	\"" + userData + "\"");
    }


    public void setTextures(Vector<Texture> textures) {
        mTextures = textures;

    }

}
