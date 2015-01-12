package com.thnsol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CharacterCameraInputController extends GestureDetector {

    public int rotateButton = Buttons.LEFT;
    public float rotateAngle = 360f;
    public int translateButton = Buttons.RIGHT;
    public float translateUnits = 10f;
    public int forwardButton = Buttons.MIDDLE;
    public int activateKey = 0;
    protected boolean activatePressed;
    public boolean alwaysScroll = true;
    public float scrollFactor = -0.1f;
    public float pinchZoomFactor = 10f;
    public boolean autoUpdate = true;
    public Vector3 target = new Vector3();
    public boolean translateTarget = true;
    public boolean forwardTarget = true;
    public boolean scrollTarget = false;
    public int forwardKey = Keys.W;
    protected boolean forwardPressed;
    public int backwardKey = Keys.S;
    protected boolean backwardPressed;
    public int goLeftKey = Keys.A;
    protected boolean goLeftPressed;
    public int goRightKey = Keys.D;
    protected boolean goRightPressed;
    public int rotateRightKey = Keys.Q;
    protected boolean rotateRightPressed;
    public int rotateLeftKey = Keys.E;
    protected boolean rotateLeftPressed;
    public Camera camera;
    protected int button = -1;
    private float startX, startY;
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();

    protected static class CameraGestureListener extends GestureAdapter {
        public CharacterCameraInputController controller;
        private float previousZoom;

        @Override
        public boolean touchDown (float x, float y, int pointer, int button) {
            previousZoom = 0;
            return false;
        }

        @Override
        public boolean tap (float x, float y, int count, int button) {
            return false;
        }

        @Override
        public boolean longPress (float x, float y) {
            return false;
        }

        @Override
        public boolean fling (float velocityX, float velocityY, int button) {
            return false;
        }

        @Override
        public boolean pan (float x, float y, float deltaX, float deltaY) {
            return false;
        }

        @Override
        public boolean zoom (float initialDistance, float distance) {
            float newZoom = distance - initialDistance;
            float amount = newZoom - previousZoom;
            previousZoom = newZoom;
            float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
            return controller.pinchZoom(amount / ((w > h) ? h : w));
        }

        @Override
        public boolean pinch (Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            return false;
        }
    };

    protected final CameraGestureListener gestureListener;

    protected CharacterCameraInputController(final CameraGestureListener gestureListener, final Camera camera) {
        super(gestureListener);
        this.gestureListener = gestureListener;
        this.gestureListener.controller = this;
        this.camera = camera;
    }

    public CharacterCameraInputController(final Camera camera) {
        this(new CameraGestureListener(), camera);
    }

    public void update () {
        if (rotateRightPressed || rotateLeftPressed || goLeftPressed|| goRightPressed || forwardPressed || backwardPressed) {
            final float delta = Gdx.graphics.getDeltaTime();
            if (rotateRightPressed) {
                //camera.rotate(camera.up, -delta * rotateAngle);
                camera.rotate(Vector3.Y, delta * rotateAngle);
            }
            if (rotateLeftPressed) {
                //camera.rotate(camera.up, delta * rotateAngle);
                camera.rotate(Vector3.Y, -delta * rotateAngle);
            }
            if (forwardPressed) {
                Vector3 t = tmpV1.set(camera.direction).scl(delta * translateUnits);
                t.y = 0;
                camera.translate(t);
                if (forwardTarget) target.add(tmpV1);
            }
            if (backwardPressed) {
                Vector3 t = tmpV1.set(camera.direction).scl(-delta * translateUnits);
                t.y = 0;
                camera.translate(t);
                if (forwardTarget) target.add(tmpV1);
            }

            if (goLeftPressed) {
                Vector3 v = camera.direction.cpy();
                v.rotate(Vector3.Y,-90);
                Vector3 t = tmpV1.set(v).scl(-delta * translateUnits);
                t.y = 0;
                camera.translate(t);
                if (forwardTarget) target.add(tmpV1);
            }

            if (goRightPressed) {
                Vector3 v = camera.direction.cpy();
                v.rotate(Vector3.Y,90);
                Vector3 t = tmpV1.set(v).scl(-delta * translateUnits);
                t.y = 0;
                camera.translate(t);
                if (forwardTarget) target.add(tmpV1);
            }
            if (autoUpdate) camera.update();
        }
    }

    private int touched;
    private boolean multiTouch;

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        touched |= (1 << pointer);
        multiTouch = !MathUtils.isPowerOfTwo(touched);
        if (multiTouch)
            this.button = -1;
        else if (this.button < 0 && (activateKey == 0 || activatePressed)) {
            startX = screenX;
            startY = screenY;
            this.button = button;
        }
        return super.touchDown(screenX, screenY, pointer, button) || activatePressed;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        touched &= -1 ^ (1 << pointer);
        multiTouch = !MathUtils.isPowerOfTwo(touched);
        if (button == this.button) this.button = -1;
        return super.touchUp(screenX, screenY, pointer, button) || activatePressed;
    }

    protected boolean process (float deltaX, float deltaY, int button) {
        if (button == rotateButton) {
            tmpV1.set(camera.direction).crs(camera.up).y = 0f;
            camera.rotateAround(target, tmpV1.nor(), deltaY * rotateAngle);
            camera.rotateAround(target, Vector3.Y, deltaX * -rotateAngle);
        } else if (button == translateButton) {
            camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits));
            camera.translate(tmpV2.set(camera.up).scl(-deltaY * translateUnits));
            if (translateTarget) target.add(tmpV1).add(tmpV2);
        } else if (button == forwardButton) {
            camera.translate(tmpV1.set(camera.direction).scl(deltaY * translateUnits));
            if (forwardTarget) target.add(tmpV1);
        }
        if (autoUpdate) camera.update();
        return true;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        boolean result = super.touchDragged(screenX, screenY, pointer);
        if (result || this.button < 0) return result;
        final float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
        final float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
        startX = screenX;
        startY = screenY;
        return process(deltaX, deltaY, button);
    }

    @Override
    public boolean scrolled (int amount) {
        return zoom(amount * scrollFactor * translateUnits);
    }

    public boolean zoom (float amount) {
        if (!alwaysScroll && activateKey != 0 && !activatePressed) return false;
        camera.translate(tmpV1.set(camera.direction).scl(amount));
        if (scrollTarget) target.add(tmpV1);
        if (autoUpdate) camera.update();
        return true;
    }

    protected boolean pinchZoom (float amount) {
        return zoom(pinchZoomFactor * amount);
    }

    @Override
    public boolean keyDown (int keycode) {
        if (keycode == activateKey) activatePressed = true;
        if (keycode == forwardKey)
            forwardPressed = true;
        else if (keycode == backwardKey)
            backwardPressed = true;
        else if (keycode == goLeftKey)
            goLeftPressed = true;
        else if (keycode == goRightKey)
            goRightPressed = true;
        else if (keycode == rotateRightKey)
            rotateRightPressed = true;
        else if (keycode == rotateLeftKey)
            rotateLeftPressed = true;
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        if (keycode == activateKey) {
            activatePressed = false;
            button = -1;
        }
        if (keycode == forwardKey)
            forwardPressed = false;
        else if (keycode == backwardKey)
            backwardPressed = false;
        else if (keycode == goLeftKey)
            goLeftPressed = false;
        else if (keycode == goRightKey)
            goRightPressed = false;
        else if (keycode == rotateRightKey)
            rotateRightPressed = false;
        else if (keycode == rotateLeftKey)
            rotateLeftPressed = false;
        return false;
    }
}
