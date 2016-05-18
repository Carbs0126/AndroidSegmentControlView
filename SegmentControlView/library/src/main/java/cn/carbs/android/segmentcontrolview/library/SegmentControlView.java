package cn.carbs.android.segmentcontrolview.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Field;

/**
 * an SegmentControlView inspired by the UISegmentControl on IOS platform.
 * this view has many interesting such as :
 * 	1. set gradient effect when segment is changing by ViewPager if needed
 * 	2. set the corners' radius
 * 	3. set colors of texts and background
 * 	4. set titles in xml resource file
 * 	5. has pressed effect when finger touches this view, you can set the dark coefficient   
 * 
 * Author	Carbs.Wang 
 * Email  	yeah0126#yeah.net
 */
public class SegmentControlView extends View {
	
	/**
	 * onSegmentChanged function will be triggered if segment changed
	 */
	public interface OnSegmentChangedListener{
    	void onSegmentChanged(int newSelectedIndex);
    }
	
	private static final float TOUCHED_BACKGROUND_DARK_COEFFICIENT = 0.95F;
	
	private static final int COLOR_PRIMARY_NORMAL = 0XFFFFFFFF;
	private static final int COLOR_PRIMARY_SELECTED = 0XFF2CA99F;
	
	private static final int DEFAULT_COLOR_BACKGROUND_SELECTED = COLOR_PRIMARY_SELECTED;
	private static final int DEFAULT_COLOR_BACKGROUND_NORMAL = COLOR_PRIMARY_NORMAL;
	private static final int DEFAULT_COLOR_TEXT_SELECTED = COLOR_PRIMARY_NORMAL;
	private static final int DEFAULT_COLOR_TEXT_NORMAL = COLOR_PRIMARY_SELECTED;
	private static final int DEFAULT_COLOR_FRAME = COLOR_PRIMARY_SELECTED;
	private static final int DEFAULT_TEXT_SIZE_SP = 16;
	private static final int DEFAULT_FRAME_WIDTH_PX = 2;
	private static final int DEFAULT_FRAME_CORNER_RADIUS_PX = 0;
	private static final int DEFAULT_SELECTED_INDEX = 0;
	private static final int DEFAULT_SEGMENT_PADDING_HORIZONTAL = 16;
	private static final int DEFAULT_SEGMENT_PADDING_VERTICAL = 12;
	private static final boolean DEFAULT_IS_GRADIENT = false;
	
    private String[] mTexts = null;
    
    private int mColorBackgroundSelected = DEFAULT_COLOR_BACKGROUND_SELECTED;
    private int mColorBackgroundNormal = DEFAULT_COLOR_BACKGROUND_NORMAL;
    private int mColorTextSelected = DEFAULT_COLOR_TEXT_SELECTED;
    private int mColorTextNormal = DEFAULT_COLOR_TEXT_NORMAL;
    private int mColorFrame = DEFAULT_COLOR_FRAME;
    private int mFrameWidth = DEFAULT_FRAME_WIDTH_PX;
    private int mFrameCornerRadius = DEFAULT_FRAME_CORNER_RADIUS_PX;
    
    private int mTextSize = 0;
    private int mSelectedIndex = DEFAULT_SELECTED_INDEX;
    
    //used in wrap_content mode
    private int mSegmentPaddingHorizontal = DEFAULT_SEGMENT_PADDING_HORIZONTAL;
    private int mSegmentPaddingVertical = DEFAULT_SEGMENT_PADDING_VERTICAL;
    
    private boolean mIsGradient = DEFAULT_IS_GRADIENT;
    private OnSegmentChangedListener mOnSegmentChangedListener;
	
    private float unitWidth = 0;
    private Paint paintText;		//painter of the text 
    private Paint paintBackground;	//painter of the background
    private Paint paintFrame;		//painter of the frame
    private RectF rectF;
    private RectF rectFArc;
    private Path pathFrame;
    
    private float textCenterYOffset;
    
    private int preTouchedIndex = -1;
    private int curTouchedIndex = -1;

    private ViewPager viewPager;
    
    public SegmentControlView(Context context) {
        super(context);
        init();
    }
    public SegmentControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }
    public SegmentControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SegmentControlView);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if(attr == R.styleable.SegmentControlView_scv_BackgroundSelectedColor){
            	mColorBackgroundSelected = a.getColor(attr, DEFAULT_COLOR_BACKGROUND_SELECTED);
            }else if(attr == R.styleable.SegmentControlView_scv_BackgroundNormalColor){
            	mColorBackgroundNormal = a.getColor(attr, DEFAULT_COLOR_BACKGROUND_NORMAL);
            }else if(attr == R.styleable.SegmentControlView_scv_TextSelectedColor){
            	mColorTextSelected = a.getColor(attr, DEFAULT_COLOR_TEXT_SELECTED);
            }else if(attr == R.styleable.SegmentControlView_scv_TextNormalColor){
            	mColorTextNormal = a.getColor(attr, DEFAULT_COLOR_TEXT_NORMAL);
            }else if(attr == R.styleable.SegmentControlView_scv_FrameColor){
            	mColorFrame = a.getColor(attr, DEFAULT_COLOR_FRAME);
            }else if(attr == R.styleable.SegmentControlView_scv_TextSize){
            	mTextSize = a.getDimensionPixelSize(attr, sp2px(getContext(), DEFAULT_TEXT_SIZE_SP));
            }else if(attr == R.styleable.SegmentControlView_scv_TextArray){
            	mTexts = convertCharSequenceToString(a.getTextArray(attr));
            }else if(attr == R.styleable.SegmentControlView_scv_FrameWidth){
            	mFrameWidth = a.getDimensionPixelSize(attr, DEFAULT_FRAME_WIDTH_PX);
            }else if(attr == R.styleable.SegmentControlView_scv_FrameCornerRadius){
            	mFrameCornerRadius = a.getDimensionPixelSize(attr, DEFAULT_FRAME_CORNER_RADIUS_PX);
            }else if(attr == R.styleable.SegmentControlView_scv_SelectedIndex){
            	mSelectedIndex = a.getInteger(attr, DEFAULT_SELECTED_INDEX);
            }else if(attr == R.styleable.SegmentControlView_scv_SegmentPaddingHorizontal){
            	mSegmentPaddingHorizontal = a.getDimensionPixelSize(attr, DEFAULT_SEGMENT_PADDING_HORIZONTAL);
            }else if(attr == R.styleable.SegmentControlView_scv_SegmentPaddingVertical){
            	mSegmentPaddingVertical = a.getDimensionPixelSize(attr, DEFAULT_SEGMENT_PADDING_VERTICAL);
            }else if(attr == R.styleable.SegmentControlView_scv_Gradient){
            	mIsGradient = a.getBoolean(attr, DEFAULT_IS_GRADIENT);
            }
        }
        a.recycle();
    }
    
    private void init(){
    	rectF = new RectF();
    	rectFArc = new RectF();
    	pathFrame = new Path();

    	if(mTextSize == 0)
    		mTextSize = sp2px(getContext(), DEFAULT_TEXT_SIZE_SP);
    	
    	paintText = new Paint();
    	paintText.setAntiAlias(true);
    	paintText.setTextAlign(Paint.Align.CENTER);
    	paintText.setTextSize(mTextSize);
        
    	paintBackground = new Paint();
    	paintBackground.setAntiAlias(true);
    	paintBackground.setStyle(Paint.Style.FILL);
        
    	paintFrame = new Paint();
    	paintFrame.setAntiAlias(true);
    	paintFrame.setStyle(Paint.Style.STROKE);
    	paintFrame.setStrokeWidth(mFrameWidth);
    	paintFrame.setColor(mColorFrame);
    	
        textCenterYOffset = getTextCenterYOffset(paintText.getFontMetrics());
        this.setClickable(true);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	setMeasuredDimension(measureWidth(widthMeasureSpec, paintText), 
        					 measureHeight(heightMeasureSpec, paintText));	
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        rectF.left = getPaddingLeft();
        rectF.top = getPaddingTop();
        rectF.right = w - getPaddingRight();
        rectF.bottom = h - getPaddingBottom();
        float inset = (float)Math.ceil(mFrameWidth / 2);
        rectF.inset(inset, inset);

        if(mTexts!= null && mTexts.length > 0){
        	unitWidth = rectF.width() / mTexts.length;
        }
        
        rectFArc.left = 0;
		rectFArc.top = 0;
		rectFArc.right = 2 * mFrameCornerRadius;
		rectFArc.bottom = 2 * mFrameCornerRadius;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isStringArrayEmpty(mTexts)){
        	drawBackgroundAndFrameAndText(canvas);
        }
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		preTouchedIndex = curTouchedIndex;
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			curTouchedIndex = getTouchedIndex(event.getX(), event.getY());
			if(preTouchedIndex != curTouchedIndex){
				invalidate();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			curTouchedIndex = getTouchedIndex(event.getX(), event.getY());
			if(preTouchedIndex != curTouchedIndex){
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			curTouchedIndex = getTouchedIndex(event.getX(), event.getY());
			if(curTouchedIndex != -1){
				if(mOnSegmentChangedListener != null && mSelectedIndex != curTouchedIndex){
					mOnSegmentChangedListener.onSegmentChanged(curTouchedIndex);
				}
				mSelectedIndex = curTouchedIndex;
			}
			curTouchedIndex = -1;
			if(mIsGradient && checkViewPagerOnPageChangeListener(this.viewPager)){
				
			}else{
				invalidate();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			curTouchedIndex = -1;
			invalidate();
			break;
		}
		return super.onTouchEvent(event);
	}
    
    public void setTextSize(int textSize){
    	if(this.mTextSize != textSize){
    		this.mTextSize = textSize;
    		paintText.setTextSize(textSize);
    		textCenterYOffset = getTextCenterYOffset(paintText.getFontMetrics());
    		requestLayout();
    		invalidate();
    	}
    }
    
    public int getSelectedIndex(){
    	return mSelectedIndex;
    }
    
    public void setSelectedIndex(int selectedIndex){
    	if(mSelectedIndex != selectedIndex){
    		mSelectedIndex = selectedIndex;
    		if(mOnSegmentChangedListener != null){
    			mOnSegmentChangedListener.onSegmentChanged(mSelectedIndex);
    		}
//    		invalidate();
    		if(mIsGradient && checkViewPagerOnPageChangeListener(this.viewPager)){
			}else{
				invalidate();
			}
    	}
    }
    
    public void setTextColor(int textColorNormal, int textColorSelected){
        this.mColorTextNormal = textColorNormal;
        this.mColorTextSelected = textColorSelected;
        invalidate();
    }
    
    public void setBackgroundColor(int backgroundColorNormal, int backgroundColorSelected){
        this.mColorBackgroundNormal = backgroundColorNormal;
        this.mColorBackgroundSelected = backgroundColorSelected;
        invalidate();
    }
    
    public void setFrameColor(int frameColor){
    	this.mColorFrame = frameColor;
    	invalidate();
    }
    
    public void setFrameWidth(int frameWidth){
    	this.mFrameWidth = frameWidth;
    	requestLayout();
    	invalidate();
    }
    
    public void setTexts(String[] texts){
    	assertTextsValid(texts);
    	if(texts == null || texts.length < 2){
    		throw new IllegalArgumentException("SegmentControlView's content text array'length should larger than 1");
    	}
    	if(checkIfEqual(this.mTexts, texts)){
    		return;
    	}
    	this.mTexts = texts;
       	unitWidth = rectF.width() / texts.length;
       	requestLayout();
       	invalidate();
    }
    
    public int getCount(){
    	if(mTexts == null) return 0;
    	return mTexts.length;
    }
    
    /**
     * setViewPager(viewpager) to response to the change of ViewPager
     * @param viewPager
     */
    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
        }
    }
    
    /**
     * set if this view has the Gradient effect when segment changed
     * @param gradient
     */
    public void setGradient(boolean gradient){
    	if(mIsGradient != gradient){
    		mIsGradient = gradient;
    	}
    }
    
    public boolean getGradient(){
    	return mIsGradient;
    }
    
    /**
     * when segment changed, 
     * mOnSegmentChangedListener.onSegmentChanged(newSelectedIndex) will be triggered
     * @param listener
     */
    public void setOnSegmentChangedListener(OnSegmentChangedListener listener){
		mOnSegmentChangedListener = listener;
	}
    
    public void update(){
    	invalidate();
    }
    
    private float getTextCenterYOffset(Paint.FontMetrics fontMetrics){
    	if(fontMetrics == null) return 0;
    	return Math.abs(fontMetrics.top + fontMetrics.bottom)/2;
    }
    
    private String[] convertCharSequenceToString(CharSequence[] csArray){
    	if(csArray == null)	return null;
    	String[] sArray = new String[csArray.length];
    	for(int i = 0; i < csArray.length; i++){
    		sArray[i] = csArray[i].toString();
    	}
    	return sArray;
    }

    private void assertTextsValid(String[] texts){
    	if(texts == null || texts.length < 2){
    		throw new IllegalArgumentException("SegmentControlView's content text array'length should larger than 1");
    	}
    }
    
    private boolean checkViewPagerOnPageChangeListener(ViewPager viewPager){
		if(viewPager == null) return false;
		Field field = null;
		try {
			field = ViewPager.class.getDeclaredField("mOnPageChangeListener");
			if(field == null) return false;
			field.setAccessible(true);
			Object o = field.get(viewPager);
			if(o != null && o instanceof InternalViewPagerListener){
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
    
    private int getTouchedIndex(float x, float y){
    	
    	if(!rectF.contains(x, y)){
    		return -1;
    	}
    	for(int i = 0; i < mTexts.length; i++){
    		if(rectF.left + i * unitWidth <= x && x < rectF.left + (i + 1) * unitWidth){
    			return i;
    		}
    	}
    	return -1;
    }
    
    private boolean checkIfEqual(String[] a, String[] b){
    	if(a == null && b == null){
    		return true;
    	}
    	if(a != null){
    		if(b == null){
    			return false;
    		}
    		if(a.length != b.length){
    			return false;
    		}
    		for(int i = 0; i < a.length; i++){
    			if(a[i] == null && b[i] == null){
    				continue;
    			}
    			if(a[i] != null && a[i].equals(b[i])){
    				continue;
    			}else{
    				return false;
    			}
    		}
    		return true;
    	}
    	return false;
    }
    
    private int measureWidth(int measureSpec, Paint paint) {  
		int result = 0;  
		int specMode = MeasureSpec.getMode(measureSpec);  
		int specSize = MeasureSpec.getSize(measureSpec);  
		   
		if (specMode == MeasureSpec.EXACTLY) {  
			result = specSize;
		} else {  
			int maxWidth = 0;
			int maxWidthItem = getMaxWidthOfTextArray(mTexts, paint);
			maxWidth = (maxWidthItem + 2 * mSegmentPaddingHorizontal + 2 * mFrameWidth) * mTexts.length;
			
			if(maxWidth < 2 * mFrameCornerRadius){
				maxWidth = 2 * mFrameCornerRadius;
			}
			
			result = this.getPaddingLeft() + this.getPaddingRight() + maxWidth;//MeasureSpec.UNSPECIFIED
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);  
			}
		}
		return result;  
	}  
    
    private int measureHeight(int measureSpec, Paint paint) {  
		int result = 0;  
		int specMode = MeasureSpec.getMode(measureSpec);  
		int specSize = MeasureSpec.getSize(measureSpec);  
		
		if (specMode == MeasureSpec.EXACTLY) {  
			result = specSize;  
		} else {  
			int maxHeight = 0;
			int maxHeightItem = getMaxHeightOfTextArray(mTexts, paint);
			
			maxHeight = maxHeightItem + 2 * mSegmentPaddingVertical + 2 * mFrameWidth;
			
			if(maxHeight < 2 * mFrameCornerRadius){
				maxHeight = 2 * mFrameCornerRadius;
			}
			
			result = this.getPaddingTop() + this.getPaddingBottom() + maxHeight;//MeasureSpec.UNSPECIFIED
			if (specMode == MeasureSpec.AT_MOST) {  
				result = Math.min(result, specSize);  
			}  
		}  
		return result;
	}
    
    private int getMaxWidthOfTextArray(String[] array, Paint paint){
    	if(array == null){
    		return 0;
    	}
    	int maxWidth = 0;
    	for(String item : array){
    		if(item != null){
    			int itemWidth = getTextWidth(item, paint);
    			maxWidth = Math.max(itemWidth, maxWidth);
    		}
    	}
    	return maxWidth;
    }
    
    private int getMaxHeightOfTextArray(String[] array, Paint paint){
    	if(array == null){
    		return 0;
    	}
    	int maxHeight = 0;
    	for(String item : array){
    		if(item != null){
    			int itemHeight = getTextHeight(item, paint);
    			maxHeight = Math.max(itemHeight, maxHeight);
    		}
    	}
    	return maxHeight;
    }
    
    private void drawBackgroundAndFrameAndText(Canvas canvas){
    	int curBackgroundColor = 0;
    	int curTextColor = 0;
    	for(int i = 0; i < mTexts.length; i++){
    		float left = rectF.left + unitWidth * i;
    		pathFrame.reset();
    		if(i == 0){
    			pathFrame.moveTo(rectF.left, rectF.top + mFrameCornerRadius);
    			rectFArc.offsetTo(rectF.left, rectF.top);
    			pathFrame.arcTo(rectFArc, 180, 90);
    			pathFrame.lineTo(rectF.left + unitWidth, rectF.top);
    			pathFrame.lineTo(rectF.left + unitWidth, rectF.bottom);
    			pathFrame.lineTo(rectF.left + mFrameCornerRadius, rectF.bottom);
    			rectFArc.offsetTo(rectF.left, rectF.bottom - 2 * mFrameCornerRadius);
    			pathFrame.arcTo(rectFArc, 90, 90);
    		}else if(i == (mTexts.length - 1)){
    			pathFrame.moveTo(rectF.left + i * unitWidth, rectF.top);
    			pathFrame.lineTo(rectF.right - mFrameCornerRadius, rectF.top);
    			rectFArc.offsetTo(rectF.right - 2 * mFrameCornerRadius, rectF.top);
    			pathFrame.arcTo(rectFArc, 270, 90);
    			pathFrame.lineTo(rectF.right, rectF.bottom - mFrameCornerRadius);
    			rectFArc.offsetTo(rectF.right - 2 * mFrameCornerRadius, rectF.bottom - 2 * mFrameCornerRadius);
    			pathFrame.arcTo(rectFArc, 0, 90);
    			pathFrame.lineTo(rectF.left + i * unitWidth, rectF.bottom);
    		}else{
    			pathFrame.moveTo(left, rectF.top);
    			pathFrame.lineTo(left + unitWidth, rectF.top);
    			pathFrame.lineTo(left + unitWidth, rectF.bottom);
    			pathFrame.lineTo(left, rectF.bottom);
    		}
    		pathFrame.close();
    		
    		if(!mIsGradient){
	    		if(i == mSelectedIndex){
					curBackgroundColor = mColorBackgroundSelected;
					curTextColor = mColorTextSelected;
				}else{
					curBackgroundColor = mColorBackgroundNormal;
					curTextColor = mColorTextNormal;
				}
			}
			if(mIsGradient){
				if(viewPagerPositionOffset != 0f){
					if(i == viewPagerPosition){
						curBackgroundColor = getEvaluateColor(viewPagerPositionOffset, mColorBackgroundSelected, mColorBackgroundNormal);
						curTextColor = getEvaluateColor(viewPagerPositionOffset, mColorTextSelected, mColorTextNormal);
					}else if(i == viewPagerPosition + 1){
						curBackgroundColor = getEvaluateColor(viewPagerPositionOffset, mColorBackgroundNormal, mColorBackgroundSelected);
						curTextColor = getEvaluateColor(viewPagerPositionOffset, mColorTextNormal, mColorTextSelected);
					}else{
						curBackgroundColor = mColorBackgroundNormal;
						curTextColor = mColorTextNormal;
					}
				}else{
					if(i == mSelectedIndex){
						curBackgroundColor = mColorBackgroundSelected;
						curTextColor = mColorTextSelected;
					}else{
						curBackgroundColor = mColorBackgroundNormal;
						curTextColor = mColorTextNormal;
					}
				}
			}
			paintBackground.setColor(curBackgroundColor);
			
			if(curTouchedIndex == i){
				paintBackground.setColor(getDarkColor(curBackgroundColor, TOUCHED_BACKGROUND_DARK_COEFFICIENT));
			}
			canvas.drawPath(pathFrame, paintBackground);
			canvas.drawPath(pathFrame, paintFrame);
    		
    		paintText.setColor(curTextColor);
    		canvas.drawText(mTexts[i], left + unitWidth / 2,rectF.centerY() + textCenterYOffset, paintText);
    	}
    }
    
    private int viewPagerPosition = -1;
    private float viewPagerPositionOffset = 0f;
    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        	if(mIsGradient){
        		mSelectedIndex = position;
        		viewPagerPosition = position;
        		viewPagerPositionOffset = positionOffset;
        		invalidate();
        	}
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageSelected(int position) {
        	if(mIsGradient){
        	}else{
        		SegmentControlView.this.setSelectedIndex(position);
        	}
        }
    }

    private int getDarkColor(int color, float darkCoefficient){
    	
    	int a = (color & 0xff000000) >>> 24;
    	int r = (color & 0x00ff0000) >>> 16;
    	int g = (color & 0x0000ff00) >>> 8;
    	int b = (color & 0x000000ff) >>> 0;
    	
    	r = (int)(r * darkCoefficient);
    	g = (int)(g * darkCoefficient);
    	b = (int)(b * darkCoefficient);
    	
    	return a << 24 | r << 16 | g << 8 | b;
    }
   
    private int getEvaluateColor(float fraction, int startColor, int endColor){
    	
    	int a, r, g, b;
    	
    	int sA = (startColor & 0xff000000) >>> 24;
    	int sR = (startColor & 0x00ff0000) >>> 16;
    	int sG = (startColor & 0x0000ff00) >>> 8;
    	int sB = (startColor & 0x000000ff) >>> 0;
    	
    	int eA = (endColor & 0xff000000) >>> 24;
    	int eR = (endColor & 0x00ff0000) >>> 16;
    	int eG = (endColor & 0x0000ff00) >>> 8;
    	int eB = (endColor & 0x000000ff) >>> 0;
    	
    	a = (int)(sA + (eA - sA) * fraction);
    	r = (int)(sR + (eR - sR) * fraction);
    	g = (int)(sG + (eG - sG) * fraction);
    	b = (int)(sB + (eB - sB) * fraction);
    	
    	return a << 24 | r << 16 | g << 8 | b;
    }
    
    private boolean isStringArrayEmpty(String[] array){
    	return (array == null || array.length == 0);
    }
    
    private static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    } 
    
    private int getTextWidth(String text, Paint paint){
    	if(!TextUtils.isEmpty(text)){
    		return (int)(paint.measureText(text) + 0.5f);
  		}
    	return -1;
    }
    
    private int getTextHeight(String text, Paint paint){
    	if(!TextUtils.isEmpty(text)){
    		Rect textBounds = new Rect();
    		paint.getTextBounds(text, 0, text.length(), textBounds);
  			return textBounds.height();
  		}
    	return -1;
    }
}