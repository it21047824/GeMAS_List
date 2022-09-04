package com.example.gemaslist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AnimeStats extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AnimeStats() {
        // Required empty public constructor
    }

    public static AnimeStats newInstance(String param1, String param2) {
        AnimeStats fragment = new AnimeStats();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anime_stats, container, false);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        Context context = getContext();
        LinearLayoutCompat pieChartLayout = view.findViewById(R.id.anime_stats_donut);

        PieChartDrawable pieChart = new PieChartDrawable();

        ImageView pieChartImage = new ImageView(context);
        LayoutParams imageParams = new LayoutParams(500, 500);

        pieChartImage.setLayoutParams(imageParams);
        pieChartImage.setImageDrawable(pieChart);
        pieChartImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        pieChartLayout.addView(pieChartImage);

        return view;
    }

    class PieChartDrawable extends Drawable {

        private final RectF rect = new RectF(0,0,500,500);
        Paint paintPrimary, paintSecondary, paintTernary, paintBackground;
        private int watching, planning, completed;
        private float total= 100.0F;
        private float startAngle = 270.0F;

        public float getAngle(int num) {
            float angle = ((num/total)*360);
            setStartAngle(startAngle+angle);
            return angle;
        }

        public void setStartAngle(float startAngle) {
            this.startAngle = startAngle;
        }

        public PieChartDrawable() {
            paintPrimary = new Paint();
            paintPrimary.setAntiAlias(true);
            paintPrimary.setColor(getResources().getColor(R.color.watching));
            paintPrimary.setStrokeCap(Paint.Cap.ROUND);

            paintSecondary = new Paint();
            paintSecondary.setAntiAlias(true);
            paintSecondary.setColor(getResources().getColor(R.color.planning));
            paintSecondary.setStrokeCap(Paint.Cap.ROUND);

            paintTernary = new Paint();
            paintTernary.setAntiAlias(true);
            paintTernary.setColor(getResources().getColor(R.color.completed));
            paintTernary.setStrokeCap(Paint.Cap.ROUND);

            paintBackground = new Paint();
            paintBackground.setAntiAlias(true);
            paintBackground.setColor(Color.WHITE);
            paintBackground.setStrokeCap(Paint.Cap.ROUND);
            paintBackground.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            float radius = (float) Math.min(width, height) / 3;

            canvas.drawArc(rect, startAngle, getAngle(50), true, paintTernary);
            canvas.drawArc(rect, startAngle, getAngle(45), true, paintSecondary);
            canvas.drawArc(rect, startAngle, getAngle(5), true, paintPrimary);
            canvas.drawCircle(width/2, height/2, radius, paintBackground);
        }

        @Override
        public void setAlpha(int i) {
            //required method
        }

        @Override
        public int getAlpha() {
            return super.getAlpha();
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            //required method
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }

    }
}


