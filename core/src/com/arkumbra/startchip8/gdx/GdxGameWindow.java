package com.arkumbra.startchip8.gdx;


import com.arkumbra.chip8.Chip8;
import com.arkumbra.chip8.debug.Debugger;
import com.arkumbra.chip8.state.SaveStateHandler;
import com.arkumbra.chip8.external.GuiService;
import com.arkumbra.chip8.machine.KeyPressListener;
import com.arkumbra.chip8.machine.ScreenMemory;
import com.arkumbra.chip8.external.SoundService;
import com.arkumbra.startchip8.gdx.chip8.GdxSoundService;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import java.io.File;

public class GdxGameWindow extends ApplicationAdapter implements GuiService {

	// Emuluator
	private final SaveStateFileManager saveStateFileManager;
	private Chip8 chip8;
	private GdxSoundService soundService;
	private SaveStateHandler saveStateHandler;
	private Debugger debugger;
	private ScreenMemory screenMemory;

	// LibGDX
	private Environment environment;
	private PerspectiveCamera cam;
	private InputProcessor inputProcessor;
	private ModelBatch modelBatch;
	private ModelInstance[][] pixelInstances = new ModelInstance[ScreenMemory.WIDTH][ScreenMemory.HEIGHT];
	private Array<ModelInstance> modelInstances = new Array<>();


	public GdxGameWindow(SaveStateFileManager saveStateFileManager) {
		this.saveStateFileManager = saveStateFileManager;
	}

	// ===============================================================================================
	// ================================== Managing Chip 8 Emulator ===================================
	// ===============================================================================================

	public void setUpChip8() {
		soundService = new GdxSoundService();
		chip8 = new Chip8(this, soundService);
	}


	// Callback from Chip8 emulator once the emulato has been created
	@Override
	public void init(ScreenMemory screenMemory, KeyPressListener keyPressListener,
			SaveStateHandler saveStateHandler, Debugger debugger) {

		this.screenMemory = screenMemory;
		this.saveStateHandler = saveStateHandler;
		this.debugger = debugger;

		inputProcessor = new GdxInputProcessor(keyPressListener, saveStateHandler, saveStateFileManager);
		Gdx.input.setInputProcessor(inputProcessor);
	}

	public void loadGame() {
		String relativePath = "chip8/src/main/resources/BLINKY.ch8"; // TODO Add file loader
//		String relativePath = "chip8/src/main/resources/TANK.ch8"; // TODO Add file loader

		String absolutePath = new File(relativePath).getAbsolutePath();

		chip8.loadGame(absolutePath);
	}

	public void startChip8() {
		soundService.init();
		chip8.runAsync();
	}

	public Debugger getDebugger() {
		return debugger;
	}

	// ===============================================================================================
	// ===================================== LibGDX processing =======================================
	// ===============================================================================================

	@Override
	public void create () {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		// TODO turn off in 2d mode
		environment.add(new PointLight().set(Color.valueOf("CCAACC"), new Vector3(32, 16, 10), 500f));


		modelBatch = new ModelBatch();

		// Set up camera to look at the game screen face on
		cam = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(ScreenMemory.WIDTH / 2, ScreenMemory.HEIGHT / 2, -20f);
		cam.lookAt(ScreenMemory.WIDTH / 2,ScreenMemory.HEIGHT / 2,0);
		cam.near = 1f;
		cam.far = 50f;
		cam.rotate(180, 0, 0, -1);
		cam.update();


		createBoxPerPixel();
		createUiLayer();
	}

	private Stage stage;
	private VisTable table;

	private void createUiLayer() {
//		VisUI.load(SkinScale.X2);
		VisUI.load();
		stage = new Stage();
//		Gdx.input.setInputProcessor(stage);

		table = new VisTable();
		table.align(Align.topRight);
		table.setFillParent(true);
		stage.addActor(table);


		Label v0Label = new VisLabel("V0");
		table.add(v0Label);
		Label v0Data = new VisLabel("000");
		table.add(v0Data);
		table.row();
		Label v1Label = new VisLabel("V1");
		table.add(v1Label);
		Label v1Data = new VisLabel("000");
		table.add(v1Data);

		table.setDebug(true); // This is optional, but enables debug lines for tables.
	}

	private void createBoxPerPixel() {
		ColorAttribute emissive = new ColorAttribute(ColorAttribute.Emissive, Color.valueOf("001100"));
		ColorAttribute diffuse = new ColorAttribute(ColorAttribute.Diffuse, Color.valueOf("22CB43"));
		ColorAttribute ambient = new ColorAttribute(ColorAttribute.Ambient, Color.valueOf("AA66CC"));
		ColorAttribute reflective = new ColorAttribute(ColorAttribute.Reflection, Color.valueOf("00CCAA"));
		Material material = new Material(emissive, diffuse, ambient, reflective);

		ModelBuilder modelBuilder = new ModelBuilder();
		// TODO change to depth of 0.05 in 2d mode
		Model model = modelBuilder.createBox(1f, 1f, 0.05f,
				material,
				Usage.Position | Usage.Normal | Usage.TextureCoordinates);

		for (int y = 0; y < pixelInstances[0].length; y++) {
			for (int x = 0; x < pixelInstances.length; x++) {
				ModelInstance modelInstance = createModelInstancePerPixelAndTransformToLocation(model, x, y);
				pixelInstances[x][y] = modelInstance;
				modelInstances.add(modelInstance);
			}
		}
	}

	private ModelInstance createModelInstancePerPixelAndTransformToLocation(Model model, int x, int y) {
		ModelInstance instance = new ModelInstance(model);
		instance.transform.translate(
				x,
				y,
				0
		);

		return instance;
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		boolean[][] screenPixels = screenMemory.getPixels();
		Array<ModelInstance> modelInstancesForFrame = new Array<>();
		for (int y = 0; y < screenPixels[0].length; y++) {
			for (int x = 0; x < screenPixels.length; x++) {
				if (screenPixels[x][y]) {
					modelInstancesForFrame.add(pixelInstances[x][y]);
				}
			}
		}

		modelBatch.begin(cam);
		modelBatch.render(modelInstancesForFrame, environment);
		// render UI
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		modelBatch.end();
	}
	
	@Override
	public void dispose() {
		modelBatch.dispose();
		VisUI.dispose();
	}


}