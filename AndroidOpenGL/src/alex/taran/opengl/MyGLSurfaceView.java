package alex.taran.opengl;

import vladimir.losev.HUD;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
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
	private int lastPointerCount;
	
	private void initValues() {
		isPressed = false;
		isScaling = false;
		lastX1 = 0.0f;
		lastY1 = 0.0f;
		lastX2 = 0.0f;
		lastY2 = 0.0f;
		lastPointerCount = 0;
	}

	@Override
	public boolean onTouchEvent(MotionEvent m) {
		if (hudSystem.onTouchEvent(m)) {
			return true;
		}
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
		}
		/*ControlMode newControlMode = null;
		if (m.getAction() == MotionEvent.ACTION_DOWN) {
			pointers.clear();
			lastPointerCount = 0;
		}

		// Main buttons have main priority
		if (m.getPointerCount() == 1) {
			if (MathUtils.isPointInRect(
					m.getX(),
					m.getY(),
					controlState.surfaceSizes.x
							- controlState.countButtonSize(),
					controlState.surfaceSizes.y
							- controlState.countButtonSize(),
					controlState.surfaceSizes.x, controlState.surfaceSizes.y)) {
				controlState.controlMode = ControlMode.SCALING;
				return true;
			} else if (MathUtils.isPointInRect(
					m.getX(),
					m.getY(),
					controlState.surfaceSizes.x - 2.0f
							* controlState.countButtonSize(),
					controlState.surfaceSizes.y
							- controlState.countButtonSize(),
					controlState.surfaceSizes.x
							- controlState.countButtonSize(),
					controlState.surfaceSizes.y)) {
				controlState.controlMode = ControlMode.SELECT;
				return true;
			} else if (MathUtils.isPointInRect(
					m.getX(),
					m.getY(),
					controlState.surfaceSizes.x - 3.0f
							* controlState.countButtonSize(),
					controlState.surfaceSizes.y
							- controlState.countButtonSize(),
					controlState.surfaceSizes.x -2.0f
							* controlState.countButtonSize(),
					controlState.surfaceSizes.y)) {
				controlState.controlMode = ControlMode.DECLARE_GO;
				return true;
			}
		}
		// Two fingers work always
		if (m.getPointerCount() == 2 && lastPointerCount == 2) {
			//cleaning up a selection curve
			synchronized (controlState.selectionCurve) {
				controlState.selectionCurve.clear();
				controlState.selectionCurve.add(new PointF(m.getX(), m
						.getY()));
			}
			float l1 = MathUtils.sqr(m.getX(0) - m.getX(1))
					+ MathUtils.sqr(m.getY(0) - m.getY(1));
			float l2 = MathUtils.sqr(pointers.get(0).x - pointers.get(1).x)
					+ MathUtils.sqr(pointers.get(0).y - pointers.get(1).y);
			float scaleRatio = (float) Math.sqrt(l1 / l2);
			PointF center = new PointF(0.5f * (m.getX(0) + m.getX(1)),
					0.5f * (m.getY(0) + m.getY(1)));
			PointF lastCenter = new PointF(
					0.5f * (pointers.get(0).x + pointers.get(1).x),
					0.5f * (pointers.get(0).y + pointers.get(1).y));
			// In local coords
			PointF lastCenterLocal = new PointF(
					(lastCenter.x - controlState.offset.x) / controlState.scale,
					(lastCenter.y - controlState.offset.y) / controlState.scale);
			controlState.scale *= scaleRatio;
			// checking for min/max scale
			float minScale = Math.max(
					controlState.surfaceSizes.x / gameWorld.gameMap.sizes.x,
					(controlState.surfaceSizes.y - controlState
							.countButtonSize()) / gameWorld.gameMap.sizes.y);
			if (controlState.scale <= minScale) {
				controlState.scale = minScale;
			}
			if (controlState.scale >= minScale * 5.0f) {
				controlState.scale = minScale * 5.0f;
			}
			// moving field
			controlState.offset.x = center.x - lastCenterLocal.x
					* controlState.scale;
			controlState.offset.y = center.y - lastCenterLocal.y
					* controlState.scale;
		}
		// Mode-specific things
		if (controlState.controlMode == ControlMode.SCALING) {
			if (m.getPointerCount() == 1 && lastPointerCount == 1) {
				float dx = m.getX(0) - pointers.get(0).x;
				float dy = m.getY(0) - pointers.get(0).y;
				controlState.offset.x += dx;
				controlState.offset.y += dy;
			}
		}
		if (controlState.controlMode == ControlMode.SELECT) {
			if (m.getAction() == MotionEvent.ACTION_DOWN) {
				controlState.selectedPoints.clear();
				synchronized (controlState.selectionCurve) {
					controlState.selectionCurve.clear();
					controlState.selectionCurve.add(new PointF(m.getX(), m
							.getY()));
				}
			}
			if (m.getAction() == MotionEvent.ACTION_MOVE) {
				synchronized (controlState.selectionCurve) {
					PointF p = new PointF(m.getX(), m.getY());
					PointF last = controlState.selectionCurve
							.get(controlState.selectionCurve.size() - 1);
					PointF prev = null;
					boolean intersection = false;
					for (PointF curr : controlState.selectionCurve) {
						if (curr == last) {
							break;
						}
						if (prev == null) {
							prev = curr;
							continue;
						}
						intersection = MathUtils.isSegmentsIntersect(p, last,
								curr, prev);
						if (intersection == true) {
							break;
						}
						prev = curr;
					}
					if (intersection == true) {
						while (controlState.selectionCurve.size() >= 1) {
							if (controlState.selectionCurve.get(0) == prev) {
								break;
							}
							controlState.selectionCurve.remove(0);
						}
						controlState.selectionCurve.remove(0);
						List<PointF> curve = controlState.selectionCurve;
						for (PointF pt : curve) {
							pt.x -= controlState.offset.x;
							pt.y -= controlState.offset.y;
							pt.x /= controlState.scale;
							pt.y /= controlState.scale;
						}
						recountSelection(curve);
						newControlMode = ControlMode.DECLARE_GO;
						controlState.selectionCurve.clear();
					}
					controlState.selectionCurve.add(p);
				}
			}
			if (m.getAction() == MotionEvent.ACTION_UP) {
				synchronized (controlState.selectionCurve) {
					controlState.selectionCurve.clear();
				}
			}
		}
		if (controlState.controlMode == ControlMode.DECLARE_GO) {
			if (m.getAction() == MotionEvent.ACTION_DOWN) {
				PointF target = new PointF((m.getX() - controlState.offset.x)
						/ controlState.scale,
						(m.getY() - controlState.offset.y) / controlState.scale);
				synchronized (controlState.selectedPoints) {
					float diam = 2.5f*(float) Math.sqrt(controlState.selectedPoints.size());
					Random r = new Random(SystemClock.elapsedRealtime());
					for (Integer i : controlState.selectedPoints) {
						GameUnit u = gameWorld.units.get(i);
						if (u != null) {
							u.target = new PointF(target.x+(r.nextFloat()-0.5f)*diam,target.y+(r.nextFloat()-0.5f)*diam);
							u.state = UnitState.Attack;
						}
					}
				}
				newControlMode = ControlMode.SELECT;
			}
		}
		// checking for bounds ANYWAY
		if (controlState.offset.x >= 0.0f) {
			controlState.offset.x = 0.0f;
		}
		if (controlState.offset.y >= 0.0f) {
			controlState.offset.y = 0.0f;
		}
		if (controlState.offset.x + gameWorld.gameMap.sizes.x
				* controlState.scale <= controlState.surfaceSizes.x) {
			controlState.offset.x = controlState.surfaceSizes.x
					- gameWorld.gameMap.sizes.x * controlState.scale;
		}
		if (controlState.offset.y + gameWorld.gameMap.sizes.y
				* controlState.scale + controlState.countButtonSize() <= controlState.surfaceSizes.y) {
			controlState.offset.y = controlState.surfaceSizes.y
					- gameWorld.gameMap.sizes.y * controlState.scale
					- controlState.countButtonSize();
		}

		// and finally remember last pointer
		lastPointerCount = m.getPointerCount();
		pointers.clear();
		for (int i = 0; i < lastPointerCount; ++i) {
			pointers.add(new PointF(m.getX(i), m.getY(i)));
		}
		if(newControlMode!=null){
			controlState.controlMode = newControlMode;
		}*/
		return true;
	}
}
