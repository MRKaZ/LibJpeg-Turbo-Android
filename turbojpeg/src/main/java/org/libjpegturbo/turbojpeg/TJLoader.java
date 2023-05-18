package org.libjpegturbo.turbojpeg;

/**
 * @Project LibJpeg-Turbo-Android
 * @Class TJLoader
 * @Author MRKaZ
 * @Since 4:42 PM, 5/17/2023
 * @Origin Taprobana (LK)
 * @Copyright (c) 2023 MRKaZ. All rights reserved.
 */
public final class TJLoader {
    public static void load(){
        // Load generated `libjpeg-turbo` shared libraries
        System.loadLibrary("turbojpeg");
        System.loadLibrary("jpeg");
        // Load `libjpeg-turbo` native interface
        System.loadLibrary("turbojpeg-jni");
    }
}
