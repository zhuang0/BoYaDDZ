package com.thws.boyaddz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class MenuView extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener {

	SurfaceHolder holder;
	Canvas canvas;
	boolean threadFlag = true;
	Bitmap background;
	Context context;
	private int x = 270;
	private int y = 50;
	private Bitmap[] menuItems;
	public MenuView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private void init() {
		menuItems = new Bitmap[5];
		holder = getHolder();
		background = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu_bg);
		menuItems[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu1);
		menuItems[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu2);
		menuItems[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu3);
		menuItems[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu4);
		menuItems[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu5);
		new GameView(context);
		this.getHolder().addCallback(this);
		this.setOnTouchListener(this);
	}

	Thread menuThread = new Thread() {
		@SuppressLint("WrongCall")
		@Override
		public void run() {

			while (threadFlag) {
				try {
					canvas = holder.lockCanvas();
					synchronized (this) {
						onDraw(canvas);
					}
					// System.out.println("menuThread");
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

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		Rect src = new Rect();
		Rect des = new Rect();
		src.set(0, 0, background.getWidth(), background.getHeight());
		// System.out.println("menu:" + background.getWidth() + "X" +
		// background.getHeight());
		des.set(0, 0, MainActivity.SCREEN_WIDTH, MainActivity.SCREEN_HEIGHT);
		Paint paint = new Paint();
		canvas.drawBitmap(background, src, des, paint);
		for (int i = 0; i < menuItems.length; i++) {
			canvas.drawBitmap(menuItems[i], (int) (x * MainActivity.SCALE_HORIAONTAL),
					(int) ((y + i * 43) * MainActivity.SCALE_VERTICAL), paint);
		}

	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		threadFlag = true;
		menuThread.start();
		System.out.println("surfaceCreated");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		threadFlag = false;
		boolean retry = true;
		while (retry) {// 循环
			try {
				menuThread.join();// 等待线程结束
				retry = false;// 停止循环
			} catch (InterruptedException e) {
			}// 不断地循环，直到刷帧线程结束
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int ex = (int) event.getX();
		int ey = (int) event.getY();
		System.out.println(event.getX() + "," + event.getY());
		int selectIndex = -1;
		for (int i = 0; i < menuItems.length; i++) {
			System.out.println(x + "  " + (y + i * 43));
			if (CardsManager.inRect(ex, ey, (int) (x * MainActivity.SCALE_HORIAONTAL),
					(int) ((y + i * 43) * MainActivity.SCALE_VERTICAL),
					(int) (125 * MainActivity.SCALE_HORIAONTAL),
					(int) (33 * MainActivity.SCALE_VERTICAL))) {
				selectIndex = i;
				break;
			}
		}
		System.out.println(selectIndex);
		switch (selectIndex) {
			case 0 :
				MainActivity.handler.sendEmptyMessage(MainActivity.GAME);
				break;
			case 1 :
				break;
			case 2 :
				break;
			case 3 :
				break;
			case 4 :
				MainActivity.handler.sendEmptyMessage(MainActivity.EXIT);
				break;
		}
		return super.onTouchEvent(event);
	}
}
