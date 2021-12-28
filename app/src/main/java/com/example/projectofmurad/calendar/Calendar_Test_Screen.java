package com.example.projectofmurad.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class Calendar_Test_Screen extends AppCompatActivity {
    private LocalDate selectedDate;

    LocalDate today;

    String day;

    Calendar_Month_Fragment fragment;
    Calendar_Month_Fragment prevMonthFragment;
    Calendar_Month_Fragment nextMonthFragment;

    public static ViewPager2 viewPager2;
    public static ScreenSlidePagerAdapter adapter;

    public static ArrayList<Calendar_Month_Fragment> fragmentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_test_screen);

        fragmentArrayList = new ArrayList<>();


        Objects.requireNonNull(getSupportActionBar()).setTitle("Calendar");

        selectedDate = LocalDate.now();
        today = LocalDate.now();

        day = "" + selectedDate.getDayOfMonth();
        Toast.makeText(getApplicationContext(), day, Toast.LENGTH_SHORT).show();

        /*fragment = new Calendar_Month_Fragment();
        Bundle bundle = new Bundle();

        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_DAY, selectedDate.getDayOfMonth());
        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_MONTH, selectedDate.getMonth().getValue());
        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_YEAR, selectedDate.getYear());

        fragment.setArguments(bundle);*/

        /*fragment = Utils.createCalendar_Month_Fragment(selectedDate);
        prevMonthFragment = Utils.createCalendar_Month_Fragment(selectedDate.minusMonths(1));
        nextMonthFragment = Utils.createCalendar_Month_Fragment(selectedDate.plusMonths(1));*/

        fragmentArrayList.add(prevMonthFragment);
        fragmentArrayList.add(fragment);
        fragmentArrayList.add(nextMonthFragment);

        viewPager2 = findViewById(R.id.view_pager2);
        //adapter = new ScreenSlidePagerAdapter(this, selectedDate);

        LinkedList<Calendar_Month_Fragment> fragments = new LinkedList<>();
        fragments.add(Utils.createCalendar_Month_Fragment(selectedDate.minusMonths(2)));
        fragments.add(Utils.createCalendar_Month_Fragment(selectedDate.minusMonths(1)));
        //fragments.get(0).onCreate(Bundle.EMPTY);
        fragments.add(Utils.createCalendar_Month_Fragment(selectedDate));
        //fragments.get(1).onCreate(Bundle.EMPTY);
        fragments.add(Utils.createCalendar_Month_Fragment(selectedDate.plusMonths(1)));
        //fragments.get(2).onCreate(Bundle.EMPTY);
        fragments.add(Utils.createCalendar_Month_Fragment(selectedDate.plusMonths(2)));

        Log.d("view_pager2", "selectedDate = " + Utils.getDefaultDate(selectedDate));

        Log.d("view_pager2", "---------------------------------------------------------------------------------------------------");
        Log.d("view_pager2", "position = 0   -> " + Utils.getDefaultDate(fragments.get(0).getSelectedDate()));
        Log.d("view_pager2", "position = 1   -> " + Utils.getDefaultDate(fragments.get(1).getSelectedDate()));
        Log.d("view_pager2", "position = 2   -> " + Utils.getDefaultDate(fragments.get(2).getSelectedDate()));
        Log.d("view_pager2", "position = 3   -> " + Utils.getDefaultDate(fragments.get(3).getSelectedDate()));
        Log.d("view_pager2", "position = 4   -> " + Utils.getDefaultDate(fragments.get(4).getSelectedDate()));
        Log.d("view_pager2", "---------------------------------------------------------------------------------------------------");

        LinkedListAdapter smartAdapter = new LinkedListAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
        viewPager2.setAdapter(smartAdapter);
        viewPager2.setCurrentItem(1, false);
        viewPager2.setCurrentItem(3, false);
        viewPager2.setCurrentItem(2, false);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            boolean move = false;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                /*if(positionOffset == 0 && position != 1){
                    move = true;
                    Log.d("view_pager2", "moved");
                }
                Log.d("view_pager2", "" + position + "   " + positionOffset + "   " + positionOffsetPixels);*/
            }

            @Override
            public void onPageSelected(int position) {
                //super.onPageSelected(position);
                /*Calendar_Test_Screen.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        while(true){
                            if(move) break;
                        }
                        Log.d("view_pager2", "changing");
                        if(position == 2){
                            Calendar_Month_Fragment newCentralFragment = smartAdapter.getItem(position);
                            smartAdapter.remove(0);
                            smartAdapter.add( 2, Utils.createCalendar_Month_Fragment(newCentralFragment.getSelectedDate().plusMonths(1)));
                            //viewPager2.setCurrentItem(1, true);
                        }
                        else if(position == 0){
                            Calendar_Month_Fragment newCentralFragment = smartAdapter.getItem(position);
                            smartAdapter.remove(2);
                            smartAdapter.replace(0, Utils.createCalendar_Month_Fragment(newCentralFragment.getSelectedDate().minusMonths(1)));
                            smartAdapter.replace(1, newCentralFragment);
                            smartAdapter.add(2, Utils.createCalendar_Month_Fragment(newCentralFragment.getSelectedDate().plusMonths(1)));

                            smartAdapter.remove(2);
                            smartAdapter.add(0, Utils.createCalendar_Month_Fragment(newCentralFragment.getSelectedDate().minusMonths(1)));

                        }
                    }

                });*/

                /*Handler h = new Handler();
                h.post()

                boolean handler = new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        move = true;
                    }
                });


                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true){
                            if(move) break;
                        }
                        Log.d("view_pager2", "changing");
                        if(position == 2){
                            Calendar_Month_Fragment newCentralFragment = smartAdapter.getItem(position);
                            smartAdapter.remove(0);
                            smartAdapter.add( 2, Utils.createCalendar_Month_Fragment(newCentralFragment.getSelectedDate().plusMonths(1)));
                            //viewPager2.setCurrentItem(1, true);
                        }
                        else if(position == 0){
                            Calendar_Month_Fragment newCentralFragment = smartAdapter.getItem(position);
                            smartAdapter.remove(2);
                            smartAdapter.replace(0, Utils.createCalendar_Month_Fragment(newCentralFragment.getSelectedDate().minusMonths(1)));
                            smartAdapter.replace(1, newCentralFragment);
                            smartAdapter.add(2, Utils.createCalendar_Month_Fragment(newCentralFragment.getSelectedDate().plusMonths(1)));

                            smartAdapter.remove(2);
                            smartAdapter.add(0, Utils.createCalendar_Month_Fragment(newCentralFragment.getSelectedDate().minusMonths(1)));

                        }
                    }
                });
                //t.start();

                if(move){

                }*/

                if(position == 3){
                    Calendar_Month_Fragment newCentralFragment = smartAdapter.createFragment(3);
                    //viewPager2.setCurrentItem(1, true);

                    smartAdapter.movedToNext(newCentralFragment.getSelectedDate());
                    synchronized(this){
                        this.notify();
                    }
                    synchronized(smartAdapter){
                        smartAdapter.notify();
                        smartAdapter.notifyDataSetChanged();

                    }
                    synchronized(viewPager2){
                        viewPager2.notify();
                    }

                    Log.d("view_pager2", "position is " + position);
                }
                else if(position == 1){
                    Calendar_Month_Fragment newCentralFragment = smartAdapter.createFragment(1);

                    smartAdapter.movedToPrevious(newCentralFragment.getSelectedDate());
                    synchronized(this){
                        this.notify();
                    }
                    synchronized(smartAdapter){
                        smartAdapter.notify();
                        smartAdapter.notifyDataSetChanged();
                    }
                    synchronized(viewPager2){
                        viewPager2.notify();
                    }

                    Log.d("view_pager2", "position is " + position);
                }
                position = 2;
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        /*adapter = new ScreenSlidePagerAdapter(this, selectedDate);
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                LocalDate newDate = fragmentArrayList.get(position).getSelectedDate();
                Log.d("murad", "" + newDate.getDayOfMonth());

                adapter = new ScreenSlidePagerAdapter(Calendar_Test_Screen.this, newDate);
                viewPager.setAdapter(adapter);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        viewPager.setCurrentItem(1, false);*/


        //getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int selectedId = item.getItemId();

        if(selectedId == R.id.calendar_app_bar_today){
            selectedDate = today;

            //fragment = Calendar_Month_Fragment.newInstance(selectedDate.getDayOfMonth(), selectedDate.getMonth().getValue(), selectedDate.getYear());
            fragment = new Calendar_Month_Fragment(today.getDayOfMonth(), today.getMonth().getValue(), today.getYear());
            /*Bundle bundle = new Bundle();

            bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_DAY, selectedDate.getDayOfMonth());
            bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_MONTH, selectedDate.getMonth().getValue());
            bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_YEAR, selectedDate.getYear());

            fragment.setArguments(bundle);*/
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            //openFragment();
        }

        return true;
    }

}








