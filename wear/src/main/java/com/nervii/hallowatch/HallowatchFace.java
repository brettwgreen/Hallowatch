/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.nervii.hallowatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class HallowatchFace extends CanvasWatchFaceService {

    private int specW, specH;
    private BoneFontTextView tvTime, tvBattery, tvDate;
    private ImageView imgMoon;
    private final Point displaySize = new Point();


    @Override
    public Engine onCreateEngine() {
        return new Engine(this);
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        private Context _context;
        public Engine(Context context) {
            _context = context;
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        private final String TIME_FORMAT_DISPLAYED = "hh:mm a";
        private final String DATE_FORMAT_DISPLAYED = "E, MMMM d";

        private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                tvBattery.setText(String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%"));
            }
        };

        private BroadcastReceiver mTimeInfoReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent intent) {
                tvTime.setText(
                        new SimpleDateFormat(TIME_FORMAT_DISPLAYED)
                                .format(Calendar.getInstance().getTime()));

            }
        };

        private BroadcastReceiver mDateInfoReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent intent) {
                tvDate.setText(
                        new SimpleDateFormat(DATE_FORMAT_DISPLAYED)
                                .format(Calendar.getInstance().getTime()));
                int moonPhase = MoonPhase.GetPhaseDay((GregorianCalendar) Calendar.getInstance());

                if (moonPhase == 0) {
                    imgMoon.setImageResource(R.drawable.m0);
                }
                else if (moonPhase == 15) {
                    imgMoon.setImageResource(R.drawable.m15);
                }
                else if (moonPhase <= 3) {
                    imgMoon.setImageResource(R.drawable.m3);
                }
                else if (moonPhase <= 6) {
                    imgMoon.setImageResource(R.drawable.m6);
                }
                else if (moonPhase <= 9) {
                    imgMoon.setImageResource(R.drawable.m9);
                }
                else if (moonPhase <= 14) {
                    imgMoon.setImageResource(R.drawable.m12);
                }
                else if (moonPhase <= 18) {
                    imgMoon.setImageResource(R.drawable.m18);
                }
                else if (moonPhase <= 21) {
                    imgMoon.setImageResource(R.drawable.m21);
                }
                else if (moonPhase <= 24){
                    imgMoon.setImageResource(R.drawable.m24);
                }
                else if (moonPhase <= 29) {
                    imgMoon.setImageResource(R.drawable.m27);
                }
                else {
                    imgMoon.setImageResource(R.drawable.m0);
                }
                imgMoon.getDrawable().invalidateSelf();
            }

        };

        boolean mRegisteredReceivers = false;

        boolean mAmbient;
        View myLayout;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            LayoutInflater inflater = LayoutInflater.from(_context);
            myLayout = inflater.inflate(R.layout.round_activity_main, null);

            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            display.getSize(displaySize);

            specW = View.MeasureSpec.makeMeasureSpec(displaySize.x,
                    View.MeasureSpec.EXACTLY);
            specH = View.MeasureSpec.makeMeasureSpec(displaySize.y,
                    View.MeasureSpec.EXACTLY);

            tvTime = (BoneFontTextView) myLayout.findViewById(R.id.watch_time);
            tvBattery = (BoneFontTextView) myLayout.findViewById(R.id.watch_battery);
            tvDate = (BoneFontTextView) myLayout.findViewById(R.id.watch_date);
            imgMoon = (ImageView) myLayout.findViewById(R.id.watch_moon);

            setWatchFaceStyle(new WatchFaceStyle.Builder(HallowatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerReceivers();
            } else {
                unregisterReceivers();
            }
        }

        private void registerReceivers() {
            if (mRegisteredReceivers) {
                return;
            }
            mRegisteredReceivers = true;
            IntentFilter INTENT_FILTER = new IntentFilter();
            INTENT_FILTER.addAction(Intent.ACTION_TIME_TICK);
            INTENT_FILTER.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            INTENT_FILTER.addAction(Intent.ACTION_TIME_CHANGED);

            registerReceiver(mTimeInfoReceiver, INTENT_FILTER);
            registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            registerReceiver(mDateInfoReceiver, new IntentFilter(Intent.ACTION_DATE_CHANGED));

            // Call once manually to set initial values
            mTimeInfoReceiver.onReceive(HallowatchFace.this, registerReceiver(null, INTENT_FILTER));
            mDateInfoReceiver.onReceive(HallowatchFace.this, registerReceiver(null, INTENT_FILTER));
            mBatInfoReceiver.onReceive(HallowatchFace.this, registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));

        }

        private void unregisterReceivers() {
            if (!mRegisteredReceivers) {
                return;
            }
            mRegisteredReceivers = false;
            unregisterReceiver(mTimeInfoReceiver);
            unregisterReceiver(mBatInfoReceiver);
            unregisterReceiver(mDateInfoReceiver);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    tvTime.getPaint().setAntiAlias(!inAmbientMode);
                    tvDate.getPaint().setAntiAlias(!inAmbientMode);
                }
            }
            if (inAmbientMode) {
                tvBattery.setVisibility(View.GONE);
                imgMoon.setVisibility(View.GONE);
            }
            else {
                tvBattery.setVisibility(View.VISIBLE);
                imgMoon.setVisibility(View.VISIBLE);
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            myLayout.measure(specW, specH);
            myLayout.layout(0, 0, myLayout.getMeasuredWidth(),
                    myLayout.getMeasuredHeight());

            canvas.drawColor(Color.BLACK);
            myLayout.draw(canvas);
        }

    }

}
