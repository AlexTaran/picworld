/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl;

import vladimir.losev.HUD;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

	private MyRenderer myRenderer;
	private HUD hudSystem;

	public MyGLSurfaceView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		initValues();
	}
	
	public MyGLSurfaceView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		setEGLContextClientVersion(2);
		initValues();
	}
	
	@Override
	public void setRenderer(Renderer r) {
		super.setRenderer(r);
		myRenderer = (MyRenderer)r;
	}
	
	public void setHUD(HUD hud) {
		hudSystem = hud;
	}
	
	private boolean isPressed;
	private boolean isScaling;
	private float lastX1;
	private float lastY1;
	private float lastX2;
	private float lastY2;
	private float lastX3;
	private float lastY3;
	private int lastPointerCount;
	
	private enum ControlMode {
		NONE(0), // 0 fingers
		ROTATING(1), // 1 finger
		SCALING(2), // 2 fingers
		MOVING(3); // 3 fingers
		
		private final int numFingers;
		
		private ControlMode(int nf) {
			numFingers = nf;
		}
		
		public int getNumFingers() {
			return numFingers;
		}
		
		public static ControlMode byNumFungers(int nf) {
			switch(nf) {
			case 0: return NONE;
			case 1: return ROTATING;
			case 2: return SCALING;
			case 3: return MOVING;
			default: throw new RuntimeException("No ControlMode for such number of fingers: " + nf);
			}
		}
	}
	
	private void initValues() {
		isPressed = false;
		isScaling = false;
		lastX1 = 0.0f;
		lastY1 = 0.0f;
		lastX2 = 0.0f;
		lastY2 = 0.0f;
		lastX3 = 0.0f;
		lastY3 = 0.0f;
		lastPointerCount = 0;
	}

	@Override
	public boolean onTouchEvent(MotionEvent m) {
		if (hudSystem.onTouchEvent(m)) {
			return true;
		}
		if (m.getAction() == MotionEvent.ACTION_DOWN) {
			int pointerCount = m.getPointerCount();
			if (pointerCount >= 1 && pointerCount <= 3) {
				onBeginManipulation(ControlMode.byNumFungers(pointerCount), m);
			}
			lastPointerCount = pointerCount;
		} else if (m.getAction() == MotionEvent.ACTION_MOVE) {
			int pointerCount = m.getPointerCount();
			if (pointerCount == lastPointerCount) {
				if (pointerCount >= 1 && pointerCount <=3) {
					onContinueManipulation(ControlMode.byNumFungers(pointerCount), m);
				}
			} else {
				if (lastPointerCount >= 1 && lastPointerCount <=3) {
					onFinishManipulation(ControlMode.byNumFungers(lastPointerCount), m);
				}
				if (pointerCount >=1 && pointerCount <=3) {
					onBeginManipulation(ControlMode.byNumFungers(pointerCount), m);
				}
			}
			lastPointerCount = pointerCount;
		} else if (m.getAction() == MotionEvent.ACTION_UP) {
			int pointerCount = m.getPointerCount();
			if (pointerCount >=1 && pointerCount <=3) {
				onFinishManipulation(ControlMode.byNumFungers(pointerCount), m);
			}
			lastPointerCount = pointerCount;
		}
		/*
		if (m.getAction() == MotionEvent.ACTION_DOWN) {
			isPressed = true;
			int pointerCount = m.getPointerCount();
			lastX1 = m.getX(0);
			lastY1 = m.getY(0);
			if (pointerCount >= 2) {
				lastX2 = m.getX(1);
				lastY2 = m.getY(1);
				isScaling = true;
			} else {
				isScaling = false;
			}
			lastPointerCount = pointerCount;
		} else if (m.getAction() == MotionEvent.ACTION_MOVE) {
			if (!isPressed) {
				return false;
			}
			float x1 = m.getX(0), dx1 = x1 - lastX1;
			float y1 = m.getY(0), dy1 = y1 - lastY1;
			
			int pointerCount = m.getPointerCount();
			if (pointerCount >=2) {
				float x2 = m.getX(1);
				float y2 = m.getY(1);
				if(!isScaling || lastPointerCount == 1) {
					isScaling = true;
				} else {
					float oldDist2 = (lastX1-lastX2) * (lastX1-lastX2) + (lastY1-lastY2) * (lastY1-lastY2);
					float dist2 = (x1-x2) * (x1-x2) + (y1-y2) * (y1-y2);
					float scaleKoef = (float)Math.sqrt(dist2/oldDist2);
					myRenderer.cameraRadius *= scaleKoef;
					if (myRenderer.cameraRadius < 2.0f) {
						myRenderer.cameraRadius = 2.0f;
					}
					if (myRenderer.cameraRadius > 20.0f) {
						myRenderer.cameraRadius = 20.0f;
					}
					dx1 = (x2 + x1) / 2 - (lastX2+lastX1) / 2;
					dy1 = (y2 + y1) / 2 - (lastY2+lastY1) / 2;
				}
				lastX2 = x2;
				lastY2 = y2;
			} else if (pointerCount == 1 && isScaling) {
				lastX1 = x1;
				lastY1 = y1;
				isScaling = false;
				return true;
			}
			myRenderer.cameraPhi += dx1 / 200.0f;
			while (myRenderer.cameraPhi < 0.0f) {
				myRenderer.cameraPhi += 2.0f * Math.PI;
			}
			while (myRenderer.cameraPhi > 2.0f * Math.PI) {
				myRenderer.cameraPhi -= 2.0f * Math.PI;
			}
			myRenderer.cameraTheta += dy1 / 200.0f;
			if (myRenderer.cameraTheta > Math.PI * 0.5f) {
				myRenderer.cameraTheta = (float)Math.PI * 0.5f;
			}
			if (myRenderer.cameraTheta < -Math.PI * 0.5f) {
				myRenderer.cameraTheta = -(float)Math.PI * 0.5f;
			}
			lastX1 = x1;
			lastY1 = y1;
			lastPointerCount = pointerCount;
			//Log.d("FUCK", "Phi = "+ myRenderer.cameraPhi + "  Theta = " + myRenderer.cameraTheta);
		} else if (m.getAction() == MotionEvent.ACTION_UP) {
			isPressed = false;
		}*/
		return true;
	}
	
	private void onBeginManipulation(ControlMode cm, MotionEvent m) {
		Log.d("FUCK", "Begin Manipulation: " + cm.toString());
		if (cm == ControlMode.ROTATING) {
			lastX1 = m.getX(0);
			lastY1 = m.getY(0);
		} else if (cm == ControlMode.SCALING) {
			lastX1 = m.getX(0);
			lastY1 = m.getY(0);
			lastX2 = m.getX(1);
			lastY2 = m.getY(1);
		} else if (cm == ControlMode.MOVING) {
			lastX1 = m.getX(0);
			lastY1 = m.getY(0);
			lastX2 = m.getX(1);
			lastY2 = m.getY(1);
			lastX3 = m.getX(2);
			lastY3 = m.getY(2);
		}
	}
	
	private void onContinueManipulation(ControlMode cm, MotionEvent m) {
		if (cm == ControlMode.ROTATING) {
			float x1 = m.getX(0), dx = x1 - lastX1;
			float y1 = m.getY(0), dy = y1 - lastY1;
			
			performRotating(dx, dy);
			
			lastX1 = x1;
			lastY1 = y1;
		} else if (cm == ControlMode.SCALING) {
			float x1 = m.getX(0);
			float y1 = m.getY(0);
			float x2 = m.getX(1);
			float y2 = m.getY(1);
			float oldDist2 = (lastX1-lastX2) * (lastX1-lastX2) + (lastY1-lastY2) * (lastY1-lastY2);
			float dist2 = (x1-x2) * (x1-x2) + (y1-y2) * (y1-y2);
			float scaleKoef = (float)Math.sqrt(oldDist2/dist2);
			
			performScaling(scaleKoef);
			
			float dx = (x2 + x1) / 2 - (lastX2 + lastX1) / 2;
			float dy = (y2 + y1) / 2 - (lastY2 + lastY1) / 2;
			
			performRotating(dx, dy);
			
			lastX1 = x1;
			lastY1 = y1;
			lastX2 = x2;
			lastY2 = y2;
		}  else if (cm == ControlMode.MOVING) {
			float x1 = m.getX(0);
			float y1 = m.getY(0);
			float x2 = m.getX(1);
			float y2 = m.getY(1);
			float x3 = m.getX(2);
			float y3 = m.getY(2);
			float dx = (x1 + x2 + x3) / 3 - (lastX1 + lastX2 + lastX3) / 3;
			float dy = (y1 + y2 + y3) / 3 - (lastY1 + lastY2 + lastY3) / 3;
			
			float camrightx = -(float) Math.sin(myRenderer.cameraPhi);
			float camrightz = (float) Math.cos(myRenderer.cameraPhi);
			
			myRenderer.cameraX += camrightx * dx * myRenderer.cameraRadius / 750.0f;
			myRenderer.cameraZ += camrightz * dx * myRenderer.cameraRadius / 750.0f;
			
			float camfwdx = (float) Math.cos(myRenderer.cameraPhi);
			float camfwdz = (float) Math.sin(myRenderer.cameraPhi);
			
			myRenderer.cameraX -= camfwdx * dy * myRenderer.cameraRadius / 750.0f;
			myRenderer.cameraZ -= camfwdz * dy * myRenderer.cameraRadius / 750.0f;
			
			
			float oldSq2 = Math.abs((lastX3 - lastX1) * (lastY2 - lastY1) - (lastX2 - lastX1) * (lastY3- lastY1));
			float sq2 = Math.abs((x3 - x1) * (y2 - y1) - (x2 - x1) * (y3- y1));
			float scaleKoef = (float)Math.sqrt(oldSq2/sq2);
			
			performScaling(scaleKoef);
			
			lastX1 = x1;
			lastY1 = y1;
			lastX2 = x2;
			lastY2 = y2;
			lastX3 = x3;
			lastY3 = y3;
		}
	}
	
	private void onFinishManipulation(ControlMode cm, MotionEvent m) {
		Log.d("FUCK", "Finish Manipulation: " + cm.toString());
		// do nothing :)
	}
	
	private void performRotating(float dx, float dy) {
		myRenderer.cameraPhi += dx / 200.0f;
		while (myRenderer.cameraPhi < 0.0f) {
			myRenderer.cameraPhi += 2.0f * Math.PI;
		}
		while (myRenderer.cameraPhi > 2.0f * Math.PI) {
			myRenderer.cameraPhi -= 2.0f * Math.PI;
		}
		myRenderer.cameraTheta += dy / 200.0f;
		if (myRenderer.cameraTheta > Math.PI * 0.5f) {
			myRenderer.cameraTheta = (float)Math.PI * 0.5f;
		}
		if (myRenderer.cameraTheta < -Math.PI * 0.5f) {
			myRenderer.cameraTheta = -(float)Math.PI * 0.5f;
		}
	}
	
	private void performScaling(float scaleKoef) {
		myRenderer.cameraRadius *= scaleKoef;
		if (myRenderer.cameraRadius < 2.0f) {
			myRenderer.cameraRadius = 2.0f;
		}
		if (myRenderer.cameraRadius > 20.0f) {
			myRenderer.cameraRadius = 20.0f;
		}
	}
}
