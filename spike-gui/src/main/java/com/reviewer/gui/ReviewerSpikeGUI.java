package com.reviewer.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class ReviewerSpikeGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JLabel jLabel1;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JTextArea textArea1;
	private JTextArea textArea2;

	private final List<String> original = Arrays.asList("if (true) {", "   doSomething();", "}", "doAnotherThing();");
	private final List<String> revised = Arrays.asList("doSomething();", "doAnotherThing();", "oneMore();");

	public String flattedString(List<?> list) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : list) {
			sb.append(obj.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public ReviewerSpikeGUI() throws Exception {
		super("Code Review - Spike");
		initComponents();
	}

	private void hightlightDiff() throws Exception {
		StringBuilder originalSb = new StringBuilder();
		StringBuilder changedSb = new StringBuilder();
		Patch patch = DiffUtils.diff(original, revised);
		List<Delta> deltas = patch.getDeltas();
		int last = 0;

		List<String> newText1 = new ArrayList<String>();
		List<String> newText2 = new ArrayList<String>();

		for (Delta delta : deltas) {
			if (last + 1 < delta.getOriginal().getPosition()) { 
				for (int i = last + 1; i < delta.getOriginal().getPosition(); i++) {
					originalSb.append(original.get(i) + "\n");
					changedSb.append(original.get(i) + "\n");
				}
			}
			List<?> or = delta.getOriginal().getLines();
			originalSb.append(flattedString(or));
			newText1.add(flattedString(or).toString());

			List<?> re = delta.getRevised().getLines();
			changedSb.append(flattedString(re));
			newText2.add(flattedString(re).toString());
			last = delta.getOriginal().last();
		}
		if (last + 1 < original.size()) { // last is not delta
			for (int i = last + 1; i < original.size(); i++) {
				originalSb.append(original.get(i) + "\n");
				changedSb.append(original.get(i) + "\n");
			}
		}

		textArea1.setText(originalSb.toString());
		textArea2.setText(changedSb.toString());

		Highlighter highlighter = textArea1.getHighlighter();
		for (String string : newText1) {
			String text = originalSb.toString();
			int pos = text.indexOf(string);
			highlighter.addHighlight(pos, pos + string.length(),
					new DefaultHighlighter.DefaultHighlightPainter(Color.RED));
		}

		highlighter = textArea2.getHighlighter();

		for (String string : newText2) {
			String text = changedSb.toString();
			int pos = text.indexOf(string);
			highlighter.addHighlight(pos, pos + string.length(),
					new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN));
		}
	}

	private void initComponents() throws Exception {
		jLabel1 = new JLabel("Do your review:");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		textArea1 = new JTextArea();
		textArea1.setColumns(20);
		textArea1.setLineWrap(true);
		textArea1.setRows(5);
		textArea1.setWrapStyleWord(true);
		textArea1.setText(flattedString(original));

		textArea2 = new JTextArea();
		textArea2.setColumns(20);
		textArea2.setLineWrap(true);
		textArea2.setRows(5);
		textArea2.setWrapStyleWord(true);
		textArea2.setText(flattedString(revised));

		hightlightDiff();
		jScrollPane1 = new JScrollPane(textArea1);
		jScrollPane2 = new JScrollPane(textArea2);
		
		textArea1.setEditable(false);
		textArea2.setEditable(false);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		ParallelGroup hGroup = layout
				.createParallelGroup(GroupLayout.Alignment.LEADING);

		SequentialGroup h1 = layout.createSequentialGroup();
		ParallelGroup h2 = layout
				.createParallelGroup(GroupLayout.Alignment.TRAILING);
		
		h2.addComponent(jScrollPane1, GroupLayout.Alignment.LEADING,
				GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE);
		h2.addComponent(jScrollPane2, GroupLayout.Alignment.LEADING,
				GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE);
		h2.addComponent(jLabel1, GroupLayout.Alignment.LEADING,
				GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE);

		h1.addContainerGap();
		h1.addGroup(h2);
		h1.addContainerGap();
		hGroup.addGroup(Alignment.TRAILING, h1);
		layout.setHorizontalGroup(hGroup);

		ParallelGroup vGroup = layout
				.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup v1 = layout.createSequentialGroup();
		v1.addContainerGap();
		v1.addComponent(jLabel1);
		v1.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		v1.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 100,
				Short.MAX_VALUE);
		v1.addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 100,
				Short.MAX_VALUE);
		v1.addContainerGap();
		vGroup.addGroup(v1);
		layout.setVerticalGroup(vGroup);
		pack();
	}

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new ReviewerSpikeGUI().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
