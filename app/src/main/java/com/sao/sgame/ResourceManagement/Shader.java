package com.sao.sgame.ResourceManagement;

import android.opengl.GLES20;
import android.util.Log;

public class Shader {
    public static final String TAG = "Shader";

    /**
     * Carga los shaders necesarios
     * @param type Tipo de shader a pasar GLES20.GL_VERTEX_SHADER/GLES20.GL_FRAGMENT_SHADER
     * @param shaderCode String con el shader a cargar
     * @return el manejador del shader
     */
    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        if(shader==0)
        {
            Log.e(TAG, "Could not create shader " + type + ":");
            Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
            return shader;
        }
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + type + ":");
            Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        return shader;
    }
}
