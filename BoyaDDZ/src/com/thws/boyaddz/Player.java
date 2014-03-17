package com.thws.boyaddz;

import android.annotation.SuppressLint;
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

public class Player {

	// 玩家手中的牌
	int[] cards;

	// 玩家选中牌的标志
	boolean[] cardsFlag;

	// 玩家ID
	int playerId;

	// 当前玩家
	int currentId;

	// 当前轮回
	int currentCircle;

	// 玩家所在桌面上的坐标
	int top, left;

	// 玩家所在桌子的实例
	Desk desk;

	// 玩家最新一手牌
	CardsHolder latestCards;

	// 桌面最新的一手牌
	CardsHolder cardsOnDesktop;

	// Context
	Context context;

	int paintDirection = CardsType.direction_Vertical;
	Bitmap cardImage;

	private Player last;
	private Player next;

	public Player(int[] cards, int left, int top, int paintDir, int id, Desk desk, Context context) {
		this.desk = desk;
		this.playerId = id;
		this.cards = cards;
		this.context = context;
		cardsFlag = new boolean[cards.length];
		this.setLeftAndTop(left, top);
		this.paintDirection = paintDir;
	}

	public void setLeftAndTop(int left, int top) {
		this.left = left;
		this.top = top;
	}

	// 设置玩家上下家关系
	public void setLastAndNext(Player last, Player next) {
		this.last = last;
		this.next = next;
	}

	// 绘制玩家手中的牌
	public void paint(Canvas canvas) {
		System.out.println("id:" + playerId);
		Rect src = new Rect();
		Rect des = new Rect();

		int row;
		int col;

		// 当玩家是NPC时，竖向绘制，扑克牌全是背面
		if (paintDirection == CardsType.direction_Vertical) {
			Paint paint = new Paint();
			paint.setStyle(Style.STROKE);
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(1);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			Bitmap backImage = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.card_bg);

			src.set(0, 0, backImage.getWidth(), backImage.getHeight());
			des.set((int) (left * MainActivity.SCALE_HORIAONTAL),
					(int) (top * MainActivity.SCALE_VERTICAL),
					(int) ((left + 40) * MainActivity.SCALE_HORIAONTAL),
					(int) ((top + 60) * MainActivity.SCALE_VERTICAL));
			RectF rectF = new RectF(des);
			canvas.drawRoundRect(rectF, 5, 5, paint);
			canvas.drawBitmap(backImage, src, des, paint);

			// 显示剩余牌数
			paint.setStyle(Style.FILL);
			paint.setColor(Color.WHITE);
			paint.setTextSize((int) (20 * MainActivity.SCALE_HORIAONTAL));
			canvas.drawText("" + cards.length, (int) (left * MainActivity.SCALE_HORIAONTAL),
					(int) ((top + 80) * MainActivity.SCALE_VERTICAL), paint);

		}
		else {
			Paint paint = new Paint();
			paint.setStyle(Style.STROKE);
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(1);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			for (int i = 0; i < cards.length; i++) {
				row = CardsManager.getImageRow(cards[i]);
				col = CardsManager.getImageCol(cards[i]);
				cardImage = BitmapFactory.decodeResource(context.getResources(),
						CardImage.cardImages[row][col]);
				int select = 0;
				if (cardsFlag[i]) {
					select = 10;
				}
				src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
				des.set((int) ((left + i * 20) * MainActivity.SCALE_HORIAONTAL),
						(int) ((top - select) * MainActivity.SCALE_VERTICAL),
						(int) ((left + 40 + i * 20) * MainActivity.SCALE_HORIAONTAL), (int) ((top
								- select + 60) * MainActivity.SCALE_VERTICAL));
				RectF rectF = new RectF(des);
				canvas.drawRoundRect(rectF, 5, 5, paint);
				canvas.drawBitmap(cardImage, src, des, paint);

			}
		}

	}
	public void paintResultCards(Canvas canvas) {
		// TODO Auto-generated method stub
		Rect src = new Rect();
		Rect des = new Rect();
		int row;
		int col;

		for (int i = 0; i < cards.length; i++) {
			row = CardsManager.getImageRow(cards[i]);
			col = CardsManager.getImageCol(cards[i]);
			cardImage = BitmapFactory.decodeResource(context.getResources(),
					CardImage.cardImages[row][col]);
			Paint paint = new Paint();
			paint.setStyle(Style.STROKE);
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(1);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			// 当玩家是NPC时，竖向绘制，扑克牌全是背面
			if (paintDirection == CardsType.direction_Vertical) {
				src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
				des.set((int) (left * MainActivity.SCALE_HORIAONTAL),
						(int) ((top - 40 + i * 15) * MainActivity.SCALE_VERTICAL),
						(int) ((left + 40) * MainActivity.SCALE_HORIAONTAL),
						(int) ((top + 20 + i * 15) * MainActivity.SCALE_VERTICAL));
				RectF rectF = new RectF(des);
				canvas.drawRoundRect(rectF, 5, 5, paint);
				canvas.drawBitmap(cardImage, src, des, paint);

			}
			else {
				src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
				des.set((int) ((left + 40 + i * 20) * MainActivity.SCALE_HORIAONTAL),
						(int) (top * MainActivity.SCALE_VERTICAL),
						(int) ((left + 80 + i * 20) * MainActivity.SCALE_HORIAONTAL),
						(int) ((top + 60) * MainActivity.SCALE_VERTICAL));
				RectF rectF = new RectF(des);
				canvas.drawRoundRect(rectF, 5, 5, paint);
				canvas.drawBitmap(cardImage, src, des, paint);

			}
		}
	}

	// 电脑判断出牌的智能
	public CardsHolder chupaiAI(CardsHolder card) {
		int[] pokeWanted = null;

		if (card == null) {
			// 玩家随意出一手牌
			pokeWanted = CardsManager.outCardByItsself(cards, last, next);
		}
		else {
			// 玩家需要出一手比card大的牌
			pokeWanted = CardsManager.findTheRightCard(card, cards, last, next);
		}
		// 如果不能出牌，则返回
		if (pokeWanted == null) {
			return null;
		}
		// 以下为出牌的后续操作，将牌从玩家手中剔除
		for (int i = 0; i < pokeWanted.length; i++) {
			for (int j = 0; j < cards.length; j++) {
				if (cards[j] == pokeWanted[i]) {
					cards[j] = -1;
					break;
				}
			}
		}
		int[] newpokes = new int[0];
		if (cards.length - pokeWanted.length > 0) {
			newpokes = new int[cards.length - pokeWanted.length];
		}
		int j = 0;
		for (int i = 0; i < cards.length; i++) {
			if (cards[i] != -1) {
				newpokes[j] = cards[i];
				j++;
			}
		}
		this.cards = newpokes;
		CardsHolder thiscard = new CardsHolder(pokeWanted, playerId, context);
		// 更新桌子最近一手牌
		Desk.cardsOnDesktop = thiscard;
		this.latestCards = thiscard;
		return thiscard;
	}

	// 非电脑的出牌
	@SuppressLint("ShowToast")
	public CardsHolder chupai(CardsHolder card) {
		int count = 0;
		for (int i = 0; i < cards.length; i++) {
			if (cardsFlag[i]) {
				count++;
				System.out.println("出牌：" + String.valueOf(CardsManager.getCardNumber(cards[i])));
			}
		}
		int[] chupaiPokes = new int[count];
		int j = 0;
		for (int i = 0; i < cards.length; i++) {
			if (cardsFlag[i]) {
				chupaiPokes[j] = cards[i];
				j++;
			}
		}
		int cardType = CardsManager.getType(chupaiPokes);
		System.out.println("cardType:" + cardType);
		if (cardType == CardsType.error) {
			// 出牌错误
			if (chupaiPokes.length != 0) {
				MainActivity.handler.sendEmptyMessage(MainActivity.WRONG_CARD);
			}
			else {
				MainActivity.handler.sendEmptyMessage(MainActivity.EMPTY_CARD);
			}
			return null;
		}
		CardsHolder newLatestCardsHolder = new CardsHolder(chupaiPokes, playerId, context);
		if (card == null) {
			Desk.cardsOnDesktop = newLatestCardsHolder;
			this.latestCards = newLatestCardsHolder;

			int[] newPokes = new int[cards.length - count];
			int k = 0;
			for (int i = 0; i < cards.length; i++) {
				if (!cardsFlag[i]) {
					newPokes[k] = cards[i];
					k++;
				}

			}
			this.cards = newPokes;
			this.cardsFlag = new boolean[cards.length];
		}
		else {

			if (CardsManager.compare(newLatestCardsHolder, card) == 1) {
				Desk.cardsOnDesktop = newLatestCardsHolder;
				this.latestCards = newLatestCardsHolder;

				int[] newPokes = new int[cards.length - count];
				int ni = 0;
				for (int i = 0; i < cards.length; i++) {
					if (!cardsFlag[i]) {
						newPokes[ni] = cards[i];
						ni++;
					}
				}
				this.cards = newPokes;
				this.cardsFlag = new boolean[cards.length];
			}
			if (CardsManager.compare(newLatestCardsHolder, card) == 0) {
				MainActivity.handler.sendEmptyMessage(MainActivity.SMALL_CARD);
				return null;
			}
			if (CardsManager.compare(newLatestCardsHolder, card) == -1) {
				MainActivity.handler.sendEmptyMessage(MainActivity.WRONG_CARD);
				return null;
			}
		}
		return newLatestCardsHolder;
	}

	// 当玩家自己操作时，触摸屏事件的处理
	public void onTuch(int x, int y) {

		for (int i = 0; i < cards.length; i++) {
			// 判断是那张牌被选中，设置标志
			if (i != cards.length - 1) {
				if (CardsManager.inRect(x, y,
						(int) ((left + i * 20) * MainActivity.SCALE_HORIAONTAL),
						(int) ((top - (cardsFlag[i] ? 10 : 0)) * MainActivity.SCALE_VERTICAL),
						(int) (20 * MainActivity.SCALE_HORIAONTAL),
						(int) (60 * MainActivity.SCALE_VERTICAL))) {
					cardsFlag[i] = !cardsFlag[i];
					break;
				}
			}
			else {
				if (CardsManager.inRect(x, y,
						(int) ((left + i * 20) * MainActivity.SCALE_HORIAONTAL),
						(int) ((top - (cardsFlag[i] ? 10 : 0)) * MainActivity.SCALE_VERTICAL),
						(int) (40 * MainActivity.SCALE_HORIAONTAL),
						(int) (60 * MainActivity.SCALE_VERTICAL))) {
					cardsFlag[i] = !cardsFlag[i];
					break;
				}
			}

		}
	}

	public void redo() {
		// TODO Auto-generated method stub
		for (int i = 0; i < cardsFlag.length; i++) {
			cardsFlag[i] = false;
		}
	}

}
