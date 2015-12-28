package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.aliasi.tokenizer.EnglishStopTokenizerFactory;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Files;
import com.aliasi.util.ObjectToCounterMap;
import com.aliasi.util.Strings;
import com.sun.jna.Library;
import com.sun.jna.Native;

import classification.ClassifyA;
import classification.ClassifyC;
import cluster.Category;
import cluster.Compare;
import cluster.CompareA;
import cluster.CompareC;
import cluster.Distance;
import cluster.Compare.CLibrary;

public class UserConsole extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// GUI code omitted here...
	// 构建评论，文档，查重按钮
	private JButton assessment;
	private JButton content;
	private JButton duplicity;
	private JButton tokenize;
	// 构建卡片面板
	private JPanel panel;// 主面板
	private JPanel buttonmenu;// 放置按钮的面板
	private CardLayout card;// Cardlayout布局器
	private JPanel a_panel, c_panel, d_panel, t_panel; // 要切换的四个JPanel
	// 创建每个面板必备组件
	private Label matrix[][];
	private JButton adsimilarity;
	private JButton cdsimilarity;
	// 评论面板
	private Integer atimes = 1;
	private Graphics apaper;
	private CardLayout acard;
	private PictureA adisplay_1;
	private int apictureisvalid = 0;
	private JPanel adisplay_2;
	private JPanel adisplay;
	private JPanel amenu;
	private TextField apath;
	private JButton apathbutton;
	private JFileChooser achooser;
	private JButton aclassify;
	private TextArea atextarea;
	private TextArea akeyarea;
	private JLabel akeys;
	private JButton aopen;
	private JButton aprint;
	// 文档分类面板
	private Integer ctimes = 1;// 分类次数
	private Graphics cpaper;
	private CardLayout ccard;
	private PictureC cdisplay_1;
	private int cpictureisvalid = 0;
	private JPanel cdisplay_2;
	private JPanel cdisplay;
	private JPanel cmenu;
	private TextField cpath;
	private JButton cpathbutton;
	private JFileChooser cchooser;
	private JButton cclassify;
	private TextArea ctextarea;
	private TextArea ckeyarea;
	private JLabel ckeys;
	private JButton copen;
	private JButton cprint;
	// 查重面板
	private JPanel dmenu;
	private TextField dpath;
	private JButton dpathbutton;
	private JFileChooser dchooser;
	private JButton checkfile;
	private JButton checknow;
	private TextField input;
	private JTextPane textPane;
	// 分词面板
	private JPanel tmenu;
	private TextField tpath;
	private JButton tpathbutton;
	private JFileChooser tchooser;
	private JPanel tokenization;
	private TextArea ttextarea;
	private JButton lingpipe;
	private JButton ikanalyzer;
	private JButton nlpir;

	public static void main(String[] args) {
		UserConsole user = new UserConsole();
		user.createGUI();

	}

	private void buildMatrixPanel(Map<Integer, List<Distance>> dismap) {

		panel.setLayout(new GridLayout(dismap.size(), dismap.size()));
		matrix = new Label[dismap.size() + 1][dismap.size() + 1];
		Object[] classes = dismap.keySet().toArray();
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < dismap.get(classes[i]).size(); j++) {
				if (i == 0) {
					matrix[i][j] = new Label();
					matrix[i][j].setText(dismap.get(classes[j]).get(j)
							.getIndex());
					matrix[i][j].setForeground(Color.blue);
					panel.add(matrix[i][j]);
				} else {
					matrix[i][j] = new Label();
					BigDecimal bd = new BigDecimal(dismap.get(classes[i])
							.get(j).getValue());
					BigDecimal bd1 = bd.setScale(1, bd.ROUND_HALF_DOWN);
					double temp = bd1.doubleValue();
					matrix[i][j].setText(Double.toString(temp));
					matrix[i][j].setFont(new Font("TimesRoman", Font.BOLD, 10));
					panel.add(matrix[i][j]);

				}

			}
		}
	}

	private void buildAssessment() {
		a_panel.add(new JLabel("评论分类"), BorderLayout.NORTH);
		a_panel.add(amenu, BorderLayout.NORTH);
		a_panel.add(adisplay);
		adsimilarity = new JButton("显示文档相似度");
		adsimilarity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if ((apath.getText().equals("")) == false) {
					File dir = new File(apath.getText());
					if (dir.exists() && dir.isDirectory()) {
						try {
							adisplay_1.revalidate();
							adisplay_1.updateUI();
							adisplay_1.getPath(apath.getText());
							apaper = adisplay_1.getGraphics();
							adisplay_1.paintComponent(apaper);
							apictureisvalid = 1;
							acard.show(adisplay, "ad");
							// buildMatrixPanel(dismap);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(null, "文件夹路径输入错误", "警告",
								JOptionPane.ERROR_MESSAGE);
					}

				} else {
					JOptionPane.showMessageDialog(null, "没有输入文件夹路径", "警告",
							JOptionPane.ERROR_MESSAGE);

				}
			}
		});
		apath = new TextField(50);
		apathbutton = new JButton("选择文件夹");
		apathbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String choosertitle = "选择文件夹";
				achooser = new JFileChooser();
				achooser.setCurrentDirectory(new java.io.File("."));
				achooser.setDialogTitle(choosertitle);
				System.out.println("---" + choosertitle);
				achooser
						.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				achooser.setAcceptAllFileFilterUsed(false);
				if (achooser.showOpenDialog(a_panel) == JFileChooser.APPROVE_OPTION) {
					System.out.println("getCurrentDirectory(): "
							+ achooser.getCurrentDirectory());
					System.out.println("getSelectedFile() : "
							+ achooser.getSelectedFile());
					apath.setText(achooser.getSelectedFile().getAbsolutePath());
				} else {
					System.out.println("No Selection ");
				}

			}
		});
		aclassify = new JButton("开始分类");
		atextarea = new TextArea();
		atextarea.setEditable(false);
		akeyarea = new TextArea();
		akeyarea.setEditable(false);
		akeys = new JLabel("关键词:");
		aopen = new JButton("打开分类文件夹");
		aclassify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if ((apath.getText().equals("")) == false) {
					File dir = new File(apath.getText());
					if (dir.exists() && dir.isDirectory()) {
						acard.show(adisplay, "ac");
						atextarea.setText("");
						atextarea
								.setFont(new Font("TimesRoman", Font.BOLD, 13));
						akeyarea.setText("");
						akeyarea.setFont(new Font("TimesRoman", Font.BOLD, 13));
						adisplay_2.add(aopen);
						adisplay_2.add(atextarea);
						adisplay_2.add(akeys);
						adisplay_2.add(akeyarea);
						Map<Integer, List<String>> nrconn = new HashMap<Integer, List<String>>();
						try {
							ClassifyA ca = new ClassifyA();
							nrconn = ca.getAssessment(apath.getText(), nrconn,
									atimes);
							Object[] classes2 = nrconn.keySet().toArray();
							for (int i = 0; i < classes2.length; i++) {
								atextarea.append("\n");
								atextarea.append("第" + (i + 1) + "类" + ":\n");
								atextarea
										.append("size: "
												+ nrconn.get(classes2[i])
														.size() + "\n");
								for (int j = 0; j < nrconn.get(classes2[i])
										.size(); j++) {
									atextarea.append(nrconn.get(classes2[i])
											.get(j)
											+ "|");
								}
								String parentfile = new File("")
										.getAbsolutePath()
										+ "\\assessments\\"
										+ atimes
										+ "\\"
										+ (i + 1);
								CompareA comparea = new CompareA();
								Set<String> keyset = comparea.extractAKeywords(
										i, parentfile);
								Iterator<String> kit = keyset.iterator();
								// 显示关键词
								akeyarea.append("第" + (i + 1) + "类" + ":\n");
								while (kit.hasNext()) {
									akeyarea.append(kit.next() + "#");
								}
								akeyarea.append("\r\n");
							}
							atextarea.append("\r\n");
							// buildMatrixPanel(dismap);
							atimes = atimes + 1;
							System.out.println("atimes:" + atimes);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(null, "文件夹路径输入错误", "警告",
								JOptionPane.ERROR_MESSAGE);
					}

				} else {
					JOptionPane.showMessageDialog(null, "没有输入文件夹路径", "警告",
							JOptionPane.ERROR_MESSAGE);

				}

			}
		});
		aprint = new JButton("打印并打开");
		aprint.addActionListener(new ActionListener() {
			private AssessmentPDF apdf;
			private int openAssessment = 0;

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if ((apath.getText().equals("")) == false) {
					File dir = new File(apath.getText());
					if (dir.exists() && dir.isDirectory()) {
						try {
							apdf = new AssessmentPDF(adisplay_1, apath
									.getText(), atimes - 1);
							if (apictureisvalid == 1
									&& adisplay_2.countComponents() <= 0) {
								apdf.writeSimilarity();
								openAssessment = 1;
							}
							if (adisplay_2.countComponents() > 0
									&& apictureisvalid == 0) {
								apdf.writeCategory();
								openAssessment = 1;
							}
							if (apictureisvalid == 1
									&& adisplay_2.countComponents() > 0) {
								apdf.writeSimilarity();
								apdf.writeCategory();
								openAssessment = 1;
							}
							if (apictureisvalid == 0
									&& adisplay_2.countComponents() <= 0) {
								JOptionPane.showMessageDialog(null, "没有显示任何部件",
										"警告", JOptionPane.ERROR_MESSAGE);
							}
							if (openAssessment == 1) {
								apdf.openFile();
							}
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(null, "文件夹路径输入错误", "警告",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "没有输入文件夹路径", "警告",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		aopen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String filepath = new File("").getAbsolutePath()
						+ "\\assessments" + "\\" + (atimes - 1);
				try {
					Runtime.getRuntime().exec("explorer " + filepath);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		amenu.add(apath);
		amenu.add(apathbutton);
		amenu.add(adsimilarity);
		amenu.add(aclassify);
		amenu.add(aprint);
	}

	// 文档分类面板
	private void buildContent() {
		c_panel.add(new JLabel("文档聚类"));
		c_panel.add(cmenu, BorderLayout.NORTH);
		c_panel.add(cdisplay);
		cdsimilarity = new JButton("显示文档相似度");
		cdsimilarity.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if ((cpath.getText().equals("")) == false) {
					File dir = new File(cpath.getText());
					if (dir.exists() && dir.isDirectory()) {
						try {

							cdisplay_1.getPath(cpath.getText());
							cpaper = cdisplay_1.getGraphics();
							cdisplay_1.paintComponent(cpaper);
							cpictureisvalid = 1;
							ccard.show(cdisplay, "cd");
							System.out.println(cdisplay_1.isValid());

							// buildMatrixPanel(dismap);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(null, "文件夹路径输入错误", "警告",
								JOptionPane.ERROR_MESSAGE);
					}

				} else {
					JOptionPane.showMessageDialog(null, "没有输入文件夹路径", "警告",
							JOptionPane.ERROR_MESSAGE);

				}
			}
		});
		cpath = new TextField(50);
		cpathbutton = new JButton("选择文件夹");
		cpathbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String choosertitle = "选择文件夹";
				cchooser = new JFileChooser();
				cchooser.setCurrentDirectory(new java.io.File("."));
				cchooser.setDialogTitle(choosertitle);
				System.out.println("---" + choosertitle);
				cchooser
						.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				cchooser.setAcceptAllFileFilterUsed(false);
				if (cchooser.showOpenDialog(c_panel) == JFileChooser.APPROVE_OPTION) {
					System.out.println("getCurrentDirectory(): "
							+ cchooser.getCurrentDirectory());
					System.out.println("getSelectedFile() : "
							+ cchooser.getSelectedFile());
					cpath.setText(cchooser.getSelectedFile().getAbsolutePath());
				} else {
					System.out.println("No Selection ");
				}

			}
		});
		cclassify = new JButton("开始聚类");
		ctextarea = new TextArea();
		ctextarea.setEditable(false);
		ckeys = new JLabel("关键词：");
		ckeyarea = new TextArea();
		ckeyarea.setEditable(false);
		cclassify.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if ((cpath.getText().equals("")) == false) {
					File dir = new File(cpath.getText());
					if (dir.exists() && dir.isDirectory()) {
						ccard.show(cdisplay, "cc");
						ctextarea.setText("");
						ctextarea
								.setFont(new Font("TimesRoman", Font.BOLD, 13));
						ckeyarea.setText("");
						ckeyarea.setFont(new Font("TimesRoman", Font.BOLD, 13));
						cdisplay_2.add(copen);
						cdisplay_2.add(ctextarea);
						cdisplay_2.add(ckeys);
						cdisplay_2.add(ckeyarea);
						Map<Integer, List<String>> nrconn = new HashMap<Integer, List<String>>();
						try {
							ClassifyC cc = new ClassifyC();
							nrconn = cc.getContent(cpath.getText(), nrconn,
									ctimes);
							Object[] classes2 = nrconn.keySet().toArray();
							for (int i = 0; i < classes2.length; i++) {
								ctextarea.append("\n");
								ctextarea.append("第" + (i + 1) + "类" + ":\n");
								ctextarea
										.append("size: "
												+ nrconn.get(classes2[i])
														.size() + "\n");
								for (int j = 0; j < nrconn.get(classes2[i])
										.size(); j++) {
									ctextarea.append(nrconn.get(classes2[i])
											.get(j)
											+ "|");
								}
								String parentfile = new File("")
										.getAbsolutePath()
										+ "\\docs\\" + ctimes + "\\" + (i + 1);
								CompareC comparec = new CompareC();
								Set<String> keyset = comparec.extractAKeywords(
										i, parentfile);
								Iterator<String> kit = keyset.iterator();
								// 显示关键词
								ckeyarea.append("第" + (i + 1) + "类" + ":\n");
								while (kit.hasNext()) {
									ckeyarea.append(kit.next() + "#");
								}
								ckeyarea.append("\r\n");
							}
							ctextarea.append("\r\n");
							ctimes = ctimes + 1;
							System.out.println("ctimes:" + ctimes);
							// buildMatrixPanel(dismap);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(null, "文件夹路径输入错误", "警告",
								JOptionPane.ERROR_MESSAGE);
					}

				} else {
					JOptionPane.showMessageDialog(null, "没有输入文件夹路径", "警告",
							JOptionPane.ERROR_MESSAGE);

				}

			}
		});
		cprint = new JButton("打印并打开");
		cprint.addActionListener(new ActionListener() {
			private ContentPDF cpdf;
			private int openContent = 0;

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if ((cpath.getText().equals("")) == false) {
					File dir = new File(cpath.getText());
					if (dir.exists() && dir.isDirectory()) {
						try {
							cpdf = new ContentPDF(cdisplay_1, cpath.getText(),
									ctimes - 1);
							if (cpictureisvalid == 1
									&& cdisplay_2.countComponents() <= 0) {
								cpdf.writeSimilarity();
								openContent = 1;
							}
							if (cdisplay_2.countComponents() > 0
									&& cpictureisvalid == 0) {
								cpdf.writeCategory();
								openContent = 1;
							}
							if (cpictureisvalid == 1
									&& cdisplay_2.countComponents() > 0) {
								cpdf.writeSimilarity();
								cpdf.writeCategory();
								openContent = 1;
							}
							if (cpictureisvalid == 0
									&& cdisplay_2.countComponents() <= 0) {
								JOptionPane.showMessageDialog(null, "没有显示任何部件",
										"警告", JOptionPane.ERROR_MESSAGE);
							}
							if (openContent == 1) {
								cpdf.openFile();
							}
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(null, "文件夹路径输入错误", "警告",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "没有输入文件夹路径", "警告",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		copen = new JButton("打开分类文件夹");
		copen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String filepath = new File("").getAbsolutePath() + "\\docs"
						+ "\\" + (ctimes - 1);
				try {
					Runtime.getRuntime().exec("explorer " + filepath);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		cmenu.add(cpath);
		cmenu.add(cpathbutton);
		cmenu.add(cdsimilarity);
		cmenu.add(cclassify);
		cmenu.add(cprint);
	}

	// 文档查重面板
	private void buildDuplicity() {
		d_panel.add(new JLabel("文档查重"));
		d_panel.add(dmenu, BorderLayout.NORTH);
		dpath = new TextField(50);
		dpathbutton = new JButton("选择文件");
		dpathbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String choosertitle = "选择文件";
				dchooser = new JFileChooser();
				dchooser.setCurrentDirectory(new java.io.File("."));
				dchooser.setDialogTitle(choosertitle);
				System.out.println("---" + choosertitle);
				dchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				dchooser.setAcceptAllFileFilterUsed(false);
				if (dchooser.showOpenDialog(d_panel) == JFileChooser.APPROVE_OPTION) {
					System.out.println("getCurrentDirectory(): "
							+ dchooser.getCurrentDirectory());
					System.out.println("getSelectedFile() : "
							+ dchooser.getSelectedFile());
					dpath.setText(dchooser.getSelectedFile().getAbsolutePath());
				} else {
					System.out.println("No Selection ");
				}

			}
		});
		checkfile = new JButton("查重");
		checknow = new JButton("开始查重");
		input = new TextField();
		checkfile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if ((dpath.getText().equals("")) == false) {
					File it = new File(dpath.getText());
					if (it.exists()) {
						d_panel.removeAll();
						d_panel.revalidate();
						d_panel.add(new JLabel("文档查重"));
						d_panel.add(dmenu, BorderLayout.NORTH);
						// 通过设置最大大小，限制Box自动分配大小
						input.setMaximumSize(new Dimension(100, 20));
						Box horizontal = Box.createHorizontalBox();
						horizontal.add(new JLabel("请输入查重率"));
						horizontal.add(input);
						horizontal.add(checknow);
						d_panel.add(horizontal);
						textPane = new JTextPane();
						textPane.setEditable(false);
						JScrollPane tj = new JScrollPane(textPane);
						d_panel.add(tj, BorderLayout.CENTER);

						checknow.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								textPane.setText("");
								if ((input.getText().equals("")) == false) {
									String datapath = "C:\\Users\\BOB.BOB-PC\\Desktop\\毕业设计\\毕业程序设计\\test3\\111";
									Compare compare = new Compare();
									File testfile = new File(dpath.getText());
									try {
										List<Distance> comparelist = compare
												.getComparelist(testfile,
														datapath);
										for (int i = 0; i < comparelist.size(); i++) {
											if (Double.parseDouble(input
													.getText()) < 1) {
												if (comparelist.get(i)
														.getValue() >= Double
														.parseDouble(input
																.getText())) {
													insertDocument(
															comparelist.get(i)
																	.getPoint()
																	+ ": "
																	+ Double
																			.toString(comparelist
																					.get(
																							i)
																					.getValue())
																	+ "\r\n",
															Color.RED);
													d_panel.revalidate();
												}
												// else{
												// insertDocument(comparelist.get(i).getPoint()+": "+Double.toString(comparelist.get(i).getValue())+"\r\n",
												// Color.GREEN);
												// d_panel.revalidate();
												//													
												// }
											} else {
												JOptionPane
														.showMessageDialog(
																null,
																"查重率必须小于1，请重新输入",
																"警告",
																JOptionPane.ERROR_MESSAGE);
												break;
											}
										}
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								} else {
									JOptionPane.showMessageDialog(null,
											"查重率没有输入", "警告",
											JOptionPane.ERROR_MESSAGE);
								}
							}
						});

					} else {
						JOptionPane.showMessageDialog(null, "文件路径输入错误", "警告",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "没有输入文件路径", "警告",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		dmenu.add(dpath);
		dmenu.add(dpathbutton);
		dmenu.add(checkfile);
	}

	public void insertDocument(String text, Color textColor)// 根据传入的颜色及文字，将文字插入文本域
	{
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, textColor);// 设置文字颜色
		StyleConstants.setFontSize(set, 12);// 设置字体大小
		Document doc = textPane.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), text, set);// 插入文字
		} catch (BadLocationException e) {
		}
	}

	private void buildTokenize() {
		t_panel.add(new JLabel("文档分词"));
		t_panel.add(tmenu, BorderLayout.NORTH);

		tokenization = new JPanel();
		tokenization.setBackground(Color.white);
		tokenization.setMaximumSize(new Dimension(1300, 30));
		tokenization.setLayout(new FlowLayout());
		tokenization.setVisible(true);
		tokenization.validate();
		t_panel.add(tokenization, BorderLayout.NORTH);
		tpath = new TextField(50);
		tpathbutton = new JButton("选择文件");
		tpathbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String choosertitle = "选择文件";
				tchooser = new JFileChooser();
				tchooser.setCurrentDirectory(new java.io.File("."));
				tchooser.setDialogTitle(choosertitle);
				System.out.println("---" + choosertitle);
				tchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				tchooser.setAcceptAllFileFilterUsed(false);
				if (tchooser.showOpenDialog(d_panel) == JFileChooser.APPROVE_OPTION) {
					System.out.println("getCurrentDirectory(): "
							+ tchooser.getCurrentDirectory());
					System.out.println("getSelectedFile() : "
							+ tchooser.getSelectedFile());
					tpath.setText(tchooser.getSelectedFile().getAbsolutePath());
				} else {
					System.out.println("No Selection ");
				}

			}
		});
		ttextarea = new TextArea();
		ttextarea.setEditable(false);
		t_panel.add(ttextarea);
		ikanalyzer = new JButton("IK Analyzer");
		ikanalyzer.addActionListener(new ActionListener() {
			private ObjectToCounterMap<String> mTokenCounter;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if ((tpath.getText().equals("")) == false) {
					File it = new File(tpath.getText());
					if (it.exists()) {
						ttextarea.setText("");
						File file = new File(tpath.getText());
						String content;
						InputStreamReader isr = null;
						BufferedReader reader = null;
						try {

							isr = new InputStreamReader(new FileInputStream(
									file), "UTF-8");
							reader = new BufferedReader(isr);
							mTokenCounter = new ObjectToCounterMap<String>();
							while ((content = reader.readLine()) != null) {
								StringReader sr = new StringReader(content);
								IKSegmenter ik = new IKSegmenter(sr, true);
								Lexeme lex = null;
								while ((lex = ik.next()) != null) {
									// ttextarea.append(lex.getLexemeText() +
									// "|");
									mTokenCounter
											.increment(lex.getLexemeText());
								}
								// ttextarea.append("\r\n");
							}
							Iterator<String> mtoken = mTokenCounter.keySet()
									.iterator();
							while (mtoken.hasNext()) {
								String temp = mtoken.next();
								ttextarea
										.append(temp + ":"
												+ mTokenCounter.getCount(temp)
												+ "\r\n");
							}

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					} else {
						JOptionPane.showMessageDialog(null, "文件路径输入错误", "警告",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "没有输入文件路径", "警告",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		lingpipe = new JButton("Lingpipe");
		lingpipe.addActionListener(new ActionListener() {
			final TokenizerFactory TOKENIZER_FACTORY = tokenizerFactory();
			private ObjectToCounterMap<String> mTokenCounter;

			TokenizerFactory tokenizerFactory() {
				TokenizerFactory factory = IndoEuropeanTokenizerFactory.INSTANCE;
				factory = new LowerCaseTokenizerFactory(factory);
				factory = new EnglishStopTokenizerFactory(factory);
				factory = new PorterStemmerTokenizerFactory(factory);
				return factory;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if ((tpath.getText().equals("")) == false) {
					File it = new File(tpath.getText());
					if (it.exists()) {
						ttextarea.setText("");
						File file = new File(tpath.getText());
						String content = null;
						InputStreamReader isr = null;
						BufferedReader reader = null;
						try {

							isr = new InputStreamReader(new FileInputStream(
									file), "UTF-8");
							reader = new BufferedReader(isr);
							mTokenCounter = new ObjectToCounterMap<String>();
							while ((content = reader.readLine()) != null) {
								final char[] mText = content.toCharArray();
								Tokenizer tokenizer = TOKENIZER_FACTORY
										.tokenizer(mText, 0, mText.length);
								String token;
								while ((token = tokenizer.nextToken()) != null) {
									// ttextarea.append(token + "|");
									mTokenCounter.increment(token);
								}
								// ttextarea.append("\r\n");
							}
							Iterator<String> mtoken = mTokenCounter.keySet()
									.iterator();
							while (mtoken.hasNext()) {
								String temp = mtoken.next();
								ttextarea
										.append(temp + ":"
												+ mTokenCounter.getCount(temp)
												+ "\r\n");
							}

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(null, "文件路径输入错误", "警告",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "没有输入文件路径", "警告",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		nlpir = new JButton("NLPIR");
		nlpir.addActionListener(new ActionListener() {
			private ObjectToCounterMap<String> mTokenCounter;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if ((tpath.getText().equals("")) == false) {
					File it = new File(tpath.getText());
					if (it.exists()) {
						ttextarea.setText("");
						File file = new File(tpath.getText());
						InputStreamReader isr = null;
						BufferedReader reader = null;
						String content = null;
						try {
							// NLPIR分词
							String argu = "C:\\Users\\BOB.BOB-PC\\Desktop\\JnaTest_NLPIR";
							// String system_charset = "GBK";//GBK----0
							String system_charset = "UTF-8";
							int charset_type = 1;

							int init_flag = CLibrary.Instance.NLPIR_Init(argu,
									charset_type, "0");
							String nativeBytes = null;

							if (0 == init_flag) {
								nativeBytes = CLibrary.Instance
										.NLPIR_GetLastErrorMsg();
								System.err.println("初始化失败！fail reason is "
										+ nativeBytes);
								return;
							}
							isr = new InputStreamReader(new FileInputStream(
									file), "UTF-8");
							reader = new BufferedReader(isr);
							mTokenCounter = new ObjectToCounterMap<String>();
							while ((content = reader.readLine()) != null) {
								nativeBytes = CLibrary.Instance
										.NLPIR_ParagraphProcess(content, 1);
								String[] stemp = nativeBytes.split(" ");
								for (int i = 0; i < stemp.length; i++) {
									String[] subtemp = stemp[i].split("/");
									if (subtemp.length > 1) {
										// ttextarea.append(subtemp[0] + "|");
										mTokenCounter.increment(subtemp[0]);
									}

								}
								// ttextarea.append("\r\n");
							}
							Iterator<String> mtoken = mTokenCounter.keySet()
									.iterator();
							while (mtoken.hasNext()) {
								String temp = mtoken.next();
								ttextarea
										.append(temp + ":"
												+ mTokenCounter.getCount(temp)
												+ "\r\n");
							}

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					} else {
						JOptionPane.showMessageDialog(null, "文件路径输入错误", "警告",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "没有输入文件路径", "警告",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		tokenization.add(ikanalyzer);
		tokenization.add(lingpipe);
		tokenization.add(nlpir);

		tmenu.add(tpath);
		tmenu.add(tpathbutton);
	}

	private void createGUI() {
		try {
			// 将LookAndFeel设置成Windows样式
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		card = new CardLayout(4, 4);// 构建四张卡片
		panel = new JPanel(card); // JPanel的布局管理将被设置成CardLayout
		buttonmenu = new JPanel(); // 构造放按钮的JPanel
		assessment = new JButton("评论分类");
		content = new JButton("文档聚类");
		duplicity = new JButton("文档查重");
		tokenize = new JButton("文档分词");
		// 设置间距
		content.setMaximumSize(new Dimension(300, 150));
		assessment.setMaximumSize(new Dimension(300, 150));
		duplicity.setMaximumSize(new Dimension(300, 150));
		tokenize.setMaximumSize(new Dimension(300, 150));

		content.setMargin(new Insets(2, 2, 2, 2));
		assessment.setMargin(new Insets(2, 2, 2, 2));
		duplicity.setMargin(new Insets(2, 2, 2, 2));
		tokenize.setMargin(new Insets(2, 2, 2, 2));
		// 将按钮添加到面板
		buttonmenu.setPreferredSize(new Dimension(300, 600));
		buttonmenu.setLayout(new BoxLayout(buttonmenu, BoxLayout.Y_AXIS));
		buttonmenu.add(assessment);
		buttonmenu.add(content);
		buttonmenu.add(duplicity);
		buttonmenu.add(tokenize);
		// 建立不同的卡片面板
		a_panel = new JPanel();
		c_panel = new JPanel();
		d_panel = new JPanel();
		t_panel = new JPanel();

		a_panel.setLayout(new BoxLayout(a_panel, BoxLayout.Y_AXIS));
		a_panel.setBackground(Color.WHITE);
		a_panel.setVisible(true);

		c_panel.setBackground(Color.WHITE);
		c_panel.setVisible(true);
		c_panel.setLayout(new BoxLayout(c_panel, BoxLayout.Y_AXIS));

		d_panel.setBackground(Color.WHITE);
		d_panel.setVisible(true);
		d_panel.setLayout(new BoxLayout(d_panel, BoxLayout.Y_AXIS));

		t_panel.setBackground(Color.WHITE);
		t_panel.setVisible(true);
		t_panel.setLayout(new BoxLayout(t_panel, BoxLayout.Y_AXIS));
		// 使用size最大化可以限制BoxLayout对容器大小的变更
		amenu = new JPanel();
		amenu.setBackground(Color.WHITE);
		amenu.setMaximumSize(new Dimension(1300, 30));
		amenu.setLayout(new FlowLayout());
		amenu.setVisible(true);
		amenu.validate();

		acard = new CardLayout(2, 2);
		adisplay = new JPanel(acard);
		adisplay.setBackground(Color.white);
		adisplay.setVisible(true);
		adisplay.validate();

		adisplay_1 = new PictureA();
		adisplay_2 = new JPanel();
		adisplay_1.setBackground(Color.white);
		adisplay_2.setBackground(Color.white);
		adisplay_2.setLayout(new BoxLayout(adisplay_2, BoxLayout.Y_AXIS));

		JScrollPane aj = new JScrollPane(adisplay_1);
		adisplay.add(aj, "ad");
		adisplay.add(adisplay_2, "ac");

		cmenu = new JPanel();
		cmenu.setBackground(Color.WHITE);
		cmenu.setMaximumSize(new Dimension(1300, 30));
		cmenu.setLayout(new FlowLayout());
		cmenu.setVisible(true);
		cmenu.validate();

		ccard = new CardLayout(2, 2);
		cdisplay = new JPanel(ccard);
		cdisplay.setBackground(Color.white);
		cdisplay.setVisible(true);
		cdisplay.validate();

		cdisplay_1 = new PictureC();
		cdisplay_2 = new JPanel();
		cdisplay_1.setBackground(Color.white);
		cdisplay_2.setBackground(Color.white);
		cdisplay_2.setLayout(new BoxLayout(cdisplay_2, BoxLayout.Y_AXIS));

		JScrollPane cj = new JScrollPane(cdisplay_1);
		cdisplay.add(cj, "cd");
		cdisplay.add(cdisplay_2, "cc");

		dmenu = new JPanel();
		dmenu.setBackground(Color.white);
		dmenu.setMaximumSize(new Dimension(1300, 30));
		dmenu.setLayout(new FlowLayout());
		dmenu.setVisible(true);
		dmenu.validate();

		tmenu = new JPanel();
		tmenu.setBackground(Color.white);
		tmenu.setMaximumSize(new Dimension(1300, 30));
		tmenu.setLayout(new FlowLayout());
		tmenu.setVisible(true);
		tmenu.validate();

		buildAssessment();
		buildContent();
		buildDuplicity();
		buildTokenize();

		panel.setMaximumSize(new Dimension(1300, 600));
		panel.add(a_panel, "a_panel");
		panel.add(c_panel, "c_panel");
		panel.add(d_panel, "d_panel");
		panel.add(t_panel, "t_panel");
		/** 下面是翻转到卡片布局的某个组件，可参考API中的文档 */
		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		this.getContentPane().add(buttonmenu);
		this.getContentPane().add(panel);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1300, 600);
		this.setVisible(true);
		this.validate();
		this.setTitle("聚类算法在文档分类中的应用");

		assessment.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				card.show(panel, "a_panel");
			}
		});

		content.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				card.show(panel, "c_panel");

			}
		});
		duplicity.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				card.show(panel, "d_panel");

			}
		});
		tokenize.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				card.show(panel, "t_panel");
			}
		});
	}

	public interface CLibrary extends Library {
		File file = new File("lib\\win64\\NLPTR");
		String path = new File("lib\\win64").getAbsolutePath() + File.separator
				+ "NLPTR.dll";
		// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native
				.loadLibrary(
						"C:\\Users\\BOB.BOB-PC\\Desktop\\JnaTest_NLPIR\\lib\\win64\\NLPIR.dll",
						CLibrary.class);

		public int NLPIR_Init(String sDataPath, int encoding,
				String sLicenceCode);

		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

		public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit,
				boolean bWeightOut);

		public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit,
				boolean bWeightOut);

		public int NLPIR_AddUserWord(String sWord);// add by qp 2008.11.10

		public int NLPIR_DelUsrWord(String sWord);// add by qp 2008.11.10

		public String NLPIR_GetLastErrorMsg();

		public void NLPIR_Exit();
	}
}
