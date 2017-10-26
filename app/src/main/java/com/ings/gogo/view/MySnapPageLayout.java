package com.ings.gogo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MySnapPageLayout extends ViewGroup {
	private final String TAG = "McoySnapPageLayout";
	private final boolean MCOY_DEBUG = true;

	private VelocityTracker mVelocityTracker;
	private int mMaximumVelocity;
	private static final int SNAP_VELOCITY = 1000;

	public static final int FLIP_DIRECTION_CUR = 0;
	public static final int FLIP_DIRECTION_UP = -1;
	public static final int FLIP_DIRECTION_DOWN = 1;

	private int mFlipDrection = FLIP_DIRECTION_CUR;

	private int mDataIndex = 0; // ��ǰView�е�����������������λ��
	private int mCurrentScreen = 0;
	private int mNextDataIndex = 0;

	private float mLastMotionY;
	// ��¼����״̬
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;

	private Scroller mScroller; // mcoy add view������ʸ���� ��û�����������Ĺ���

	private McoySnapPage mPageTop, mPageBottom;

	private PageSnapedListener mPageSnapedListener;

	// ���ֵ��ʾ��Ҫ��һҳ�͵ڶ�ҳ֮��ĺ蹵
	private int gapBetweenTopAndBottom;

	public interface McoySnapPage {
		/**
		 * ����page���ڵ�
		 * 
		 * @return
		 */
		View getRootView();

		/**
		 * �Ƿ񻬶������ �ڶ�ҳ�����Լ�ʵ�ִ˷��������ж��Ƿ��Ѿ��������ڶ�ҳ�Ķ��� �������Ƿ�Ҫ������������һҳ
		 */
		boolean isAtTop();

		/**
		 * �Ƿ񻬶�����ײ� ��һҳ�����Լ�ʵ�ִ˷��������ж��Ƿ��Ѿ��������ڶ�ҳ�ĵײ� �������Ƿ�Ҫ�����������ڶ�ҳ
		 */
		boolean isAtBottom();
	}

	public interface PageSnapedListener {

		/**
		 * @mcoy ����ĳһҳ��������һҳ���ʱ�Ļص�����
		 */
		void onSnapedCompleted(int derection);
	}

	public void setPageSnapListener(PageSnapedListener listener) {
		mPageSnapedListener = listener;
	}

	public MySnapPageLayout(Context context, AttributeSet att) {
		this(context, att, 0);
	}

	public MySnapPageLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViews();
	}

	private void initViews() {
		mScroller = new Scroller(getContext());

		gapBetweenTopAndBottom = ViewConfiguration.get(getContext())
				.getScaledTouchSlop();
		mMaximumVelocity = ViewConfiguration.get(getContext())
				.getScaledMaximumFlingVelocity();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// The children are given the same width and height as the workspace
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			try {
				getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		int childTop = 0;
		int count = getChildCount();
		// DLog.i(TAG, "onLayout mDataIndex = " + mDataIndex);
		// ���ò��֣�������ͼ˳����������
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				final int childHeight = child.getMeasuredHeight();
				childTop = childHeight * i;
				child.layout(0, childTop, childWidth, childTop + childHeight);
			}
		}
		if (count > 0) {
			snapToScreen(mDataIndex);
		}
	}

	/**
	 * ��������ҳ��
	 * 
	 * @param pageTop
	 * @param pageBottom
	 */
	public void setSnapPages(McoySnapPage pageTop, McoySnapPage pageBottom) {
		mPageTop = pageTop;
		mPageBottom = pageBottom;
		addPagesAndRefresh();
	}

	private void addPagesAndRefresh() {
		// ����ҳ��id
		mPageTop.getRootView().setId(0);
		mPageBottom.getRootView().setId(1);
		addView(mPageTop.getRootView());
		addView(mPageBottom.getRootView());
		postInvalidate();
	}

	/**
	 * @mcoy add computeScroll���������postInvalidate()������ ��postInvalidate()������ϵͳ
	 *       �ֻ����computeScroll������ ��˻�һֱ��ѭ��������ã� ѭ�����ս������computeScrollOffset()
	 *       ��computeScrollOffset�����������falseʱ��˵���Ѿ�����������
	 * 
	 *       ��Ҫ��������ʵ�ִ�view�Ĺ����ǵ���scrollTo(mScroller.getCurrX(),
	 *       mScroller.getCurrY());
	 */
	@Override
	public void computeScroll() {
		// ���ж�mScroller�����Ƿ����
		if (mScroller.computeScrollOffset()) {
			if (mScroller.getCurrY() == (mScroller.getFinalY())) {
				if (mNextDataIndex > mDataIndex) {
					mFlipDrection = FLIP_DIRECTION_DOWN;
					makePageToNext(mNextDataIndex);
				} else if (mNextDataIndex < mDataIndex) {
					mFlipDrection = FLIP_DIRECTION_UP;
					makePageToPrev(mNextDataIndex);
				} else {
					mFlipDrection = FLIP_DIRECTION_CUR;
				}
				if (mPageSnapedListener != null) {
					mPageSnapedListener.onSnapedCompleted(mFlipDrection);
				}
			}
			// �������View��scrollTo()���ʵ�ʵĹ���
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			// ������ø÷���������һ���ܿ�������Ч��
			postInvalidate();
		}
	}

	private void makePageToNext(int dataIndex) {
		mDataIndex = dataIndex;
		mCurrentScreen = getCurrentScreen();
	}

	private void makePageToPrev(int dataIndex) {
		mDataIndex = dataIndex;
		mCurrentScreen = getCurrentScreen();
	}

	public int getCurrentScreen() {
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).getId() == mDataIndex) {
				return i;
			}
		}
		return mCurrentScreen;
	}

	public View getCurrentView() {
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).getId() == mDataIndex) {
				return getChildAt(i);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 * ��д�˸����onInterceptTouchEvent()����Ҫ��������onTouchEvent()����֮ǰ����
	 * touch�¼���������down��up��move�¼���
	 * ��onInterceptTouchEvent()����trueʱ����onTouchEvent()��
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			// ��¼y��mLastMotionY��ֵ�ľ���ֵ��
			// yDiff����gapBetweenTopAndBottomʱ����Ϊ�����϶����㹻��ľ��룬��Ļ�Ϳ����ƶ��ˡ�
			final int yDiff = (int) (y - mLastMotionY);
			boolean yMoved = Math.abs(yDiff) > gapBetweenTopAndBottom;
			if (yMoved) {
				if (MCOY_DEBUG) {
					Log.e(TAG, "yDiff is " + yDiff);
					Log.e(TAG,
							"mPageTop.isFlipToBottom() is "
									+ mPageTop.isAtBottom());
					Log.e(TAG, "mCurrentScreen is " + mCurrentScreen);
					Log.e(TAG,
							"mPageBottom.isFlipToTop() is "
									+ mPageBottom.isAtTop());
				}
				if (yDiff < 0 && mPageTop.isAtBottom() && mCurrentScreen == 0
						|| yDiff > 0 && mPageBottom.isAtTop()
						&& mCurrentScreen == 1) {
					Log.e("mcoy", "121212121212121212121212");
					mTouchState = TOUCH_STATE_SCROLLING;
				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
			// Remember location of down touch
			mLastMotionY = y;
			Log.e("mcoy", "mScroller.isFinished() is " + mScroller.isFinished());
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			// Release the drag
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		boolean intercept = mTouchState != TOUCH_STATE_REST;
		Log.e("mcoy", "McoySnapPageLayout---onInterceptTouchEvent return "
				+ intercept);
		return intercept;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 * ��Ҫ�����Ǵ���onInterceptTouchEvent()����ֵΪtrueʱ���ݹ�����touch�¼�
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.e("mcoy", "onTouchEvent--" + System.currentTimeMillis());
		if (!mScroller.isFinished()) {
			return false;
		}
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mTouchState != TOUCH_STATE_SCROLLING) {
				// ��¼y��mLastMotionY��ֵ�ľ���ֵ��
				// yDiff����gapBetweenTopAndBottomʱ����Ϊ�����϶����㹻��ľ��룬��Ļ�Ϳ����ƶ��ˡ�
				final int yDiff = (int) Math.abs(y - mLastMotionY);
				boolean yMoved = yDiff > gapBetweenTopAndBottom;
				if (yMoved) {
					mTouchState = TOUCH_STATE_SCROLLING;
				}
			}
			// ��ָ�϶���Ļ�Ĵ���
			if ((mTouchState == TOUCH_STATE_SCROLLING)) {
				// Scroll to follow the motion event
				final int deltaY = (int) (mLastMotionY - y);
				mLastMotionY = y;
				final int scrollY = getScrollY();
				if (mCurrentScreen == 0) {// ��ʾ��һҳ��ֻ������ʱʹ��
					if (mPageTop != null && mPageTop.isAtBottom()) {
						scrollBy(0, Math.max(-1 * scrollY, deltaY));
					}
				} else {
					if (mPageBottom != null && mPageBottom.isAtTop()) {
						scrollBy(0, deltaY);
					}
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			// ������ָ���л���Ļ�Ĵ���
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityY = (int) velocityTracker.getYVelocity();
				if (Math.abs(velocityY) > SNAP_VELOCITY) {
					if (velocityY > 0 && mCurrentScreen == 1
							&& mPageBottom.isAtTop()) {
						snapToScreen(mDataIndex - 1);
					} else if (velocityY < 0 && mCurrentScreen == 0) {
						snapToScreen(mDataIndex + 1);
					} else {
						snapToScreen(mDataIndex);
					}
				} else {
					snapToDestination();
				}
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			} else {
			}
			mTouchState = TOUCH_STATE_REST;
			break;

		default:
			break;
		}
		return true;
	}

	private void clearOnTouchEvents() {
		mTouchState = TOUCH_STATE_REST;
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	private void snapToDestination() {
		// ����Ӧ��ȥ�ĸ���
		final int flipHeight = getHeight() / 8;

		int whichScreen = -1;
		final int topEdge = getCurrentView().getTop();

		if (topEdge < getScrollY() && (getScrollY() - topEdge) >= flipHeight
				&& mCurrentScreen == 0) {
			// ���»���
			whichScreen = mDataIndex + 1;
		} else if (topEdge > getScrollY()
				&& (topEdge - getScrollY()) >= flipHeight
				&& mCurrentScreen == 1) {
			// ���ϻ���
			whichScreen = mDataIndex - 1;
		} else {
			whichScreen = mDataIndex;
		}
		Log.e(TAG, "snapToDestination mDataIndex = " + mDataIndex);
		Log.e(TAG, "snapToDestination whichScreen = " + whichScreen);
		snapToScreen(whichScreen);
	}

	private void snapToScreen(int dataIndex) {
		if (!mScroller.isFinished())
			return;

		final int direction = dataIndex - mDataIndex;
		mNextDataIndex = dataIndex;
		boolean changingScreens = dataIndex != mDataIndex;
		View focusedChild = getFocusedChild();
		if (focusedChild != null && changingScreens) {
			focusedChild.clearFocus();
		}
		// �������ж��Ƿ��ѵ�Ŀ��λ��~
		int newY = 0;
		switch (direction) {
		case 1: // ��Ҫ�������ڶ�ҳ
			Log.e(TAG, "the direction is 1");
			newY = getCurrentView().getBottom(); // ����ͣ����λ��
			break;
		case -1: // ��Ҫ��������һҳ
			Log.e(TAG, "the direction is -1");
			Log.e(TAG, "getCurrentView().getTop() is "
					+ getCurrentView().getTop() + " getHeight() is "
					+ getHeight());
			newY = getCurrentView().getTop() - getHeight(); // ����ͣ����λ��
			break;
		case 0: // �������벻���� ��˲���ɻ�ҳ���ص�����֮ǰ��λ��
			Log.e(TAG, "the direction is 0");
			newY = getCurrentView().getTop(); // ��һҳ��top��0�� �ڶ�ҳ��topӦ���ǵ�һҳ�ĸ߶�
			break;
		default:
			break;
		}
		final int cy = getScrollY(); // ������λ��
		Log.e(TAG, "the newY is " + newY + " cy is " + cy);
		final int delta = newY - cy; // �����ľ��룬��ֵ������<������ֵ�����һ���>
		mScroller.startScroll(0, cy, 0, delta, Math.abs(delta));
		invalidate();
	}

	public void snapToPrev() {
		if (mCurrentScreen == 1) {
			snapToScreen(0);
		}
	}

	public void snapToNext() {
		if (mCurrentScreen == 0) {
			snapToScreen(1);
		}
	}

	public void snapToCurrent() {
		snapToScreen(mCurrentScreen);
		clearOnTouchEvents();
	}

}
