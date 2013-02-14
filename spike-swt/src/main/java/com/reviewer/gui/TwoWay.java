package com.reviewer.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TwoWay {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		Font font = new Font(display, "Consolas", 10, 0);
		
		FillLayout layout = new FillLayout(SWT.HORIZONTAL);
		shell.setLayout(layout);
		
		layout.marginHeight = 5;
	    layout.marginWidth = 5;
	    layout.spacing = 0;		

	    String textLeft = 
	    		"teste0\n" +
	    		"\n" +
	    		"teste1\n" +
	    		"teste3\n";
	    
	    String textRight =
	    		"teste0\n" +
	    		"teste2\n" +
	    		"teste1\n";
	    
		final StyledText left = new StyledText(shell, SWT.BORDER);
		final StyledText right = new StyledText(shell, SWT.BORDER);
		
		left.setText(textLeft);
		left.setFont(font);
		
		right.setText(textRight);
		right.setFont(font);

		left.addLineBackgroundListener(new LineBackgroundListener() {
			public void lineGetBackground(LineBackgroundEvent event) {
				int lineIndex = left.getLineAtOffset(event.lineOffset);
				
				if(lineIndex == 3)
					event.lineBackground = display.getSystemColor(SWT.COLOR_RED);
			}
		});

		right.addLineBackgroundListener(new LineBackgroundListener() {
			public void lineGetBackground(LineBackgroundEvent event) {
				int lineIndex = right.getLineAtOffset(event.lineOffset);
				
				if(lineIndex == 1)
					event.lineBackground = display.getSystemColor(SWT.COLOR_GREEN);
			}
		});
		
		left.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Rectangle bounds = left.getClientArea();
				
				int height = left.getLineHeight();
				int y = height * 1;
				
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
				e.gc.drawRectangle(0, y, bounds.width-1, 1);
			}
		});
		
		right.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Rectangle bounds = left.getClientArea();
				
				int height = left.getLineHeight();
				int y = height * 3;
				
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
				e.gc.drawRectangle(0, y, bounds.width-1, 1);
			}
		});

		shell.setSize(500, 500);
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		font.dispose();
		display.dispose();
	}
}