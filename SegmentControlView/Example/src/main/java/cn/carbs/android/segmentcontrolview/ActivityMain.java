package cn.carbs.android.segmentcontrolview;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.carbs.android.segmentcontrolview.library.SegmentControlView;

public class ActivityMain extends AppCompatActivity {

    private SegmentControlView segmentcontrolview;
    private ViewPager viewpager;
    private Button button_change_segment;
    private Button button_toggle_gradient;
    private int segmentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        segmentcontrolview = (SegmentControlView) this.findViewById(R.id.scv);
        segmentcontrolview.setOnSegmentChangedListener(new SegmentControlView.OnSegmentChangedListener() {
            @Override
            public void onSegmentChanged(int newSelectedIndex) {
                Toast.makeText(getApplicationContext(), newSelectedIndex + " selected", Toast.LENGTH_SHORT).show();
                if(viewpager != null){
                    //change the second argument to true if you want the gradient effect when viewpager is changing
                    viewpager.setCurrentItem(newSelectedIndex, false);
                }
            }
        });

        viewpager = (ViewPager)this.findViewById(R.id.vp);
        viewpager.setAdapter(new ThePagerAdapter(3));
        segmentcontrolview.setViewPager(viewpager);

        button_change_segment = (Button)this.findViewById(R.id.button_change_segment);
        button_change_segment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                segmentcontrolview.setSelectedIndex((++segmentIndex) % segmentcontrolview.getCount());
            }
        });

        button_toggle_gradient = (Button)this.findViewById(R.id.button_toggle_gradient);
        button_toggle_gradient.setText(segmentcontrolview.getGradient() ? "Gradient on" : "Gradient off");
        button_toggle_gradient.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                segmentcontrolview.setGradient(!segmentcontrolview.getGradient());
                button_toggle_gradient.setText(segmentcontrolview.getGradient() ? "Gradient on" : "Gradient off");
            }
        });
    }

    private class ThePagerAdapter extends PagerAdapter {

        private ArrayList<TextView> pages = new ArrayList<TextView>();
        private int pageNumber;
        private ViewPager.LayoutParams params = new ViewPager.LayoutParams();

        public ThePagerAdapter(int pageNumber) {
            this.pageNumber = pageNumber;
            for(int i = 0; i < pageNumber; i++){
                pages.add(null);
            }
        }

        @Override
        public int getCount() {
            return pageNumber;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView page = pages.get(position);
            if(page == null){
                page = new TextView(ActivityMain.this);
                page.setBackgroundColor(0x99990000);
                page.setLayoutParams(params);
                page.setGravity(Gravity.CENTER);
                page.setTextColor(0xffffffff);
                pages.set(position, page);
            }
            if (page != null) {
                container.addView(page);

                switch (position) {
                    case 0:
                        page.setBackgroundColor(0x99990000);
                        page.setText("ViewPagerItem-0");
                        break;
                    case 1:
                        page.setBackgroundColor(0x99009900);
                        page.setText("ViewPagerItem-1");
                        break;
                    case 2:
                        page.setBackgroundColor(0x99000099);
                        page.setText("ViewPagerItem-2");
                        break;
                }
            }
            return page;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }
}
