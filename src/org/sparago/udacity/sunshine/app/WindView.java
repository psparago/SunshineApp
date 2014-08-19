package org.sparago.udacity.sunshine.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

public class WindView extends View {

    private static final String LOG_TAG = WindView.class.getSimpleName();
    private static final float MAX_WIND_SPEED = 200f;
    private static final float TEST_WIND_SPEED = 20f;
    private static final float TEST_DEGREES = 330f;
    
    private static final int STROKE_WIDTH = 4;
    private static final int LABEL_AREA_Y = 20;
    private static final int LABEL_AREA_X = 20;
    
    private Paint axisPaint;
    private Paint barPaint;
    private Paint labelPaint;
	private Rect r = new Rect();
	private Point p = new Point();
	
    private float windSpeed = TEST_WIND_SPEED;
    private float degrees = TEST_DEGREES;
 
    public WindView(Context context) {
        super(context);
        initTools();
    }
 
    public WindView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTools();
    }
 
    public WindView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        initTools();
    }
 
	private void initTools() {
        Log.v(LOG_TAG, "Init Tools");
        
        axisPaint = new Paint();
        barPaint = new Paint();
        labelPaint = new Paint();
 
        axisPaint.setAntiAlias(true);
        barPaint.setAntiAlias(true);
        labelPaint.setAntiAlias(true);
        
        axisPaint.setStrokeWidth((float)STROKE_WIDTH);
        
        barPaint.setStrokeWidth((float)STROKE_WIDTH);
        barPaint.setColor(getResources().getColor(R.color.sunshine_light_blue));
        barPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        
        labelPaint.setTextSize(22f);
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
 
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int myHeight = hSpecSize;
 
        if(hSpecMode == MeasureSpec.EXACTLY) {
            myHeight = hSpecSize;
        }
        else if(hSpecMode == MeasureSpec.AT_MOST) {
            // Wrap Content
        }
 
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myWidth = wSpecSize;
        if(wSpecMode == MeasureSpec.EXACTLY) {
            myWidth = wSpecSize;
        }
        else if(wSpecMode == MeasureSpec.AT_MOST) {
            // Wrap Content
        }
 
        setMeasuredDimension(myWidth, myHeight);
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
         Log.v(LOG_TAG, "OnDraw");

        int centerX = getMeasuredWidth() / 2;

		String label = getResources().getString(R.string.windSpeed);
		float textSize = labelPaint.measureText(label);
		canvas.drawText(label, centerX - textSize, 40, labelPaint);
        
        // place the canvas origin at the bottom left
        canvas.translate(0f, getMeasuredHeight());
        canvas.scale(1, -1);
		
        p.x = LABEL_AREA_X;
        p.y = LABEL_AREA_Y;
        int axisXlength = getMeasuredWidth() - (p.x + STROKE_WIDTH);
        int axisYlength = getMeasuredHeight() - (p.y + STROKE_WIDTH);
        
        // draw x axis
		canvas.drawLine(p.x, p.y, axisXlength, p.y, axisPaint);
		
        // draw y axis
		canvas.drawLine(p.x, p.y, p.x, axisYlength, axisPaint);
		
		// draw the bar
		float left = centerX - (centerX / 2);
		float top = p.y + STROKE_WIDTH;
		float right = centerX + (centerX /2 );
		float bottom = r.top + (axisYlength * (windSpeed / MAX_WIND_SPEED));
		canvas.drawRect(left, top, right, bottom, barPaint);
		
		setContentDescription(Utility.getFormattedWind(getContext(), windSpeed, degrees));
		
		AccessibilityManager accessibilityManager =
		        (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
		if (accessibilityManager.isEnabled()) {
			sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
		}
    }
    
    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
    	event.getText().add(Utility.getFormattedWind(getContext(), windSpeed, degrees));
    	return true;
    }
 
}
