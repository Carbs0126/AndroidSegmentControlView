# AndroidSegmentControlView
## Abstract
an Android SegmentControlView inspired by the UISegmentControl on IOS platform.

## Introduction
an SegmentControlView inspired by the UISegmentControl on IOS platform.
this view has many interesting configurations such as :
  1. set gradient effect when segment is changing by ViewPager if needed.
  2. set the corners' radius.
  3. set colors of texts and background.
  4. set titles in xml resource file.
  5. has pressed effect when finger touches this view, you can set the dark coefficient.
  
  Author:Carbs.Wang
  
  Email:yeah0126#yeah.net

## Screenshot
<center>
![you can check the AndroidSegmentControlView_screenshot.png](https://github.com/Carbs0126/Screenshot/blob/master/AndroidSegmentControlView.gif)
</center><br>
<center>
![SegmentControlView pressed](https://github.com/Carbs0126/Screenshot/blob/master/AndroidSegmentControlView2.jpg)
</center>
<center>
![SegmentControlView gradient effect](https://github.com/Carbs0126/Screenshot/blob/master/AndroidSegmentControlView3.jpg)
</center>
## Example
1. first add dependences
```
  dependencies {
    compile 'cn.carbs.android:SegmentControlView:1.0.0'
  }
```
2. then add in xml layout:
```
    <cn.carbs.android.segmentcontrolview.library.SegmentControlView
        android:id="@+id/scv"
        android:layout_width="match_parent"
        android:layout_height="36dp"
        android:layout_marginTop="5dp"
        android:paddingLeft="10dp"
        android:paddingRight="10dp"
        app:scv_FrameCornerRadius="3dp"
        app:scv_FrameWidth="1dp"
        app:scv_Gradient="true"
        app:scv_SegmentPaddingVertical="5dp"
        app:scv_TextArray="@array/segment_control_arrays_1"/>
```
3. then handle SegmentControlView in java code
```
    segmentcontrolview.setOnSegmentChangedListener(new SegmentControlView.OnSegmentChangedListener() {
        @Override
        public void onSegmentChanged(int newSelectedIndex) {
            if(viewpager != null){
                //change the second argument to true if you want the gradient effect when viewpager is changing
                viewpager.setCurrentItem(newSelectedIndex, false);//viewpager changing without animation
            }
        }
    });
    //set viewpager to change segment according to the state of viewpager
    segmentcontrolview.setViewPager(viewpager);
    //set the selected index of segments initiatively
    segmentcontrolview.setSelectedIndex();
    //set gradient effect if you want
    segmentcontrolview.setGradient(true);
```
## TODO
Not support generating a MaxHeightView by java code

enjoy.




