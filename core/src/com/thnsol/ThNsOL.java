package com.thnsol;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.*;
import com.badlogic.gdx.graphics.g3d.utils.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ThNsOL extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	ObjLoader loader;
	FileHandle hDC;
	Model mod, board;
	ModelInstance modins, boardins;
	ModelBatch modbatch;
	PerspectiveCamera camera;
	ModelBuilder modelBuilder;
	Environment environment;
	CameraInputController camctrl;

	@Override
	public void create() {
		batch = new SpriteBatch();
		modbatch = new ModelBatch();

		img = new Texture("badlogic.jpg");
		//hDC = Gdx.files.internal("board.obj");
		loader = new ObjLoader();
		//board = loader.loadModel(hDC);

		// ////////LIGHT//////////
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f,
				0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f,
				-0.8f, -0.2f));

		// ////////MODEL BUILD//////////
		modelBuilder = new ModelBuilder();
		mod = modelBuilder.createBox(5f, 5f, 5f,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);
		board = modelBuilder.createLineGrid(50, 50, 1f, 1f, new Material(
				ColorAttribute.createDiffuse(Color.CYAN)), Usage.Position
				| Usage.Normal);

		// ////////MODEL CONVERT//////////
		modins = new ModelInstance(mod);
		modins.transform = new Matrix4(new Vector3(0f, 10f, 0f),
				new Quaternion(0f, 0f, 0f, 0f), new Vector3(1f, 1f, 1f));
		boardins = new ModelInstance(board);

		// ////////CAMERA//////////
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		camera.near = 0.1f;
		camera.far = 300f;
		camera.position.set(10f, 10f, 10f);
		camera.lookAt(0, 0, 0);
		camera.update();

		// ////////CONTROL//////////
		camctrl = new CameraInputController(camera);
		Gdx.input.setInputProcessor(camctrl);

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		batch.begin();
		batch.draw(img, 0f, 0f);
		batch.end();

		modbatch.begin(camera);
		modbatch.render(modins, environment);
		img.bind();
		modbatch.render(boardins, environment);
		modbatch.end();

		camctrl.update();
	}

	public void dispose() {
		batch.dispose();
		img.dispose();
		mod.dispose();
		modbatch.dispose();
	}

	@Override
	public void resume() {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}
}
