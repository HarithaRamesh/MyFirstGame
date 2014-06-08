package haritha.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class MyFirstGame extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
	
	
	Texture dropImage;
	Texture bucketImage;
	Sound dropSound;
	Music rainMusic;
	
	Rectangle bucket;
	Array<Rectangle> raindrops;
	
	long lastDropTime;
	private void spawnRainDrop(){
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
	    raindrop.width = 64;
	    raindrop.height = 64;
	    raindrops.add(raindrop);
	    lastDropTime = TimeUtils.nanoTime();
	}
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		// load the images for the droplet and the bucket, 64x64 pixels each
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		// start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();
		
		//camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		//create the bucket
		bucket = new Rectangle();
		bucket.x = 800/2 - 64/2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;
		
		raindrops = new Array<Rectangle>();
		spawnRainDrop();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		
		//for touch and mouse
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
		      touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		      camera.unproject(touchPos);
		      bucket.x = touchPos.x - 64 / 2;
		}

		//for keyboard
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {bucket.x -= 200 * Gdx.graphics.getDeltaTime();}
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {bucket.x += 200 * Gdx.graphics.getDeltaTime();}

		//to make sure it doesn't go off screen
		if(bucket.x < 0){ bucket.x = 0;}
		if(bucket.x > 800 - 64){ bucket.x = 800 - 64;}

		//raindrops
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) {spawnRainDrop();}

		java.util.Iterator<Rectangle> iter = raindrops.iterator();
		while(iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0) iter.remove();
			if(raindrop.overlaps(bucket)) {
		         dropSound.play();
		         iter.remove();
		      }
		}
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();
	}
	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
	  @Override
	   public void resize(int width, int height) {
	   }

	   @Override
	   public void pause() {
	   }

	   @Override
	   public void resume() {
	   }
}
