/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.qualcomm.vuforia.samples.VideoPlayback.app.VideoPlayback;

public class VideoPlaybackShaders {

    public static final String VIDEO_PLAYBACK_VERTEX_SHADER = " \n" + "attribute vec4 vertexPosition; \n" + "attribute vec4 vertexNormal; \n" + "attribute vec2 vertexTexCoord; \n" + "varying vec2 texCoord; \n" + "varying vec4 normal; \n" + "uniform mat4 modelViewProjectionMatrix; \n" + "\n" + "void main() \n" + "{ \n" + "   gl_Position = modelViewProjectionMatrix * vertexPosition; \n" + "   normal = vertexNormal; \n" + "   texCoord = vertexTexCoord; \n" + "} \n";
    
    /*
     * 
     * IMPORTANT:
     * 
     * The SurfaceTexture functionality from ICS provides the video frames from
     * the movie in an unconventional format. So we cant use Texture2D but we
     * need to use the ExternalOES extension.
     * 
     * Two things that are important in the shader below. The first is the
     * extension declaration (first line). The second is the type of the
     * texSamplerOES uniform.
     */

    public static final String VIDEO_PLAYBACK_FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require \n" +
            "precision mediump float; \n" +
            "varying vec2 texCoord; \n" +
            "uniform samplerExternalOES texSamplerOES; \n" +
            "void main() \n" +
            "{ \n" +
            "   vec4 texColor = texture2D(texSamplerOES, texCoord); \n" +
            "   vec3 borderColor = vec3(1.0 ,1.0, 1.0); \n" +
            "   float u = 2.0*abs(texCoord.x - 0.5);\n" +
            "   float v = 2.0*abs(texCoord.y - 0.5);\n" +
            "   gl_FragColor = texColor; \n" +
            "   float borderWidth = 0.01; \n" +
            "   float borderHeight = 0.01; \n" +
            "   float contentWidth = 1.0 - borderWidth; \n" +
            "   float contentHeight = 1.0 - borderHeight; \n" +
            "   gl_FragColor.rgb = ((u > contentWidth) || (v > contentHeight)) ? borderColor : gl_FragColor.rgb; \n" +
            "} \n";

}
