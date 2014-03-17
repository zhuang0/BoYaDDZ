package com.thws.boyaddz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener {

	boolean threadFlag = true;
	Desk desk;
	Context context;
	SurfaceHolder holder;
	Canvas canvas;
	Bitmap backgroundBitmap;

	Thread gameThread = new Thread() {
		@SuppressLint("WrongCall")
		@Override
		public void run() {
			holder = getHolder();
			while (threadFlag) {
				desk.gameLogic();
				try {
					canvas = holder.lockCanvas();
					onDraw(canvas);
				} finally {
					holder.unlockCanvasAndPost(canvas);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	};

	public GameView(Context context) {
		super(context);
		this.context = context;
		desk = new Desk(context);
		backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_bg);
		this.getHolder().addCallback(this);
		this.setOnTouchListener(this);
	}
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		Rect src = new Rect();
		Rect des = new Rect();
		src.set(0, 0, backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
		des.set(0, 0, MainActivity.SCREEN_WIDTH, MainActivity.SCREEN_HEIGHT);
		canvas.drawBitmap(backgroundBitmap, src, des, null);
		desk.controlPaint(canvas);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		threadFlag = true;
		gameThread.start();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		threadFlag = false;
		boolean retry = true;
		while (retry) {// 循环
			try {
				gameThread.join();// 等待线程结束
				retry = false;// 停止循环
			} catch (InterruptedException e) {
			}// 不断地循环，直到刷帧线程结束
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_UP) {
			return true;
		}
		int x = (int) event.getX();
		int y = (int) event.getY();
		System.out.println("ACTION_UP -- " + (event.getAction() == MotionEvent.ACTION_UP));
		desk.onTuch(x, y);
		// threadFlag=!threadFlag;
		return true;
	}

}
