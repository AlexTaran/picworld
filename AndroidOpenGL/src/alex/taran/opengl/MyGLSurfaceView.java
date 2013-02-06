package alex.taran.opengl;

import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import alex.taran.hud.AbstractHUDSystem;
import alex.taran.opengl.ControlState.ControlMode;
import alex.taran.opengl.GameUnit.UnitState;
import alex.taran.opengl.utils.MathUtils;
import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

	private MyRenderer myRenderer;
	private AbstractHUDSystem hudSystem;

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
	
	public void setHUD(AbstractHUDSystem hud) {
		hudSystem = hud;
	}
	
	private boolean isPressed;
	private float lastX;
	private float lastY;
	
	private void initValues() {
		isPressed = false;
		lastX = 0.0f;
		lastY = 0.0f;
	}

	@Override
	public boolean onTouchEvent(MotionEvent m) {
		if (hudSystem.onTouchEvent(m)) {
			return true;
		}
		if (m.getAction() == MotionEvent.ACTION_DOWN) {
			isPressed = true;
			lastX = m.getX();
			lastY = m.getY();
		} else if (m.getAction() == MotionEvent.ACTION_MOVE) {
			if (!isPressed) {
				return false;
			}
			float x = m.getX(), dx = x - lastX;
			float y = m.getY(), dy = y - lastY;
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
			lastX = x;
			lastY = y;
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
