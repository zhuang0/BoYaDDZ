package com.thws.boyaddz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class CardsHolder {

	int value = 0;
	int cardsType = 0;
	int[] cards;
	Bitmap cardImage;
	int playerId;
	Context context;

	public CardsHolder(int[] cards, int id, Context context) {
		this.playerId = id;
		this.cards = cards;
		this.context = context;
		cardsType = CardsManager.getType(cards);
		value = CardsManager.getValue(cards);
		// 如果有炸弹牌出现，分数翻倍
		if (cardsType == CardsType.huojian || cardsType == CardsType.zhadan) {
			Desk.multiple *= 2;
		}
	}

	public void paint(Canvas canvas, int left, int top, int dir) {

		Rect src = new Rect();
		Rect des = new Rect();
		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(1);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		for (int i = 0; i < cards.length; i++) {
			int row = CardsManager.getImageRow(cards[i]);
			int col = CardsManager.getImageCol(cards[i]);
			cardImage = BitmapFactory.decodeResource(context.getResources(),
					CardImage.cardImages[row][col]);
			if (dir == CardsType.direction_Vertical) {
				row = CardsManager.getImageRow(cards[i]);
				col = CardsManager.getImageCol(cards[i]);
				src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
				des.set((int) (left * MainActivity.SCALE_HORIAONTAL),
						(int) ((top + i * 15) * MainActivity.SCALE_VERTICAL),
						(int) ((left + 40) * MainActivity.SCALE_HORIAONTAL),
						(int) ((top + 60 + i * 15) * MainActivity.SCALE_VERTICAL));
			}
			else {
				row = CardsManager.getImageRow(cards[i]);
				col = CardsManager.getImageCol(cards[i]);
				src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
				des.set((int) ((left + i * 20) * MainActivity.SCALE_HORIAONTAL),
						(int) (top * MainActivity.SCALE_VERTICAL),
						(int) ((left + 40 + i * 20) * MainActivity.SCALE_HORIAONTAL),
						(int) ((top + 60) * MainActivity.SCALE_VERTICAL));
			}
			RectF rectF = new RectF(des);
			canvas.drawRoundRect(rectF, 5, 5, paint);
			canvas.drawBitmap(cardImage, src, des, paint);

		}

	}
}
