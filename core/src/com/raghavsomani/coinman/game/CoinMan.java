package com.raghavsomani.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import org.omg.PortableInterceptor.Interceptor;
import org.w3c.dom.css.Rect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int score=0;
	int manState=0;
	int pause=0;
	float gravity=0.5f;
	float velocity=0;
	int manY;
	float y;
	int k=-1;
	GlyphLayout layout;
	ArrayList<Integer> coinYs=new ArrayList<Integer>();
	ArrayList<Integer> coinXs=new ArrayList<Integer>();
	int coinCount=0;
	Texture coin;
	Texture dizzy;
	Random random;
	float x;
	Texture bomb;
	int bombCount=0;
	ArrayList<Integer> bombYs=new ArrayList<Integer>();
	ArrayList<Integer> bombXs=new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangle=new ArrayList<Rectangle>();
	ArrayList<Rectangle> bombRectangle=new ArrayList<Rectangle>();
	Rectangle manRectangle;
	BitmapFont font;
	BitmapFont gameOver;
	int gameState=0;
	Sound bombSound;
	Sound coinSound;
	Sound bgm;
	Sound jump;
	@Override
	public void create () {
		bombSound = Gdx.audio.newSound(Gdx.files.internal("Explosion+1.mp3"));
		coinSound = Gdx.audio.newSound(Gdx.files.internal("coin.wav"));
		bgm=Gdx.audio.newSound(Gdx.files.internal("bgm.wav"));
		jump=Gdx.audio.newSound(Gdx.files.internal("jump.wav"));
		batch = new SpriteBatch();
		layout = new GlyphLayout();
		background =  new Texture("bg.png");
		man=new Texture[4];
		man[0]=new Texture("frame-1.png");
		man[1]=new Texture("frame-2.png");
		man[2]=new Texture("frame-3.png");
		man[3]=new Texture("frame-4.png");
		manY=Gdx.graphics.getHeight()/2;
		coin=new Texture("coin.png");
		dizzy=new Texture("dizzy-1.png");
		bomb=new Texture("bomb.png");
		random=new Random();
		font=new BitmapFont();
		gameOver=new BitmapFont();
		font.setColor(Color.BLUE);
		font.getData().scale(10);
		gameOver.setColor(Color.RED);
		gameOver.getData().scale(5);
	}


	public void makeCoin(){
		y=random.nextFloat()*Gdx.graphics.getHeight();
		int Y=(int) y;
		coinYs.add(Y);
		coinXs.add(Gdx.graphics.getWidth());
	}
	public void makeBombs(){
		x=random.nextFloat()*Gdx.graphics.getHeight();
		int X=(int) x;
		bombYs.add(X);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState==0){
			//Game is Live
			bgm.play();
			if (coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}
			if (bombCount < 150) {
				bombCount++;
			} else {
				bombCount = 0;
				makeBombs();
			}
			coinRectangle.clear();
			for (int i = 0; i < coinXs.size(); i++) {
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 7);
				coinRectangle.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}
			bombRectangle.clear();
			for (int i = 0; i < bombXs.size(); i++) {
			/*int x1 = bombXs.get(i);
			int y1 = bombYs.get(i);*/
				int flag = 0;
			/*for (int j = 0; j < coinXs.size(); j++) {
				if (coinXs.get(j) == x1 && coinYs.get(j) == y1) {
					flag = 1;
					break;
				}*/
				if (flag == 0) {
					batch.draw(bomb, bombXs.get(i), bombYs.get(i));
					bombXs.set(i, bombXs.get(i) - 12);
					bombRectangle.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
				}
			}
			//}
			if (Gdx.input.justTouched()) {
				jump.play();
				velocity = -10;
			}
			if (pause <= 8) {
				pause++;
			} else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}
			velocity += gravity;
			manY -= velocity;
			if (manY < 0)
				manY = 0;
		}

		if(gameState==1){
			//Waiting to start
			if(Gdx.input.justTouched()){

				gameState=0;
			}
		}

		if(gameState==2){
			//Game Over Screen

			if(Gdx.input.justTouched()){
				gameState=1;
				velocity=0;
				manY=Gdx.graphics.getHeight()/2;
				coinYs.clear();
				coinXs.clear();
				bombXs.clear();
				bombYs.clear();
				coinRectangle.clear();
				bombRectangle.clear();
				coinCount=0;
				bombCount=0;
				score=0;

			}
		}


		batch.draw(man[manState], (Gdx.graphics.getWidth() - man[manState].getWidth()) / 2, manY);
		manRectangle = new Rectangle((Gdx.graphics.getWidth() - man[manState].getWidth()) / 2, manY, man[manState].getWidth(), man[manState].getHeight());


		for (int i = 0; i < coinRectangle.size(); i++) {
			if (Intersector.overlaps(manRectangle, coinRectangle.get(i))) {
				//Gdx.app.log("Coin", "Collison");
				coinSound.play();
				score++;
				coinRectangle.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}


		for (int i = 0; i < bombRectangle.size(); i++) {
			if (Intersector.overlaps(manRectangle, bombRectangle.get(i))) {
				//Gdx.app.log("Bomb", "Collison");
               //bgm.stop();

				gameState=2;
				batch.draw(dizzy,(Gdx.graphics.getWidth() - man[manState].getWidth()) / 2, manY);
				layout.setText(font, "Game Over!!\n Tap to Play Again!!");
				float height = layout.height;
				float width = layout.width;
				gameOver.draw(batch,"Game Over!!\nTap to Play Again!!",(Gdx.graphics.getWidth()/2-width/2)+400,Gdx.graphics.getHeight()/2);
				k=i;
				break;
			}

		}


		font.draw(batch,String.valueOf(score),100,200);
		batch.end();
	}
	@Override
	public void dispose () {
		batch.dispose();

	}
}
