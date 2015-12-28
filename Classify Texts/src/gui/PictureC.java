package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import cluster.CompareC;
import cluster.Distance;

@SuppressWarnings("serial")
public class PictureC extends JPanel {
	private String path = null;

	public void getPath(String path) {
		this.path = path;
	}

	private void buildMatrixDraw(Map<Integer, List<Distance>> dismap,
			Graphics paper) {
		// 直接覆盖上一次的结果
		Object[] classes = dismap.keySet().toArray();
		int i, j = 0;
		for (i = 0; i < classes.length; i++) {
			String string[] = dismap.get(classes[i]).get(0).getIndex().split(
					".txt");
			// x轴
			paper.setFont(new Font("TimesRoman", Font.BOLD, 10));
			paper.setColor(Color.blue);
			paper.drawString(string[0], 32 + i * 20, 10 * classes.length + 10);
			// y轴
			paper.setFont(new Font("TimesRoman", Font.BOLD, 10));
			paper.setColor(Color.red);
			paper.drawString(string[0], 10, 10 + i * 10);
			// 斜对角线设为“ ”
			paper.drawString(" ", 32 + i * 20, 10 + i * 10);
			for (j = dismap.get(classes[i]).size() - 1; j >= 0; j--) {
				for (int m = 0; m < classes.length; m++) {
					if (dismap.get(classes[m]).get(0).getIndex().equals(
							dismap.get(classes[i]).get(j).getPoint())) {
						BigDecimal bd = new BigDecimal(dismap.get(classes[i])
								.get(j).getValue());
						BigDecimal bd1 = bd.setScale(1, bd.ROUND_HALF_DOWN);
						double temp = bd1.doubleValue();
						// String string2[] =
						// dismap.get(classes[i]).get(j).getPoint().split(".txt");
						if (10 + 10 * m > 10 + 10 * i) {
							paper
									.setFont(new Font("TimesRoman", Font.BOLD,
											10));
							paper.setColor(Color.black);
							paper.drawString(Double.toString(temp),
									32 + i * 20, 10 + 10 * m);
						}
					}
				}
			}
		}
	}
	public Dimension getPreferredSize() {
        return new Dimension(1300, 800);
    }
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		CompareC comparea = new CompareC();
		Map<Integer, List<Distance>> dismap = new HashMap<Integer, List<Distance>>();
		try {
			if (path != null) {
				dismap = comparea.getDisMap(path);
				buildMatrixDraw(dismap, g2);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated catch block

	}

	public String printImage(JPanel panel,Integer ctimes) {
		CompareC comparea = new CompareC();
		String imagepath = new File("").getAbsolutePath()+ "\\print\\" +ctimes+"\\"+ "文章分类.jpg";
		File fileTem = new File(imagepath);
		if (!fileTem.exists()) {
			fileTem.getParentFile().mkdirs();
		}
		System.out.println(imagepath);
		File file = new File(imagepath);   
		BufferedImage bi = null;
		Map<Integer, List<Distance>> dismap = new HashMap<Integer, List<Distance>>();
		try {
			bi = new BufferedImage(panel.getWidth(), panel
					.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = (Graphics2D) bi.getGraphics();
			g2.setBackground(Color.WHITE);
			g2.clearRect(0, 0, panel.getWidth(), panel.getHeight());
			if (path != null) {
				dismap = comparea.getDisMap(path);
				buildMatrixDraw(dismap, g2);
				System.out.println(panel.isValid());
				ImageIO.write(bi, "jpg", file);  
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return imagepath;
	}
}
