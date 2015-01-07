package com.thnsol;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.*;
import com.badlogic.gdx.graphics.g3d.loader.*;
import com.badlogic.gdx.graphics.g3d.model.data.*;
import com.badlogic.gdx.graphics.g3d.utils.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.thnsol.CharacterCameraInputController;

public class ThNsOL extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Model mod, board;
	ModelInstance modins, boardins, tenshiins, landins;
	ModelBatch modbatch;
	PerspectiveCamera camera;
	ModelBuilder modelBuilder;
	Environment environment;
	CharacterCameraInputController camctrl;

	float mx = 0f, my = 10f, mz = 10f;

	@Override
	public void create() {
		batch = new SpriteBatch();
		modbatch = new ModelBatch();

		// ////////LIGHT//////////
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f,
				0.5f, 0.5f, 1f));
		environment.add(new DirectionalLight().set(0.5f, 0.5f, 0.5f, -1f, -1f,
				-1f));

		// ////////MODEL BUILD//////////
		modelBuilder = new ModelBuilder();
		mod = modelBuilder.createBox(5f, 5f, 5f,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);
		modins = new ModelInstance(mod);

		board = modelBuilder.createLineGrid(500, 500, 5f, 5f, new Material(
				ColorAttribute.createDiffuse(Color.CYAN)), Usage.Position
				| Usage.Normal);
		boardins = new ModelInstance(board);

		// ////////MODEL CONVERT//////////
		modins.transform.setToTranslation(0f, 10f, 0f);
		
		// ////////CAMERA//////////
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		camera.near = 0.1f;
		camera.far = 1000f;
		camera.position.set(mx + 10f, my + 10f, mz);
		camera.lookAt(mx, my, mz);
		camera.update();

		// ////////CONTROL//////////
		camctrl = new CharacterCameraInputController(camera);
		camctrl.target = camera.position;
		Gdx.input.setInputProcessor(camctrl);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modbatch.begin(camera);
		modbatch.render(modins, environment);
		modbatch.render(boardins, environment);
		modbatch.end();

		ModelUpdate();
		ControlUpdate();
		camctrl.update();
	}
	
	private ModelInstance LoadG3dModel(String Path){
		G3dModelLoader loader = new G3dModelLoader(new JsonReader());
		ModelData data = loader.loadModelData(Gdx.files.internal(Path));
		Model ModTmp = new Model(data, new TextureProvider.FileTextureProvider());
		return new ModelInstance(ModTmp);
	}
	
	private ModelInstance LoadObjModel(String Path){
		ObjLoader loader = new ObjLoader();
		Model ModTmp = loader.loadModel(Gdx.files.internal(Path));
		return new ModelInstance(ModTmp);
	}

  	private void ControlUpdate() {

		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			mz = mz - 1f;
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			mz = mz + 1f;
		}
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			mx = mx - 1f;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			mx = mx + 1f;
		}
	}

	private void ModelUpdate() {
		modins.transform.setTranslation(mx, my, mz);
	}
	public void dispose() {
		batch.dispose();
		mod.dispose();
		board.dispose();
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
