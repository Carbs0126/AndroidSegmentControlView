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
![you can check the AndroidSegmentControlView_screenshot.png](https://github.com/Carbs0126/AndroidSegmentControlView/blob/master/SegmentControlView/Screenshot/AndroidSegmentControlView.gif)
</center><br>
<center>
![SegmentControlView pressed](https://github.com/Carbs0126/AndroidSegmentControlView/blob/master/SegmentControlView/Screenshot/AndroidSegmentControlView2.jpg)
</center>
<center>
![SegmentControlView gradient effect](https://github.com/Carbs0126/AndroidSegmentControlView/blob/master/SegmentControlView/Screenshot/AndroidSegmentControlView3.jpg)
</center>
## Example
in xml layout:
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

## TODO
Not support generating a MaxHeightView by java code

enjoy.




