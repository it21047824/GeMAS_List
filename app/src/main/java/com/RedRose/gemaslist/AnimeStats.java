package com.RedRose.gemaslist;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AnimeStats extends Fragment {

    protected int numWatching, numPlanning, numCompleted, numTotal, totalRatings;
    protected float averageRating;
    protected View view;
    protected AnimeUserData userData;
    protected CustomLinkList watching, planning, completed;
    protected int[] ratingData;

    public AnimeStats() {
        // Required empty public constructor
    }

    public static AnimeStats newInstance() {
        return new AnimeStats();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userData = AnimeUserData.getAnimeUserData();

        watching = userData.getWatchingList();
        planning = userData.getPlanningList();
        completed = userData.getCompletedList();
        numWatching = watching.size();
        numPlanning = planning.size();
        numCompleted = completed.size();

        numTotal = numWatching + numPlanning + numCompleted;

        //calculate average rating
        int ratingSum = 0;
        totalRatings = 0;
        ratingData = new int[11];
        for(int i=0; i<numWatching; i++){
            if(watching.getItem(i).rating != -1){
                int r = watching.getItem(i).rating;
                ratingSum += r;
                ratingData[r]++;
                totalRatings++;
            }
        }
        for(int i=0; i<numPlanning; i++){
            if(planning.getItem(i).rating != -1){
                int r = planning.getItem(i).rating;
                ratingSum += r;
                ratingData[r]++;
                totalRatings++;
            }
        }
        for(int i=0; i<numCompleted; i++){
            if(completed.getItem(i).rating != -1){
                int r = completed.getItem(i).rating;
                ratingSum += r;
                ratingData[r]++;
                totalRatings++;
            }
        }

        averageRating = (float) ratingSum / totalRatings;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_anime_stats, container, false);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        Context context = getContext();
        LinearLayoutCompat pieChartLayout = view.findViewById(R.id.anime_stats_donut);
        MaterialTextView totalAnime = view.findViewById(R.id.anime_stats_total);
        MaterialTextView meanScore = view.findViewById(R.id.anime_stats_mean_score);
        MaterialTextView watchingAnime = view.findViewById(R.id.anime_stats_watching);
        MaterialTextView planningAnime = view.findViewById(R.id.anime_stats_planning);
        MaterialTextView completedAnime = view.findViewById(R.id.anime_stats_completed);

        //set info
        totalAnime.setText(String.format(Locale.US,"%d", numTotal));
        meanScore.setText(String.format(Locale.US,"%.1f", averageRating));
        watchingAnime.setText(String.format(Locale.US,"%d", numWatching));
        planningAnime.setText(String.format(Locale.US,"%d", numPlanning));
        completedAnime.setText(String.format(Locale.US,"%d", numCompleted));

        //create pie chart
        PieChartDrawable pieChart = new PieChartDrawable(context);
        LayoutParams imageParams = new LayoutParams(500, 500);

        pieChart.setLayoutParams(imageParams);

        pieChartLayout.addView(pieChart);

        LinearLayoutCompat barGraphContainer =
                view.findViewById(R.id.anime_stats_bar_graph_container);
        LinearLayoutCompat barGraphView = view.findViewById(R.id.anime_stats_bar_graph);

        //set total anime ratings
        MaterialTextView totalAnimeRatings = view.findViewById(R.id.anime_stats_total_ratings);
        totalAnimeRatings.setText(String.format(Locale.US,"%d",totalRatings));

        AnimeStats.createBarGraph(
                barGraphContainer,
                barGraphView,
                ratingData,
                context != null ? context.getColor(R.color.main_theme) : 0,
                2.3F
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //fix variable length in viewpager 2
        view.requestLayout();
    }

    class PieChartDrawable extends View {

        private final RectF rect = new RectF(0,0,500,500);
        Paint paintPrimary, paintSecondary, paintTernary, paintBackground, blackPaint;
        private float startAngle = 270.0F;

        public PieChartDrawable(Context context) {
            super(context);

            TypedValue typedValue = new TypedValue();

            int colorWatching=0, colorPlanning=0, colorCompleted=0, colorText=0;

            if (getContext() != null){
                TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[]{
                        R.attr.animeWatching,
                        R.attr.animePlanning,
                        R.attr.animeCompleted,
                        R.attr.customColor1
                });

                colorWatching = a.getColor(0,0);
                colorPlanning = a.getColor(1,0);
                colorCompleted = a.getColor(2,0);
                colorText = a.getColor(3,0);

                a.recycle();
            }

            paintPrimary = new Paint();
            paintPrimary.setAntiAlias(true);
            paintPrimary.setColor(colorWatching);
            paintPrimary.setStrokeCap(Paint.Cap.ROUND);

            paintSecondary = new Paint();
            paintSecondary.setAntiAlias(true);
            paintSecondary.setColor(colorPlanning);
            paintSecondary.setStrokeCap(Paint.Cap.ROUND);

            paintTernary = new Paint();
            paintTernary.setAntiAlias(true);
            paintTernary.setColor(colorCompleted);
            paintTernary.setStrokeCap(Paint.Cap.ROUND);

            paintBackground = new Paint();
            paintBackground.setAntiAlias(true);
            paintBackground.setColor(Color.WHITE);
            paintBackground.setStrokeCap(Paint.Cap.ROUND);
            paintBackground.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));

            blackPaint = new Paint();
            blackPaint.setAntiAlias(true);
            blackPaint.setColor(colorText);
            blackPaint.setStrokeCap(Paint.Cap.ROUND);
            blackPaint.setTextAlign(Paint.Align.CENTER);
            blackPaint.setTextSize(30);

        }

        public float getAngle(int num) {
            float angle = ((num/ (float) numTotal)*360);
            setStartAngle(startAngle+angle);
            return angle;
        }

        public void setStartAngle(float startAngle) {
            this.startAngle = startAngle;
        }

        @Override
        public void onDraw(Canvas canvas) {

            int width = getWidth();
            int height = getHeight();
            float radius = (float) Math.min(width, height) / 3;

            canvas.drawArc(rect, startAngle, getAngle(numCompleted), true, paintTernary);
            canvas.drawArc(rect, startAngle, getAngle(numPlanning), true, paintSecondary);
            canvas.drawArc(rect, startAngle, getAngle(numWatching), true, paintPrimary);
            canvas.drawCircle((float)width/2, (float)height/2, radius, paintBackground);
            canvas.drawText("Status Distribution", (float)width/2, (float)height/2, blackPaint);
        }
    }

    public static void createBarGraph(
            LinearLayoutCompat container,
            @NonNull LinearLayoutCompat layout,
            int[] data,
            int barColor,
            float scale
    ) {

        layout.post( () -> {
                //get info
                int width = layout.getWidth();
                int height = layout.getHeight();

                int bars = data.length;
                int totalFrequency = 0;

                for (int frequency : data){
                    totalFrequency += frequency;
                }

                //width of a single bar
                float barWidth = (float) (width-bars-(10*bars))/bars;

                //list of bar heights
                List<Float> barHeightList = new ArrayList<>();
                for (int frequency : data) {
                    barHeightList.add(((float)frequency/totalFrequency)*height*scale);
                }

                //create a layout to put the graph
                LinearLayoutCompat barGraphLayout = new LinearLayoutCompat(layout.getContext());

                LayoutParams graphLayoutParams =
                        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                barGraphLayout.setLayoutParams(graphLayoutParams);
                barGraphLayout.setPadding(8,8,8,0);
                barGraphLayout.setOrientation(LinearLayoutCompat.HORIZONTAL);
                barGraphLayout.setGravity(Gravity.BOTTOM);

                //create a layout for graph legend
                LinearLayoutCompat barGraphLegend = new LinearLayoutCompat(container.getContext());

                LayoutParams graphLegendParams =
                        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                barGraphLegend.setLayoutParams(graphLegendParams);
                barGraphLegend.setPadding(8,0,8,0);
                barGraphLegend.setOrientation(LinearLayoutCompat.HORIZONTAL);
                barGraphLegend.setGravity(Gravity.CENTER);

                //add bars to created layout
                int index = 0;
                for (float barHeight : barHeightList) {
                    LinearLayoutCompat bar = new LinearLayoutCompat(layout.getContext());
                    TextView legend = new TextView(container.getContext());

                    LayoutParams barLayout = new LayoutParams((int) barWidth, (int) barHeight);
                    barLayout.setMargins(5,0,5,0);
                    LayoutParams legendLayout = new LayoutParams((int) barWidth, LayoutParams.WRAP_CONTENT);
                    legendLayout.setMargins(5,0,5,0);

                    bar.setLayoutParams(barLayout);
                    bar.setBackgroundColor(barColor);

                    legend.setLayoutParams(legendLayout);
                    legend.setText(String.format(Locale.US, "%d", index));
                    legend.setTextSize(12);
                    legend.setGravity(Gravity.CENTER);

                    barGraphLayout.addView(bar);
                    barGraphLegend.addView(legend);

                    index++;
                }

                //create divider
                LinearLayoutCompat divider = new LinearLayoutCompat(layout.getContext());
                LayoutParams dividerLayoutParams =
                        new LayoutParams(LayoutParams.MATCH_PARENT, 1);
                divider.setLayoutParams(dividerLayoutParams);

                TypedValue typedValue = new TypedValue();
                TypedArray a = layout.getContext().obtainStyledAttributes(typedValue.data,
                        new int[]{R.attr.customColor1});
                int dividerColor = a.getColor(0,0);
                a.recycle();

                divider.setBackgroundColor(dividerColor);

                //add graph to given layout
                layout.addView(barGraphLayout);
                container.addView(divider);
                container.addView(barGraphLegend);

        });


    }
}


