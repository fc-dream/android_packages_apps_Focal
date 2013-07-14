/**
 * Copyright (C) 2013 The CyanogenMod Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.cyanogenmod.nemesis.widgets;

import android.content.Context;
import android.hardware.Camera;
import android.view.View;

import org.cyanogenmod.nemesis.CameraManager;
import org.cyanogenmod.nemesis.R;
import org.cyanogenmod.nemesis.SettingsStorage;

/**
 * Shutter speed setup widget
 */
public class ShutterSpeedWidget extends WidgetBase {
    private static final String KEY_PARAMETER = "shutter-speed";
    private static final String KEY_MAX_PARAMETER = "max-shutter-speed";
    private static final String KEY_MIN_PARAMETER = "min-shutter-speed";
    private static final String KEY_SONY_PARAMETER = "sony-shutter-speed";
    private static final String KEY_SONY_MAX_PARAMETER = "sony-max-shutter-speed";
    private static final String KEY_SONY_MIN_PARAMETER = "sony-min-shutter-speed";

    private WidgetOptionButton mMinusButton;
    private WidgetOptionButton mPlusButton;
    private WidgetOptionLabel mValueLabel;

    private boolean mIsSony;


    private class MinusClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            setShutterSpeedValue(Math.max(getShutterSpeedValue() - 1, getMinShutterSpeedValue()));
        }
    }

    private class PlusClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            setShutterSpeedValue(Math.min(getShutterSpeedValue() + 1, getMaxShutterSpeedValue()));
        }
    }

    public ShutterSpeedWidget(CameraManager cam, Context context) {
        super(cam, context, R.drawable.ic_widget_placeholder);

        // Add views in the widget
        mMinusButton = new WidgetOptionButton(R.drawable.ic_widget_timer_minus, context);
        mPlusButton = new WidgetOptionButton(R.drawable.ic_widget_timer_plus, context);
        mValueLabel = new WidgetOptionLabel(context);

        mMinusButton.setOnClickListener(new MinusClickListener());
        mPlusButton.setOnClickListener(new PlusClickListener());

        addViewToContainer(mMinusButton);
        addViewToContainer(mValueLabel);
        addViewToContainer(mPlusButton);

        mValueLabel.setText(restoreValueFromStorage(KEY_PARAMETER));

        getToggleButton().setHintText(R.string.widget_shutter_speed);
    }

    public boolean isSupportedSony(Camera.Parameters params) {
        return params.get(KEY_SONY_PARAMETER) != null;
    }

    @Override
    public boolean isSupported(Camera.Parameters params) {
        mIsSony = isSupportedSony(params);
        return (params.get(KEY_PARAMETER) != null || mIsSony);
    }

    public int getShutterSpeedValue() {
        if (mIsSony) {
            return Integer.parseInt(mCamManager.getParameters().get(KEY_SONY_PARAMETER));
        } else {
            return Integer.parseInt(mCamManager.getParameters().get(KEY_PARAMETER));
        }
    }

    public int getMinShutterSpeedValue() {
        if (mIsSony) {
            return Integer.parseInt(mCamManager.getParameters().get(KEY_SONY_MIN_PARAMETER));
        } else {
            return Integer.parseInt(mCamManager.getParameters().get(KEY_MIN_PARAMETER));
        }
    }

    public int getMaxShutterSpeedValue() {
        if (mIsSony) {
            return Integer.parseInt(mCamManager.getParameters().get(KEY_SONY_MAX_PARAMETER));
        } else {
            return Integer.parseInt(mCamManager.getParameters().get(KEY_MAX_PARAMETER));
        }
    }

    public void setShutterSpeedValue(int value) {
        String valueStr = Integer.toString(value);

        if (mIsSony) {
            mCamManager.setParameterAsync(KEY_SONY_PARAMETER, valueStr);
        } else {
            mCamManager.setParameterAsync(KEY_PARAMETER, valueStr);
        }
        SettingsStorage.storeCameraSetting(mWidget.getContext(), mCamManager.getCurrentFacing(),
                KEY_PARAMETER, valueStr);
        mValueLabel.setText(valueStr);
    }
}
