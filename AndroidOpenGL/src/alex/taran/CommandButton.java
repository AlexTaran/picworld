/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran;

import alex.taran.opengl.R;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class CommandButton extends ImageView {
	private int initial_margin_left;
	private int initial_margin_top;
	
	private ObjectAnimator animatorX = null;
	private ObjectAnimator animatorY = null;
	
	public CommandButton(Context context) {
		super(context);
	}
	
	public CommandButton(Context context, AttributeSet attrSet) {
		super(context, attrSet);
		postInit(context, attrSet, 0);
	}

	public CommandButton(Context context, AttributeSet attrSet, int defStyle) {
		super(context, attrSet, defStyle);
		postInit(context, attrSet, defStyle);
	}
	
	private void postInit(Context context, AttributeSet attrSet, int defStyle) {
		TypedArray a = context.obtainStyledAttributes(attrSet, R.styleable.CommandButton);
		initial_margin_left = a.getDimensionPixelSize(R.styleable.CommandButton_android_layout_marginLeft, -100500);
		initial_margin_top  = a.getDimensionPixelSize(R.styleable.CommandButton_android_layout_marginTop, -100500);
	}
	
	public void beginTranslationAnimationTo(int x, int y, long duration) {
		cancelAllAnimations();
		animatorX = ObjectAnimator.ofInt(this, "layoutMarginLeft", getLayoutMarginLeft(), x).setDuration(duration);
		animatorY = ObjectAnimator.ofInt(this, "layoutMarginTop", getLayoutMarginTop(), y).setDuration(duration);
		animatorX.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				animatorX = null;
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		animatorY.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				animatorY = null;
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		animatorX.start();
		animatorY.start();
	}
	
	public void cancelAllAnimations() {
		if (animatorX != null) {
			animatorX.cancel();
		}
		if (animatorY != null) {
			animatorY.cancel();
		}
	}
	
	public int getInitialMarginLeft() {
		return initial_margin_left;
	}
	
	public int getInitialMarginTop() {
		return initial_margin_top;
	}
	
	public int getLayoutMarginLeft() {
		return ((LayoutParams) this.getLayoutParams()).leftMargin;
	}
	
	public int getLayoutMarginTop() {
		return ((LayoutParams) this.getLayoutParams()).topMargin;
	}
	
	public void setLayoutMarginLeft(int value) {
		LayoutParams params = (LayoutParams) this.getLayoutParams();
		params.leftMargin = value;
		setLayoutParams(params);
	}
	
	public void setLayoutMarginTop(int value) {
		LayoutParams params = (LayoutParams) this.getLayoutParams();
		params.topMargin = value;
		setLayoutParams(params);
	}
}
