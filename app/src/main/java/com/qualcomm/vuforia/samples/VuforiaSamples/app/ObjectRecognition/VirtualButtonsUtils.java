package com.qualcomm.vuforia.samples.VuforiaSamples.app.ObjectRecognition;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by abdallah on 25/08/15.
 */
public class VirtualButtonsUtils {

    // Enumeration for masking button indices into single integer:
    public static final int BUTTON_1 = 1;
    public static final int BUTTON_2 = 2;
    public static final String BUTTON_1_NAME = "moveLeft";
    public static final String BUTTON_2_NAME = "moveRight";
    public static final int NUM_BUTTONS = 2;


    public static  int getIndexByName(String name) {
        return name.equalsIgnoreCase(BUTTON_1_NAME) ? 0 : 1;
    }


    public static Buffer fillBuffer(float[] array)
    {
        // Convert to floats because OpenGL doesnt work on doubles, and manually
        // casting each input value would take too much time.
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * array.length); // each
        // float
        // takes 4
        // bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (float d : array)
            bb.putFloat(d);
        bb.rewind();

        return bb;

    }
}
