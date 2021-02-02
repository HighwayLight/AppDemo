/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.cameraview;

import android.view.View;

import java.util.Set;

abstract class CameraViewImpl {
    // 相机基础事件回调
    protected final Callback mCallback;
    // 渲染视图
    protected final PreviewImpl mPreview;

    CameraViewImpl(Callback callback, PreviewImpl preview) {
        mCallback = callback;
        mPreview = preview;
    }
    // 获取渲染视图
    View getView() {
        return mPreview.getView();
    }

    /**
     * @return {@code true} if the implementation was able to start the camera session.
     *  启动相机
     */
    abstract boolean start();
    // 暂停相机
    abstract void stop();
    // 相机使用状态
    abstract boolean isCameraOpened();
    // 设置使用哪一个相机，简单如前置相机、后置相机
    abstract void setFacing(int facing);
    // 获取当前相机标识
    abstract int getFacing();
    // 获取相机支持的预览比例
    abstract Set<AspectRatio> getSupportedAspectRatios();

    /**
     * @return {@code true} if the aspect ratio was changed.
     * 设置拍摄照片比例
     */
    abstract boolean setAspectRatio(AspectRatio ratio);
    // 获取相机当前摄照片比例
    abstract AspectRatio getAspectRatio();
    // 设置自动聚焦
    abstract void setAutoFocus(boolean autoFocus);
    // 获取自动聚焦
    abstract boolean getAutoFocus();
    // 设置闪光状态
    abstract void setFlash(int flash);
    // 获取闪光状态
    abstract int getFlash();
    // 获取静态图片，即拍照
    abstract void takePicture();
    // 设置相机方向
    abstract void setDisplayOrientation(int displayOrientation);
    // 相机基础回调接口
    interface Callback {
        // 相机已打开
        void onCameraOpened();
        // 相机已关闭
        void onCameraClosed();
        // 相机获取到静态图片
        void onPictureTaken(byte[] data);

    }

}
