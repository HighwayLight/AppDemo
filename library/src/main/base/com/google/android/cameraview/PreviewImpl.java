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

import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;


/**
 * Encapsulates all the operations related to camera preview in a backward-compatible manner.
 */
abstract class PreviewImpl {

    interface Callback {
        // surface发生了变动
        void onSurfaceChanged();
    }

    private Callback mCallback;
    // 预览视图宽度
    private int mWidth;
    // 预览视图高度
    private int mHeight;

    void setCallback(Callback callback) {
        mCallback = callback;
    }

    abstract Surface getSurface();
    // 获取实际的渲染View
    abstract View getView();
    // 输出源
    abstract Class getOutputClass();
    // 预览方向
    abstract void setDisplayOrientation(int displayOrientation);
    // 渲染视图是否达到可用状态
    abstract boolean isReady();
    //  分发surface 的更变
    protected void dispatchSurfaceChanged() {
        mCallback.onSurfaceChanged();
    }
    // 主要是为了由SurfaceView渲染的情况
    SurfaceHolder getSurfaceHolder() {
        return null;
    }
    // 主要是为了由TextureView渲染的情况
    Object getSurfaceTexture() {
        return null;
    }
    // 设置缓冲区大小
    void setBufferSize(int width, int height) {
    }

    void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    int getWidth() {
        return mWidth;
    }

    int getHeight() {
        return mHeight;
    }

}
