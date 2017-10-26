package com.ings.gogo.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.ings.gogo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageLoaderTool {

	/**
	 * ����Բ��ͼƬ
	 */
	public static void loadCircleImage(String imgUrl, ImageView imageView) {
		ImageLoadingListener animateFirstListener = (ImageLoadingListener) new AnimateFirstDisplayListener();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.product_loading)
				// ������ڼ����е�ͼƬ
				.showImageForEmptyUri(R.drawable.product_loading)
				// ���õ�����Ϊ�յ�ͼ��
				.showImageOnFail(R.drawable.product_loading)
				.cacheInMemory(true)// ���û���
				.cacheOnDisk(true).considerExifParams(true)//
				.bitmapConfig(Bitmap.Config.RGB_565)// ����bitmapConfigΪBitmap.Config.RGB_565����ΪĬ����ARGB_8888��
													// ʹ��RGB_565���ʹ��ARGB_8888������2�����ڴ�
				.displayer(new RoundedBitmapDisplayer(90)).build();// 90��ʾԲ��
		ImageLoader.getInstance().displayImage(imgUrl, imageView, options,
				animateFirstListener);
	}

	/**
	 * ����ͼƬ�������Լ��ĽǶ�ȥ����
	 */
	public static void loadImageWithAngle(String imgUrl, ImageView imageView,
			int angle) {
		ImageLoadingListener animateFirstListener = (ImageLoadingListener) new AnimateFirstDisplayListener();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)// ������ڼ����е�ͼƬ
				.showImageForEmptyUri(R.drawable.ic_launcher)// ���õ�����Ϊ�յ�ͼ��
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)// ���û���
				.cacheOnDisk(true).considerExifParams(true)//
				.displayer(new RoundedBitmapDisplayer(angle)).build();// 90��ʾԲ��
		ImageLoader.getInstance().displayImage(imgUrl, imageView, options,
				animateFirstListener);
	}

	/**
	 * ����ͼƬ�����Լ��Ķ���ȥ����
	 */
	public static void loadImageWithDisplayImageOptions(String imgUrl,
			ImageView imageView, DisplayImageOptions options) {
		ImageLoadingListener animateFirstListener = (ImageLoadingListener) new AnimateFirstDisplayListener();

		ImageLoader.getInstance().displayImage(imgUrl, imageView, options,
				animateFirstListener);
	}

	/**
	 * ��������Ч��
	 * 
	 * @author Administrator
	 * 
	 */
	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		// ͼƬ����
		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 2000);// ���ö�������ʱ��
					// displayedImages.add(imageUri);
				}
			}
		}
	}
}
