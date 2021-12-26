package com.example.projectofmurad.calendar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class Calendar_Test_Screen extends AppCompatActivity {
    private LocalDate selectedDate;

    LocalDate today;

    String day;

    Calendar_Month_Fragment fragment;
    Calendar_Month_Fragment prevMonthFragment;
    Calendar_Month_Fragment nextMonthFragment;

    ViewPager2 viewPager;
    ScreenSlidePagerAdapter adapter;

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

        fragment = Utils.createCalendar_Month_Fragment(selectedDate);
        prevMonthFragment = Utils.createCalendar_Month_Fragment(selectedDate.minusMonths(1));
        nextMonthFragment = Utils.createCalendar_Month_Fragment(selectedDate.plusMonths(1));

        fragmentArrayList.add(prevMonthFragment);
        fragmentArrayList.add(fragment);
        fragmentArrayList.add(nextMonthFragment);

        Bundle bundle = new Bundle();

        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_DAY, selectedDate.getDayOfMonth());
        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_MONTH, selectedDate.getMonth().getValue());
        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_YEAR, selectedDate.getYear());



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

     class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        LocalDate localDate;

        public ScreenSlidePagerAdapter(@NonNull Fragment fragment, LocalDate localDate) {
            super(fragment);
            this.localDate = localDate;
        }

        public ScreenSlidePagerAdapter(@NonNull Calendar_Test_Screen fragmentActivity, LocalDate localDate) {
            super(fragmentActivity);
            this.localDate = localDate;
        }

        @NonNull
        @Override
        public Calendar_Month_Fragment createFragment(int position) {
            switch(position){
                case 0:
                    return Utils.createCalendar_Month_Fragment(localDate.plusMonths(1));
                case 1:
                    return Utils.createCalendar_Month_Fragment(localDate);
                case 2:
                    return Utils.createCalendar_Month_Fragment(localDate.minusMonths(1));
                default:
                    return null;
            }

        }

        @Override
        public int getItemCount() {
            return 3;
        }
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
            fragment = new Calendar_Month_Fragment();
            Bundle bundle = new Bundle();

            bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_DAY, selectedDate.getDayOfMonth());
            bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_MONTH, selectedDate.getMonth().getValue());
            bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_YEAR, selectedDate.getYear());

            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            //openFragment();
        }

        return true;
    }

}








